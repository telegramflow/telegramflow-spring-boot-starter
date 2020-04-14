package com.telegramflow.screens.xml;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.dom4j.Document;
import org.springframework.stereotype.Component;

@Component(ScreenXmlDocumentCache.NAME)
public class ScreenXmlDocumentCache {

    public static final String NAME = "tf$ScreenXmlDocumentCache";

    protected Cache<String, Document> cache;

    public ScreenXmlDocumentCache() {
        this(100);
    }

    protected ScreenXmlDocumentCache(int cacheDescriptorsCount) {
        cache = CacheBuilder.newBuilder().maximumSize(cacheDescriptorsCount).build();
    }

    public void put(String xml, Document document) {
        cache.put(xml, document);
    }

    public Document get(String xml) {
        return cache.getIfPresent(xml);
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }
}
