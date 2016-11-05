package org.marble.commons.service;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

public interface ValidationDataService {
    public void insertDataFromFile(MultipartFile file) throws IllegalStateException, IOException, SAXException, ParserConfigurationException;
}
