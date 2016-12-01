package org.marble.processor.stanford.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.marble.processor.stanford.exception.InvalidMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

@Service
public class ProcessorServiceImpl implements ProcessorService {

    private static final Logger log = LoggerFactory.getLogger(ProcessorServiceImpl.class);

    public static final String IGNORE_NEUTRAL_SENTENCES = "ignoreNeutralSentences";
    public static final String RELATED_WORDS = "relatedWords";

    @Autowired
    SentiWordNetService sentiWordNetService;

    @SuppressWarnings("unchecked")
    @Override
    public double processMessage(String message, Map<String, Object> options) throws InvalidMessageException {

        Properties props = new Properties();

        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        List<String> relatedWords = new ArrayList<>();
        // Extract options
        if (options != null) {
            if (options.containsKey(RELATED_WORDS)) {
                try {
                    relatedWords = ((List<String>) options.get(RELATED_WORDS));
                } catch (Exception e) {
                    log.warn("Invalid value for " + RELATED_WORDS + " property.", e);
                }
            }
        }

        String processedText = message;

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(processedText);

        // run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory grammaticalStructureFactory = tlp.grammaticalStructureFactory();

        // Flag to check if post is allowed
        Boolean isAllowed = Boolean.FALSE;
        Double polarity = 0D;

        for (CoreMap sentence : sentences) {
            log.trace("Sentence:   " + sentence);

            if (relatedWords.size() == 0) {
                isAllowed = Boolean.TRUE;
                break;
            } else {
                // this is the parse tree of the current sentence
                Tree tree = sentence.get(TreeAnnotation.class);
                GrammaticalStructure structure = grammaticalStructureFactory.newGrammaticalStructure(tree);
                Iterator<TypedDependency> iter = structure.typedDependenciesCollapsedTree().iterator();
                while (iter.hasNext()) {
                    TypedDependency typedDependency = iter.next();
                    String dependency = typedDependency.reln().toString();
                    if (dependency.matches("nsubj|amod|dobj")) {
                        // log.error("<" + typedDependency.gov().originalText() + ">:<" + typedDependency.dep().originalText() + ">");
                        if (relatedWords.size() == 0) {
                            isAllowed = Boolean.TRUE;
                            break;
                        } else if (relatedWords.contains(typedDependency.gov().originalText()) || relatedWords.contains(typedDependency.dep().originalText())) {
                            // log.error("Sentence Allowed: ");
                            // log.error(typedDependency.gov().toString() + "\t->\t" + typedDependency.dep().toString() + "\t->\t" +
                            // dependency);
                            isAllowed = Boolean.TRUE;
                            break;
                        }
                    }
                }
            }

            // In their paper, they make no distinction between sentences inside the tweet.
            if (isAllowed) {
                // We don't need to check anymore
                break;
            }
        }

        if (isAllowed) {
            List<ComplexWords> words = new LinkedList<>();
            for (CoreMap sentence : sentences) {
                log.trace("Allowed Sentence:   " + sentence);
                // Get the associated POS tags
                for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                    // this is the text of the token
                    String word = token.get(TextAnnotation.class);
                    // this is the POS tag of the token
                    String pos = token.get(PartOfSpeechAnnotation.class);
                    String tag = this.mapPennTreebankTagToSentiWordNet(pos);
                    if (tag != null) {
                        words.add(new ComplexWords(word, tag));
                    }
                }
            }
            polarity = calculateSentencePolarity(words);

        } else {
            throw new InvalidMessageException("Message not allowed.");
        }

        return polarity;
    }

    public Double calculateSentencePolarity(List<ComplexWords> words) {

        Double polarity = 0D;

        for (ComplexWords word : words) {
            Double wordPolarity = sentiWordNetService.getPolarity(word.word, word.pos);
            if (wordPolarity != null) {
                polarity += wordPolarity;
                log.debug("Pol: <" + word.word + ">:<" + word.pos + "> --> " + wordPolarity);
            }
        }

        return polarity;

    }

    private String mapPennTreebankTagToSentiWordNet(String tag) {
        String sentiWordNetTag = null;
        if (tag.matches("VB|VBD|VBG|VBN|VBP|VBZ")) {
            sentiWordNetTag = "v";
        } else if (tag.matches("JJ|JJR|JJS")) {
            sentiWordNetTag = "a";
        } else if (tag.matches("NN|NNS|NNP|NNPS|PRP|PRP$|WP|WP$")) {
            sentiWordNetTag = "n";
        } else if (tag.matches("RB|RBR|RBS|WRB")) {
            sentiWordNetTag = "r";
        }
        return sentiWordNetTag;
    }

    private class ComplexWords {
        String word;
        String pos;

        public ComplexWords(String word, String pos) {
            this.word = word;
            this.pos = pos;
        }
    }

}