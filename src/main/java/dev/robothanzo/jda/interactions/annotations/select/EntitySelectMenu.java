package dev.robothanzo.jda.interactions.annotations.select;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.SelectTarget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Methods annotated with this annotation will be registered as an entity select menu at startup.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EntitySelectMenu {
    /**
     * The ID of the entity select menu.
     * Defaults to the name of the method.
     */
    String value() default "";

    /**
     * Targets of the select menu.<br>
     * From JDA docs:
     * <br>Note that some combinations are unsupported by Discord, due to the restrictive API design.
     *
     * <p>The only combination that is currently supported is {@link SelectTarget#USER} + {@link SelectTarget#ROLE} (often referred to as "mentionables").
     * Combinations such as {@link SelectTarget#ROLE} + {@link SelectTarget#CHANNEL} are currently not supported.
     */
    SelectTarget[] targets();

    /**
     * ChannelTypes of the select menu.<br>
     * Only used if {@link SelectTarget#CHANNEL} is in {@link #targets()}.
     */
    ChannelType[] channelTypes() default {};
}
