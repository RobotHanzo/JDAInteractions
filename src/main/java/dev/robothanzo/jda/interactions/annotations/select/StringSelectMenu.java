package dev.robothanzo.jda.interactions.annotations.select;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with this annotation will be registered as an string select menu at startup.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StringSelectMenu {
    /**
     * The ID of the string select menu.
     * Defaults to the name of the method.
     */
    String value() default "";
}
