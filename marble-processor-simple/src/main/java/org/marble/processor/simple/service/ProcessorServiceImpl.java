package org.marble.processor.simple.service;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ProcessorServiceImpl implements ProcessorService {

    private static final Logger log = LoggerFactory.getLogger(ProcessorServiceImpl.class);

    public static final String IGNORE_NEUTRAL_SENTENCES = "ignoreNeutralSentences";

    @Autowired
    SenticNetService senticNetService;

    @Override
    public double processMessage(String message, Map<String, Object> options) {

        Boolean ignoreNeutralSentences = Boolean.FALSE;
        // Extract options
        if (options != null) {
            if (options.containsKey(IGNORE_NEUTRAL_SENTENCES)) {
                try {
                    ignoreNeutralSentences = Boolean.parseBoolean((String) options.get(IGNORE_NEUTRAL_SENTENCES));
                } catch (Exception e) {
                    log.warn("Invalid value for " + IGNORE_NEUTRAL_SENTENCES + " property.", e);
                }
            }
        }

        String processedText = message;

        String[] sentences = processedText.split("[\\.,;!?]");

        log.trace("Splitted text: " + Arrays.toString(sentences));

        double polarity = 0D;
        Integer count = 0;
        for (String sentence : sentences) {
            // Split sentences into words
            // TODO Do this using ntlk alternative (PoS tool)
            String words[] = sentence.trim().split(" ");
            Float subpolarity = this.calculateSentencePolarity(words);
            if (subpolarity != 0) {
                count++;
            }
            polarity += subpolarity;
            log.debug("Result for sentence <" + sentence + "> :" + subpolarity + ";");
        }
        if (ignoreNeutralSentences) {
            if (count == 0) {
                return 0;
            } else {
                return polarity / count;
            }
        } else {
            return polarity / sentences.length;
        }
    }

    private Float calculateSentencePolarity(String words[]) {

        Float polarity = 0f;

        Integer j = -1;
        for (Integer i = 0; i < words.length; i++) {
            if (i <= j) {
                continue;
            }
            j = Math.min(i + 3, words.length);
            Float results = null;
            String phrase = "";

            if (i + 3 < words.length) {
                phrase = words[i] + " " + words[i + 1] + " " + words[i + 2] + " " + words[i + 3];
                results = senticNetService.getPolarity(phrase);
                log.trace("Trying with four words: " + phrase);
            }
            if (results == null && (i + 2 < words.length)) {
                j--;
                phrase = words[i] + " " + words[i + 1] + " " + words[i + 2];
                results = senticNetService.getPolarity(phrase);
                log.trace("Trying with three words: " + phrase);
            }
            if (results == null && (i + 1 < words.length)) {
                j--;
                phrase = words[i] + " " + words[i + 1];
                results = senticNetService.getPolarity(phrase);
                log.trace("Trying with two words: " + phrase);
            }
            if (results == null) {
                j--;
                phrase = words[i];
                results = senticNetService.getPolarity(phrase);
                log.trace("Trying with one word: " + phrase);
            }

            if (results != null) {
                // Added no/not modifier (TODO Expand lists of modifiers)
                if (i > 0 && words[i - 1] != null && words[i - 1].matches("no[t]?")) {
                    results = (-1) * results;
                }

                polarity += results;
                log.trace("Result for this group: " + results);
            } else {
                log.trace("No results found for this group.");
            }
        }

        return polarity;

    }

}