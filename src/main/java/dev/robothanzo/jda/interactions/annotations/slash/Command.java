package dev.robothanzo.jda.interactions.annotations.slash;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class annotated with this annotation will be registered as a command group at startup
 * Method annotated with this annotation will be registered as a subcommand at startup
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Command {
    /**
     * The name of the command (group)
     * Defaults to the lowercase method (class) name
     */
    String value() default "";

    /**
     * The description of the command (group)
     * Defaults to the name of the command (group).
     */
    String description() default "";
}
