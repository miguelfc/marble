package org.marble.preprocessor.simple.service;

import java.util.Arrays;
import org.marble.model.domain.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class PreprocessorServiceImpl implements PreprocessorService {

    private static final Logger log = LoggerFactory.getLogger(PreprocessorServiceImpl.class);

    @Override
    public String preprocessMessage(String message) {

        String processedText = message;
        log.trace("Original text to prepocess: " + message);
        // Clean up symbols
        processedText = processedText.replaceAll(Constants.URL_PATTERN, "_URL_");
        // Replace "separator symbols
        processedText = processedText.replace("\n", " ").replace("\r", "");
        processedText = processedText.replaceAll("([\\(\\)\"-])", " ");
        processedText = processedText.replaceAll("\\.+", ".");
        processedText = processedText.replaceAll("[ \t]+", " ");
        // lowercase everything
        processedText = processedText.toLowerCase();
        log.trace("Cleaned-up text: " + processedText);

        return processedText;
    }

}