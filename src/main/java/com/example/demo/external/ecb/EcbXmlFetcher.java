package com.example.demo.external.ecb;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;

@Component
public class EcbXmlFetcher {

    public Document fetchXml(String url) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(new URL(url).openStream());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to fetch ECB XML from " + url, e);
        }
    }
}
