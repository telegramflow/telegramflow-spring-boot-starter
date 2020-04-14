package com.telegramflow.screens.xml;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.SAXParser;

/**
 * Object factory for SAXParser object pool.
 */
public class SAXParserObjectFactory extends BasePooledObjectFactory<SAXParser> {

    private static final Logger log = LoggerFactory.getLogger(SAXParserObjectFactory.class);

    @Override
    public SAXParser create() throws Exception {
        return Dom4j.getParser();
    }

    @Override
    public PooledObject<SAXParser> wrap(SAXParser obj) {
        return new DefaultPooledObject<>(obj);
    }

}
