package com.telegramflow.screens;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Indicates that the annotated class is a screen.
 * The annotated class must be a direct or indirect subclass of {@link Screen}.
 *
 * @see Screen
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UiController {
    String ID_ATTRIBUTE = "id";
    String VALUE_ATTRIBUTE = "value";

    /**
     * Screen identifier.
     */
    @AliasFor(ID_ATTRIBUTE)
    String value() default "";

    /**
     * Screen identifier.
     */
    @AliasFor(VALUE_ATTRIBUTE)
    String id() default "";
}
