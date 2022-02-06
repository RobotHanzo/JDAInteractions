package dev.robothanzo.jda.interactions.annotations.slash.options;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotated with this annotation will be registered as an auto completer for the {@link Option}
 * Method must have a single parameter of type {@link CommandAutoCompleteInteractionEvent}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoCompleter {
    /**
     * The name of the option to auto complete
     * Defaults to the lowercase name of the method
     **/
    String value() default "";
}
