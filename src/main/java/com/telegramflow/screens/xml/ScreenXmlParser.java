package com.telegramflow.screens.xml;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Parses screen XML descriptors.
 */
@Component(ScreenXmlParser.NAME)
public class ScreenXmlParser {

    public static final String NAME = "tf$ScreenXmlParser";

    @Inject
    protected XmlParser xmlParser;

    public Document parseDescriptor(InputStream stream) {
        Objects.requireNonNull(stream, "stream is null");

        String template;
        try {
            template = IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return parseDescriptor(template);
    }

    public Document parseDescriptor(String template) {
        Objects.requireNonNull(template, "template is null");

        Document document = xmlParser.readDocument(template);

        return document;
    }

}
