package com.telegramflow.screens;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Indicates that the annotated {@link UiController} is linked to an XML template.
 * The annotated class must be a direct or indirect subclass of {@link Screen}.
 * This annotation is inherited by subclasses.
 *
 * @see Screen
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface UiDescriptor {

    /**
     * Path to the XML descriptor. If the value contains a file name only, it is assumed that the file is located
     * in the package of the controller class.
     */
    @AliasFor("path")
    String value() default "";

    /**
     * Path to the XML descriptor. If the value contains a file name only, it is assumed that the file is located
     * in the package of the controller class.
     */
    @AliasFor("value")
    String path() default "";
}