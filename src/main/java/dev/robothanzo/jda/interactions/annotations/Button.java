package dev.robothanzo.jda.interactions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with this annotation will be registered as a button at startup.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Button {
    /**
     * The ID of the button.
     * Defaults to the name of the method.
     */
    String value() default "";
}
