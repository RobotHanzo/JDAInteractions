package dev.robothanzo.jda.interactions.annotations.context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotated with this annotation will be registered as a user context menu application command at startup
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UserCtx {
    /**
     * The name of the command.
     * Defaults to the method name.
     */
    String value() default "";
}
