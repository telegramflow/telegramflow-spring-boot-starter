package com.telegramflow.screens.xml;

import com.telegramflow.global.DevelopmentException;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Loads screen XML descriptors.
 */
@Component(ScreenXmlLoader.NAME)
public class ScreenXmlLoader {

    public static final String NAME = "tf$ScreenXmlLoader";

    @Inject
    protected ResourceLoader resources;
    @Inject
    protected ScreenXmlDocumentCache screenXmlCache;
    @Inject
    protected ScreenXmlParser screenXmlParser;

    /**
     * Loads a descriptor.
     *
     * @param resourcePath path to the resource containing the XML
     * @return root XML element
     */
    public Element load(String resourcePath) {

        String template = loadTemplate(resourcePath);
        Document document = getDocument(template);

        return document.getRootElement();
    }

    protected String loadTemplate(String resourcePath) {
        try (InputStream stream = getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new DevelopmentException("Template is not found " + resourcePath);
            }

            return IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read screen template");
        }
    }

    protected Document getDocument(String template) {
        Document document = screenXmlCache.get(template);
        if (document == null) {
            document = screenXmlParser.parseDescriptor(template);
            screenXmlCache.put(template, document);
        }
        return document;
    }

    protected InputStream getResourceAsStream(String location) {
        try {
            Resource resource = getResource(location);
            if (resource.exists())
                return resource.getInputStream();
            else
                return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Resource getResource(String location) {
        if (!ResourceUtils.isUrl(location)) {
            if (location.startsWith("/")) {
                location = location.substring(1);
            }
            File file = new File(location);
            if (file.exists()) {
                location = file.toURI().toString();
            } else {
                location = "classpath:" + location;
            }
        }
        return resources.getResource(location);
    }
}