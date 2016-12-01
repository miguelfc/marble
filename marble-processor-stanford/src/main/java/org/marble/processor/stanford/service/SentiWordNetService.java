package org.marble.processor.stanford.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

public interface SentiWordNetService {
    public void insertDataFromFile(InputStream inputStream) throws IllegalStateException, IOException, SAXException, ParserConfigurationException;
    Double getPolarity(String sentence, String pos);
}
