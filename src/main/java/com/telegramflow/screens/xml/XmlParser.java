package com.telegramflow.screens.xml;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import java.io.*;
import java.util.Map;
import java.util.function.Function;

/**
 * Helper bean for XML parsing.
 * Caches SAXParser instances in the pool.
 */
@Component(XmlParser.NAME)
public class XmlParser {

    public static final String NAME = "tf$XmlParser";

    private final static int MAX_POOL_SIZE = 100;
    private final static int MAX_BORROW_WAIT_MILLIS = 10000;

    protected GenericObjectPool<SAXParser> pool;

    public XmlParser() {
        initialize();
    }

    protected void initialize() {
        GenericObjectPoolConfig<SAXParser> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(MAX_POOL_SIZE);
        poolConfig.setMaxTotal(MAX_POOL_SIZE);
        poolConfig.setMaxWaitMillis(MAX_BORROW_WAIT_MILLIS);

        PooledObjectFactory<SAXParser> factory = new SAXParserObjectFactory();
        pool = new GenericObjectPool<>(factory, poolConfig);
    }

    /**
     * Shuts down the pool, unregisters JMX.
     */
    public void shutdown() {
        if (pool != null) {
            pool.close();
            pool = null;
        }
    }

    public String writeDocument(Document doc, boolean prettyPrint) {
        return Dom4j.writeDocument(doc, prettyPrint);
    }

    public void writeDocument(Document doc, boolean prettyPrint, Writer writer) {
        Dom4j.writeDocument(doc, prettyPrint, writer);
    }

    public void writeDocument(Document doc, boolean prettyPrint, OutputStream stream) {
        Dom4j.writeDocument(doc, prettyPrint, stream);
    }

    public Document readDocument(File file) {
        return withSAXParserFromPool(saxParser -> Dom4j.readDocument(file, getSaxReader(saxParser)));
    }

    public Document readDocument(InputStream stream) {
        return withSAXParserFromPool(saxParser -> Dom4j.readDocument(stream, getSaxReader(saxParser)));
    }

    public Document readDocument(Reader reader) {
        return withSAXParserFromPool(saxParser -> Dom4j.readDocument(reader, getSaxReader(saxParser)));
    }

    public Document readDocument(String xmlString) {
        return withSAXParserFromPool(saxParser -> Dom4j.readDocument(xmlString, getSaxReader(saxParser)));
    }

    public void storeMap(Element parentElement, Map<String, String> map) {
        Dom4j.storeMap(parentElement, map);
    }

    public void loadMap(Element mapElement, Map<String, String> map) {
        Dom4j.loadMap(mapElement, map);
    }

    public void walkAttributesRecursive(Element element, Dom4j.ElementAttributeVisitor visitor) {
        Dom4j.walkAttributesRecursive(element, visitor);
    }

    public void walkAttributes(Element element, Dom4j.ElementAttributeVisitor visitor) {
        Dom4j.walkAttributes(element, visitor);
    }

    protected SAXReader getSaxReader(SAXParser saxParser) {
        try {
            return new SAXReader(saxParser.getXMLReader());
        } catch (SAXException e) {
            throw new RuntimeException("Unable to create SAX reader", e);
        }
    }

    protected  <T> T withSAXParserFromPool(Function<SAXParser, T> action) {
        SAXParser parser;
        try {
            parser = pool.borrowObject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to borrow SAXParser object from pool", e);
        }
        try {
            return action.apply(parser);
        } finally {
            pool.returnObject(parser);
        }
    }

}
