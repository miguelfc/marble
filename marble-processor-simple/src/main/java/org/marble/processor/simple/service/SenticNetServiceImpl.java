package org.marble.processor.simple.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.marble.processor.simple.domain.model.SenticItem;

@Service
public class SenticNetServiceImpl implements SenticNetService {

    private static final Logger log = LoggerFactory.getLogger(SenticNetServiceImpl.class);

    @Autowired
    DatastoreService datastoreService;

    Map<String, Float> data = new HashMap<>();

    @Override
    public void insertDataFromFile(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
        log.info("Processing uploaded sentic information from file...");

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(inputStream);
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("rdf:Description");

        if (nodeList.getLength() <= 0) {
            throw new ParserConfigurationException("No elements present in the xml file.");
        }

        datastoreService.removeCollection(SenticItem.class);

        int temp = 0;
        for (temp = 0; temp < nodeList.getLength(); temp++) {
            Node node = nodeList.item(temp);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element xmlItem = (Element) node;

                // log.info("...");
                // log.info("text: " +
                // xmlItem.getElementsByTagName("text").item(0).getTextContent());
                // log.info("pleasantness: " +
                // xmlItem.getElementsByTagName("pleasantness").item(0).getTextContent());
                // log.info("attention: " +
                // xmlItem.getElementsByTagName("attention").item(0).getTextContent());
                // log.info("sensitivity: " +
                // xmlItem.getElementsByTagName("sensitivity").item(0).getTextContent());
                // log.info("aptitude: " +
                // xmlItem.getElementsByTagName("aptitude").item(0).getTextContent());
                // log.info("polarity: " +
                // xmlItem.getElementsByTagName("polarity").item(0).getTextContent());

                SenticItem item = new SenticItem();
                item.setText(xmlItem.getElementsByTagName("text").item(0).getTextContent());
                item.setPolarity(Float.parseFloat(xmlItem.getElementsByTagName("polarity").item(0).getTextContent()));
                datastoreService.save(item);
            }
        }
        this.getDataToMemory();
        log.info("SenticNet info updated. There are <" + temp + "> items available.");

    }

    @PostConstruct
    public void getDataToMemory() {
        // Load all the fields here
        log.debug("Loading SenticNet data into memory...");
        this.data = new HashMap<>();
        List<SenticItem> list = datastoreService.findAll(SenticItem.class);

        for (SenticItem item : list) {
            this.data.put(item.getText(), item.getPolarity());
        }
    }

    @Override
    public Float getPolarity(String sentence) {
        return this.data.get(sentence);
    }

}
