package org.marble.commons.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.opencsv.CSVReader;

import org.marble.commons.domain.model.SenticItem;
import org.marble.commons.domain.model.ValidationItem;
import org.marble.commons.util.MarbleUtil;

@Service
public class ValidationDataServiceImpl implements ValidationDataService {

    private static final Logger log = LoggerFactory.getLogger(ValidationDataServiceImpl.class);

    @Autowired
    DatastoreService datastoreService;

    @Override
    public void insertDataFromFile(MultipartFile file) throws IllegalStateException, IOException, SAXException,
            ParserConfigurationException {
        log.info("Processing uploaded validation data from file <" + file.getOriginalFilename() + ">");
        insertDataFromFile(MarbleUtil.multipartToFile(file));

    }

    public void insertDataFromFile(File file) throws IllegalStateException, IOException, SAXException,
            ParserConfigurationException {
        datastoreService.removeCollection(ValidationItem.class);
        File validationFile = file;
        try (CSVReader reader = new CSVReader(new FileReader(validationFile), ';', '"')) {
            String[] nextLine;
            Long rowIndex = 0L;
            while ((nextLine = reader.readNext()) != null) {
                ValidationItem item = new ValidationItem();
                if (nextLine.length >= 2) {
                    item.setId(rowIndex);
                    item.setPolarity(Integer.parseInt(nextLine[0]));
                    item.setText(nextLine[1]);
                    datastoreService.save(item);
                    rowIndex++;
                }
            }
        }
    }

}
