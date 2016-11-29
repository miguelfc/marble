package org.marble.commons.executor.processor;
/*
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.marble.commons.domain.model.Job;
import org.marble.commons.domain.model.Post;
import org.marble.commons.domain.model.ProcessedPost;
import org.marble.commons.domain.model.Topic;
import org.marble.commons.domain.model.ValidationItem;
import org.marble.commons.exception.InvalidExecutionException;
import org.marble.commons.exception.InvalidPostException;
import org.marble.commons.model.Constants;
import org.marble.commons.model.ExecutorParameter;
import org.marble.commons.model.JobStatus;
import org.marble.commons.model.SymplifiedProcessingItem;
import org.marble.commons.model.ValidationResult;
import org.marble.commons.service.DatastoreService;
import org.marble.commons.service.JobService;
import org.marble.commons.service.ProcessedPostService;
import org.marble.commons.service.SentiWordNetService;
import org.marble.commons.service.SenticNetService;
import org.marble.commons.service.TopicService;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

//@Component
//@Scope("prototype")
@Deprecated
public class AnwarEkramProcessorExecutor implements ProcessorExecutor {

    private static final Logger log = LoggerFactory.getLogger(AnwarEkramProcessorExecutor.class);

    public static final String id = AnwarEkramProcessorExecutor.class.getSimpleName();

    public static final String label = "Anwar Ekram Processor";

    public static final List<ExecutorParameter> operations;

    public static final List<ExecutorParameter> parameters;

    static {
        List<ExecutorParameter> availableOperations = new ArrayList<>();
        availableOperations.add(new ExecutorParameter("basicProcessor", "Basic Processor."));
        operations = Collections.unmodifiableList(availableOperations);
    }

    static {

        List<ExecutorParameter> availableParameters = new ArrayList<>();
        availableParameters.add(new ExecutorParameter("relatedWords", "Significant Words.", "Keywords used to filter, separated by ','", "Example: iphone,camera,apple"));
        parameters = Collections.unmodifiableList(availableParameters);
    }

    private enum OperationType {
        REGULAR
    }

    @Autowired
    JobService executionService;

    @Autowired
    TopicService topicService;

    @Autowired
    DatastoreService datastoreService;

    @Autowired
    ProcessedPostService processedStatusService;

    @Autowired
    SentiWordNetService sentiWordNetService;

    private Job execution;

    // Local usage
    private List<String> relatedWords = new ArrayList<>();

    // Custom parameters
    private Boolean ignoreNeutralSentences = Boolean.FALSE;

    @Override
    public void setExecution(Job execution) {
        this.execution = execution;
        if (this.execution.getModuleParameters() != null &&
                this.execution.getModuleParameters().getParameters() != null &&
                this.execution.getModuleParameters().getParameters().get("relatedWords") != null) {
            this.relatedWords = Arrays.asList(this.execution.getModuleParameters().getParameters().get("relatedWords").split("[,;]"));
        }
    }

    @Override
    public void run() {
        String msg = "";
        try {
            log.info("Initializing execution...");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        try {

            BigInteger id = execution.getId();

            msg = "Starting Anwar Ekram processor <" + id + ">.";
            log.info(msg);
            execution.appendLog(msg);

            // Changing execution state
            execution.setStatus(JobStatus.Running);
            execution = executionService.save(execution);

            OperationType operationType = null;
            switch (this.execution.getModuleParameters().getOperation()) {
            case "basicProcessor":
                operationType = OperationType.REGULAR;
                break;
            }
            if (operationType != null) {
                if (execution.getTopic() != null) {
                    process(operationType);
                } else {
                    validate(operationType);
                }
            }

        } catch (Exception e) {
            msg = "An error ocurred while processing statuses with execution <" + execution.getId() + ">. Execution aborted.";
            log.error(msg, e);
            execution.appendLog(msg);
            execution.setStatus(JobStatus.Aborted);
            try {
                execution = executionService.save(execution);
            } catch (InvalidExecutionException e1) {
                log.error("Post couldn't be refreshed on the execution object.");
            }

            return;
        }
    }

    private void process(OperationType operationType) throws InvalidExecutionException {

        // TODO Include ignore neutral sentences from global configuration
        this.ignoreNeutralSentences = Boolean.FALSE;

        // Get the associated topic
        Topic topic = execution.getTopic();

        String msg = "";

        DBCursor processingItemsCursor;

        // This is a regular process

        // Drop current processed statuses
        processedStatusService.deleteByTopicName(topic.getName());

        // Get original Statuses
        log.info("Getting statuses for topic <" + topic.getName() + ">.");
        processingItemsCursor = datastoreService.findCursorByTopicName(topic.getName(), Post.class);

        log.info("There are <" + processingItemsCursor.count() + "> items to process.");
        Integer count = 0;

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        //TODO Chequear esto que va mas rapido
        //MaxentTagger tagger = new MaxentTagger();
        
        while (processingItemsCursor.hasNext()) {

            DBObject rawStatus = processingItemsCursor.next();

            Post status = datastoreService.getConverter().read(Post.class, rawStatus);
            SymplifiedProcessingItem symplifiedProcessingItem = status.getSymplifiedProcessingItem();

            log.debug("Item text is <" + symplifiedProcessingItem.getText() + ">");
            if (symplifiedProcessingItem.getCreatedAt() == null) {
                continue;
            }
            String text = symplifiedProcessingItem.getText();
            if (text == null) {
                log.debug("Post text for id <" + symplifiedProcessingItem.getId() + "> is null. Skipping...");
                continue;
            }
            log.debug("Analysing text: " + text.replaceAll("\n", ""));

            Double polarity = 0D;
            try {
                polarity = this.processStatus(text, pipeline);
            } catch (InvalidPostException e) {
                // This post is not going to be processed as it doesn't comply with the related words.
                continue;
            }

            log.debug("Polarity for text <" + text.replaceAll("\n", "") + "> is <" + polarity + ">");

            // If this is a regular process, results will be save to the
            // database
            ProcessedPost processedStatus = new ProcessedPost(status);
            // TODO processedStatus.setPolarity(polarity);
            datastoreService.save(processedStatus);

            count++;

            // TODO REMOVE
            if (count > 10) {
                // return;
            }

            if ((count % 40) == 0) {
                msg = "Items processed so far: <" + count + ">";
                log.info(msg);
                execution.appendLog(msg);
                executionService.save(execution);
            }
        }

        msg = "Total of items processed: <" + count + ">";
        log.info(msg);
        execution.appendLog(msg);

        msg = "The bag of words sentic processor operation for topic <" + topic.getName() + "> has finished.";

        log.info(msg);
        execution.appendLog(msg);
        execution.setStatus(JobStatus.Stopped);

        execution = executionService.save(execution);
    }

    public void validate(OperationType operationType) throws InvalidExecutionException {
        // TODO Include ignore neutral sentences from global configuration
        this.ignoreNeutralSentences = Boolean.FALSE;

        String msg = "";

        ValidationResult validationResult = new ValidationResult();

        // Define Boundaries
        Float positiveBoundary, negativeBoundary;
        try {
            positiveBoundary = Float.parseFloat(this.execution.getModuleParameters().getParameters().get("positiveBoundary"));
        } catch (Exception e) {
            positiveBoundary = 0F;
            this.execution.getModuleParameters().getParameters().put("positiveBoundary", positiveBoundary.toString());
        }
        try {
            negativeBoundary = Float.parseFloat(this.execution.getModuleParameters().getParameters().get("negativeBoundary"));
        } catch (Exception e) {
            negativeBoundary = 0F;
            this.execution.getModuleParameters().getParameters().put("negativeBoundary", negativeBoundary.toString());
        }

        DBCursor processingItemsCursor;

        // This is a validation
        processingItemsCursor = datastoreService.findCursorForAll(ValidationItem.class);

        log.info("There are <" + processingItemsCursor.count() + "> items to validate.");

        Integer count = 0;
        
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        while (processingItemsCursor.hasNext()) {

            DBObject rawStatus = processingItemsCursor.next();

            ValidationItem item = datastoreService.getConverter().read(ValidationItem.class, rawStatus);
            SymplifiedProcessingItem symplifiedProcessingItem = item.getSymplifiedProcessingItem();

            log.debug("Item text is <" + symplifiedProcessingItem.getText() + ">");
            if (symplifiedProcessingItem.getCreatedAt() == null) {
                continue;
            }
            String text = symplifiedProcessingItem.getText();
            if (text == null) {
                log.debug("Post text for id <" + symplifiedProcessingItem.getId() + "> is null. Skipping...");
                continue;
            }
            log.debug("Analysing text: " + text.replaceAll("\n", ""));

            Double polarity = 0D;
            try {
                polarity = this.processStatus(text, pipeline);
            } catch (InvalidPostException e) {
                // This post is not going to be processed as it doesn't comply with the related words.
                continue;
            }

            log.debug("Polarity for text <" + text.replaceAll("\n", "") + "> is <" + polarity + ">");

            // In case of validation, results will be shown in screen and in
            // counters
            if (polarity > positiveBoundary) {
                validationResult.addPositiveResult(symplifiedProcessingItem.getExpectedPolarity());
            } else if (polarity < negativeBoundary) {
                validationResult.addNegativeResult(symplifiedProcessingItem.getExpectedPolarity());
            } else {
                validationResult.addNeutralResult(symplifiedProcessingItem.getExpectedPolarity());
            }
            msg = "Polarity: " + polarity + ". Expected: " + symplifiedProcessingItem.getExpectedPolarity() + ".";
            log.debug(msg);

            count++;

            if ((count % 100) == 0) {
                msg = "Items processed so far: <" + count + ">";
                log.info(msg);
                execution.appendLog(msg);
                executionService.save(execution);
            }
        }

        msg = "Total of items processed: <" + count + ">";
        log.info(msg);
        execution.appendLog(msg);

        msg = validationResult.getResults();
        log.info(msg);
        execution.appendLog(msg);
        msg = "The Anwar Ekram sentic processor validation has finished.";

        log.info(msg);
        execution.appendLog(msg);
        execution.setStatus(JobStatus.Stopped);

        execution = executionService.save(execution);

    }

    public Boolean getIgnoreNeutralSentences() {
        return ignoreNeutralSentences;
    }

    public void setIgnoreNeutralSentences(Boolean ignoreNeutralSentences) {
        this.ignoreNeutralSentences = ignoreNeutralSentences;
    }

    public Double calculateSentencePolarity(List<ComplexWords> words) {

        Double polarity = 0D;

        Integer j = -1;
        for (ComplexWords word : words) {
            Double wordPolarity = sentiWordNetService.getPolarity(word.word, word.pos);
            if (wordPolarity != null) {
                polarity += wordPolarity;
                log.debug("Pol: <" + word.word + ">:<" + word.pos + "> --> " + wordPolarity);
            }
        }

        return polarity;

    }

    public Double processStatus(String originalText, StanfordCoreNLP pipeline) throws InvalidPostException {

        String processedText = originalText;
        log.debug("Original text to process: " + originalText);

        // Remove URLs
        processedText = processedText.replaceAll(Constants.URL_PATTERN, "_URL_");
        // Remove all non alphanumeric characters
        processedText = processedText.replaceAll("[^a-zA-Z0-9\\s\\.;,\\-\\_\\|]+", "");
        // Clean up symbols
        processedText = processedText.replaceAll("[\\|]+", ". ");
        processedText = processedText.replaceAll("[ \t\n]+", " ");
        // lowercase everything
        processedText = processedText.toLowerCase();
        // Split into sentences
        log.debug("Cleaned-up text: " + processedText);

        // Test Area

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
            throw new InvalidPostException("Post not allowed.");
        }

        // End of Test Area

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
*/