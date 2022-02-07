package dev.robothanzo.jda.interactions.annotations.slash.options;

import dev.robothanzo.jda.interactions.annotations.slash.Command;
import dev.robothanzo.jda.interactions.annotations.slash.Subcommand;
import net.dv8tion.jda.api.entities.ChannelType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Parameter annotated with this annotation will be registered as an option for a {@link Command} or {@link Subcommand}.
 * <br>
 * Pre-defined parameter types: <br>
 * {@link String}<br>
 * {@link Long}<br>
 * {@link Double}<br>
 * {@link Boolean}<br>
 * {@link net.dv8tion.jda.api.entities.User}<br>
 * {@link net.dv8tion.jda.api.entities.Role}<br>
 * {@link net.dv8tion.jda.api.entities.GuildChannel} (Can be limited using the parameter channelTypes)<br>
 * Types not listed above will must have their own {@link IMapper} implementation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Option {
    /**
     * The name of the option.
     * Defaults to the lowercase parameter name.
     * <h1>
     *     If you are using Gradle, there is a high chance that you get something like arg1, arg2 instead of the actual parameter name if you leave it empty.
     *     It's not a bug, it's how Gradle works.
     * </h1>
     */
    String value() default "";

    /**
     * The description of the option.
     * Defaults to the name of the option.
     */
    String description() default "";

    /**
     * The minimum value of the option.
     * Only applies if the option is of type {@link Integer}
     */
    long minLong() default Long.MIN_VALUE;

    /**
     * The maximum value of the option.
     * Only applies if the option is of type {@link Integer}
     */
    long maxLong() default Long.MAX_VALUE;

    /**
     * The minimum value of the option.
     * Only applies if the option is of type {@link Double}
     */
    double minDouble() default Double.MIN_VALUE;

    /**
     * The maximum value of the option.
     * Only applies if the option is of type {@link Double}
     */
    double maxDouble() default Double.MAX_VALUE;

    /**
     * The channel types that the option supports.
     * Only applies if the option is of type {@link net.dv8tion.jda.api.entities.Channel}.
     * Leaving it empty will allow all channel types.
     */
    ChannelType[] channelTypes() default {};

    /**
     * Whether the option is optional.
     */
    boolean optional() default false;

    /**
     * Whether the option can be auto completed.
     * An {@link AutoCompleter} with the same value must be registered if it is {@code true}.
     *
     * @throws IllegalArgumentException if the option is auto completable, no autoCompleteValues and no {@link AutoCompleter} is registered.
     */
    boolean autoComplete() default false;
}
