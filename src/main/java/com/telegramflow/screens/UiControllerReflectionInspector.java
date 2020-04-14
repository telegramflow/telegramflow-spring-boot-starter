package com.telegramflow.screens;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Component(UiControllerReflectionInspector.NAME)
public class UiControllerReflectionInspector {

    public static final String NAME = "tf$UiControllerReflectionInspector";

    protected final LoadingCache<Class<?>, ScreenMetadata> screenMetadataCache =
            CacheBuilder.newBuilder()
                    .weakKeys()
                    .build(new CacheLoader<Class<?>, ScreenMetadata>() {
                        @Override
                        public ScreenMetadata load(@Nonnull Class<?> concreteClass) {
                            return getScreenMetadataNotCached(concreteClass);
                        }
                    });

    public ScreenMetadata getScreenIntrospectionData(Class<?> clazz) {
        return screenMetadataCache.getUnchecked(clazz);
    }

    public List<InjectElement> getAnnotatedInjectElements(Class<?> clazz) {
        return screenMetadataCache.getUnchecked(clazz).getInjectElements();
    }

    protected ScreenMetadata getScreenMetadataNotCached(Class<?> concreteClass) {
        List<InjectElement> injectElements = getAnnotatedInjectElementsNotCached(concreteClass);
        return new ScreenMetadata(injectElements);
    }

    protected List<InjectElement> getAnnotatedInjectElementsNotCached(Class<?> clazz) {
        Map<AnnotatedElement, Class> toInject = Collections.emptyMap(); // lazily initialized

        List<Class<?>> classes = ClassUtils.getAllSuperclasses(clazz);
        classes.add(0, clazz);
        Collections.reverse(classes);

        for (Field field : getAllFields(classes)) {
            Class aClass = injectionAnnotation(field);
            if (aClass != null) {
                if (toInject.isEmpty()) {
                    toInject = new HashMap<>();
                }
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                toInject.put(field, aClass);
            }
        }
        for (Method method : ReflectionUtils.getUniqueDeclaredMethods(clazz)) {
            Class aClass = injectionAnnotation(method);
            if (aClass != null) {
                if (toInject.isEmpty()) {
                    toInject = new HashMap<>();
                }
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                toInject.put(method, aClass);
            }
        }

        return toInject.entrySet().stream()
                .map(entry -> new InjectElement(entry.getKey(), entry.getValue()))
                .collect(ImmutableList.toImmutableList());
    }

    protected List<Field> getAllFields(List<Class<?>> classes) {
        List<Field> list = new ArrayList<>();

        for (Class c : classes) {
            if (c != Object.class) {
                Collections.addAll(list, c.getDeclaredFields());
            }
        }
        return list;
    }

    protected Class injectionAnnotation(AnnotatedElement element) {
        if (element.isAnnotationPresent(Named.class)) {
            return Named.class;
        }

        if (element.isAnnotationPresent(Resource.class)) {
            return Resource.class;
        }

        if (element.isAnnotationPresent(Inject.class)) {
            return Inject.class;
        }

        if (element.isAnnotationPresent(Autowired.class)) {
            return Autowired.class;
        }

        return null;
    }

    public static class ScreenMetadata {
        private final List<InjectElement> injectElements;

        public ScreenMetadata(List<InjectElement> injectElements) {
            this.injectElements = injectElements;
        }

        public List<InjectElement> getInjectElements() {
            return injectElements;
        }
    }

    public static class InjectElement {
        protected final AnnotatedElement element;
        protected final Class annotationClass;

        public InjectElement(AnnotatedElement element, Class annotationClass) {
            this.element = element;
            this.annotationClass = annotationClass;
        }

        public AnnotatedElement getElement() {
            return element;
        }

        public Class getAnnotationClass() {
            return annotationClass;
        }

        @Override
        public String toString() {
            return "InjectElement{" +
                    "element=" + element +
                    ", annotationClass=" + annotationClass +
                    '}';
        }
    }
}
