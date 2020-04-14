package com.telegramflow.screens;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Configures the base packages used by auto-configuration when scanning for screen
 * classes.
 *
 * One of {@link #basePackageClasses()}, {@link #basePackages()} or its alias
 * {@link #value()} may be specified to define specific packages to scan. If specific
 * packages are not defined scanning will occur from the package of the class with this
 * annotation.
 *
 * @author Ruslan Galimov
 * @since 1.0.0
 * @see ScreenScanPackages
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ScreenScanPackages.Registrar.class)
public @interface ScreenScan {

    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation
     * declarations e.g.: {@code @ScreenScan("org.my.pkg")} instead of
     * {@code @ScreenScan(basePackages="org.my.pkg")}.
     * @return the base packages to scan
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * Base packages to scan for screens. {@link #value()} is an alias for (and mutually
     * exclusive with) this attribute.
     * <p>
     * Use {@link #basePackageClasses()} for a type-safe alternative to String-based
     * package names.
     * @return the base packages to scan
     */
    @AliasFor("value")
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to
     * scan for screens. The package of each class specified will be scanned.
     * <p>
     * Consider creating a special no-op marker class or interface in each package that
     * serves no purpose other than being referenced by this attribute.
     * @return classes from the base packages to scan
     */
    Class<?>[] basePackageClasses() default {};

}
