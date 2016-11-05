package org.marble.commons.service;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

public interface SentiWordNetService {
    public void insertDataFromFile(MultipartFile file) throws IllegalStateException, IOException, SAXException, ParserConfigurationException;
    Double getPolarity(String sentence, String pos);
}
