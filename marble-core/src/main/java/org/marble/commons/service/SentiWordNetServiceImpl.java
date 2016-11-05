package org.marble.commons.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import org.marble.commons.domain.model.SentiWordNetItem;
import org.marble.commons.util.MarbleUtil;

@Service
public class SentiWordNetServiceImpl implements SentiWordNetService {

    private static final Logger log = LoggerFactory.getLogger(SentiWordNetServiceImpl.class);

    @Autowired
    DatastoreService datastoreService;

    Map<String, Double> data = new HashMap<>();

    @Override
    public void insertDataFromFile(MultipartFile file) throws IllegalStateException, IOException, SAXException,
            ParserConfigurationException {
        log.info("Processing uploaded sentiWordNet information from file <" + file.getOriginalFilename() + ">");
        insertDataFromFile(MarbleUtil.multipartToFile(file));
    }

    public void insertDataFromFile(File file) throws SAXException, IOException, ParserConfigurationException {

        File csvFile = file;

        datastoreService.removeCollection(SentiWordNetItem.class);

        // From String to list of doubles.
        HashMap<String, HashMap<Integer, Double>> tempDictionary = new HashMap<String, HashMap<Integer, Double>>();

        // The following block was extracted from http://sentiwordnet.isti.cnr.it/code/SentiWordNetDemoCode.java
        BufferedReader csv = null;
        Integer counter = 0;
        try {
            csv = new BufferedReader(new FileReader(csvFile));
            int lineNumber = 0;

            String line;
            while ((line = csv.readLine()) != null) {
                lineNumber++;

                // If it's a comment, skip this line.
                if (!line.trim().startsWith("#")) {
                    // We use tab separation
                    String[] data = line.split("\t");
                    String wordTypeMarker = data[0];

                    // Example line:
                    // POS ID PosS NegS SynsetTerm#sensenumber Desc
                    // a 00009618 0.5 0.25 spartan#4 austere#3 ascetical#2
                    // ascetic#2 practicing great self-denial;...etc

                    // Is it a valid line? Otherwise, through exception.
                    if (data.length != 6) {
                        throw new IllegalArgumentException(
                                "Incorrect tabulation format in file, line: "
                                        + lineNumber);
                    }

                    // Calculate synset score as score = PosS - NegS
                    Double synsetScore = Double.parseDouble(data[2])
                            - Double.parseDouble(data[3]);

                    // Get all Synset terms
                    String[] synTermsSplit = data[4].split(" ");

                    // Go through all terms of current synset.
                    for (String synTermSplit : synTermsSplit) {
                        // Get synterm and synterm rank
                        String[] synTermAndRank = synTermSplit.split("#");
                        String synTerm = synTermAndRank[0] + "#"
                                + wordTypeMarker;

                        int synTermRank = Integer.parseInt(synTermAndRank[1]);
                        // What we get here is a map of the type:
                        // term -> {score of synset#1, score of synset#2...}

                        // Add map to term if it doesn't have one
                        if (!tempDictionary.containsKey(synTerm)) {
                            tempDictionary.put(synTerm,
                                    new HashMap<Integer, Double>());
                        }

                        // Add synset link to synterm
                        tempDictionary.get(synTerm).put(synTermRank,
                                synsetScore);
                    }
                    
                    if ((lineNumber % 5000) == 0) {
                        log.trace("<" + lineNumber + "> lines processed...");
                    }
                }
            }

            // Go through all the terms.
            for (Map.Entry<String, HashMap<Integer, Double>> entry : tempDictionary
                    .entrySet()) {
                String word = entry.getKey();
                Map<Integer, Double> synSetScoreMap = entry.getValue();

                // Calculate weighted average. Weigh the synsets according to
                // their rank.
                // Score= 1/2*first + 1/3*second + 1/4*third ..... etc.
                // Sum = 1/1 + 1/2 + 1/3 ...
                double score = 0.0;
                double sum = 0.0;
                for (Map.Entry<Integer, Double> setScore : synSetScoreMap
                        .entrySet()) {
                    score += setScore.getValue() / (double) setScore.getKey();
                    sum += 1.0 / (double) setScore.getKey();
                }
                score /= sum;
                datastoreService.save(new SentiWordNetItem(word, score));
                counter++;
                if ((counter % 5000) == 0) {
                    log.debug("<" + counter + "> items processed...");
                }
            }
        } catch (Exception e) {
            log.error("An error occurred while loading the SentiWordNet items", e);
            if (csv != null) {
                csv.close();
            }
            throw e;
        } finally {
            if (csv != null) {
                csv.close();
            }
        }

        this.getDataToMemory();
        log.info("SentiWordNet info updated. There are <" + counter + "> items available.");

    }

    @PostConstruct
    public void getDataToMemory() {
        // Load all the fields here
        log.debug("Loading SentiWordNet data into memory...");
        this.data = new HashMap<>();
        List<SentiWordNetItem> list = datastoreService.findAll(SentiWordNetItem.class);

        for (SentiWordNetItem item : list) {
            this.data.put(item.getId(), item.getPolarity());
        }
    }

    @Override
    public Double getPolarity(String sentence, String pos) {
        return this.data.get(sentence + "#" + pos);
    }

}
