package com.example.demo.dom;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class DOM {

    public Document loadDocumentFromXml(String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
        return builder.parse(inputStream);
    }
}
