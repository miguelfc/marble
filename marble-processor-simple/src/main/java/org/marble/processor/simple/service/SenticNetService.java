package org.marble.processor.simple.service;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

public interface SenticNetService {
    Float getPolarity(String sentence);

    void insertDataFromFile(InputStream inputStream) throws IllegalStateException, IOException, SAXException, ParserConfigurationException;
}
