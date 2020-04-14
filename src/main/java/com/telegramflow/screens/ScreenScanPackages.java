package com.telegramflow.screens;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Class for storing {@link ScreenScan @ScreenScan} specified packages for reference later
 *
 * @author Ruslan Galimov
 * @since 1.0.0
 * @see ScreenScan
 * @see ScreenScanner
 */
public class ScreenScanPackages {

    private static final String BEAN = ScreenScanPackages.class.getName();

    private static final ScreenScanPackages NONE = new ScreenScanPackages();

    private final List<String> packageNames;

    ScreenScanPackages(String... packageNames) {
        List<String> packages = new ArrayList<>();
        for (String name : packageNames) {
            if (StringUtils.hasText(name)) {
                packages.add(name);
            }
        }
        this.packageNames = Collections.unmodifiableList(packages);
    }

    /**
     * Return the package names specified from all {@link ScreenScan @ScreenScan}
     * annotations.
     * @return the screen scan package names
     */
    public List<String> getPackageNames() {
        return this.packageNames;
    }

    /**
     * Return the {@link ScreenScanPackages} for the given bean factory.
     * @param beanFactory the source bean factory
     * @return the {@link ScreenScanPackages} for the bean factory (never {@code null})
     */
    public static ScreenScanPackages get(BeanFactory beanFactory) {
        // Currently we only store a single base package, but we return a list to
        // allow this to change in the future if needed
        try {
            return beanFactory.getBean(BEAN, ScreenScanPackages.class);
        }
        catch (NoSuchBeanDefinitionException ex) {
            return NONE;
        }
    }

    /**
     * Register the specified screen scan packages with the system.
     * @param registry the source registry
     * @param packageNames the package names to register
     */
    public static void register(BeanDefinitionRegistry registry, String... packageNames) {
        Assert.notNull(registry, "Registry must not be null");
        Assert.notNull(packageNames, "PackageNames must not be null");
        register(registry, Arrays.asList(packageNames));
    }

    /**
     * Register the specified screen scan packages with the system.
     * @param registry the source registry
     * @param packageNames the package names to register
     */
    public static void register(BeanDefinitionRegistry registry, Collection<String> packageNames) {
        Assert.notNull(registry, "Registry must not be null");
        Assert.notNull(packageNames, "PackageNames must not be null");
        if (registry.containsBeanDefinition(BEAN)) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(BEAN);
            ConstructorArgumentValues constructorArguments = beanDefinition.getConstructorArgumentValues();
            constructorArguments.addIndexedArgumentValue(0, addPackageNames(constructorArguments, packageNames));
        }
        else {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(ScreenScanPackages.class);
            beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0,
                    StringUtils.toStringArray(packageNames));
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            registry.registerBeanDefinition(BEAN, beanDefinition);
        }
    }

    private static String[] addPackageNames(ConstructorArgumentValues constructorArguments,
                                            Collection<String> packageNames) {
        String[] existing = (String[]) constructorArguments.getIndexedArgumentValue(0, String[].class).getValue();
        Set<String> merged = new LinkedHashSet<>();
        merged.addAll(Arrays.asList(existing));
        merged.addAll(packageNames);
        return StringUtils.toStringArray(merged);
    }

    /**
     * {@link ImportBeanDefinitionRegistrar} to store the base package from the importing
     * configuration.
     */
    static class Registrar implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            register(registry, getPackagesToScan(metadata));
        }

        private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
            AnnotationAttributes attributes = AnnotationAttributes
                    .fromMap(metadata.getAnnotationAttributes(ScreenScan.class.getName()));
            String[] basePackages = attributes.getStringArray("basePackages");
            Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
            Set<String> packagesToScan = new LinkedHashSet<>(Arrays.asList(basePackages));
            for (Class<?> basePackageClass : basePackageClasses) {
                packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
            }
            if (packagesToScan.isEmpty()) {
                String packageName = ClassUtils.getPackageName(metadata.getClassName());
                Assert.state(!StringUtils.isEmpty(packageName), "@ScreenScan cannot be used with the default package");
                return Collections.singleton(packageName);
            }
            return packagesToScan;
        }

    }

}
