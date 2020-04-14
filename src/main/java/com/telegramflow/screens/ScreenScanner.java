package com.telegramflow.screens;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An screen scanner that searches the classpath from an {@link ScreenScan @ScreenScan}
 * specified packages.
 *
 * @author Ruslan Galimov
 * @since 1.0.0
 */
public class ScreenScanner {

    private final ApplicationContext context;

    /**
     * Create a new {@link ScreenScanner} instance.
     * @param context the source application context
     */
    public ScreenScanner(ApplicationContext context) {
        Assert.notNull(context, "Context must not be null");
        this.context = context;
    }

    /**
     * Scan for screens with the specified annotations.
     * @param annotationTypes the annotation types used on the screens
     * @return a set of screen classes
     * @throws ClassNotFoundException if an screen class cannot be loaded
     */
    @SafeVarargs
    public final Set<Class<?>> scan(Class<? extends Annotation>... annotationTypes) throws ClassNotFoundException {
        List<String> packages = getPackages();
        if (packages.isEmpty()) {
            return Collections.emptySet();
        }
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.setEnvironment(this.context.getEnvironment());
        scanner.setResourceLoader(this.context);
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            scanner.addIncludeFilter(new AnnotationTypeFilter(annotationType));
        }
        Set<Class<?>> screenSet = new HashSet<>();
        for (String basePackage : packages) {
            if (StringUtils.hasText(basePackage)) {
                for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
                    screenSet.add(ClassUtils.forName(candidate.getBeanClassName(), this.context.getClassLoader()));
                }
            }
        }
        return screenSet;
    }

    private List<String> getPackages() {
        List<String> packages = ScreenScanPackages.get(this.context).getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(this.context)) {
            packages = AutoConfigurationPackages.get(this.context);
        }
        return packages;
    }

}
