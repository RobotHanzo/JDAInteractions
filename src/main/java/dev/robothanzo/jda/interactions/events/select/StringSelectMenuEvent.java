package dev.robothanzo.jda.interactions.events.select;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event will be called after the string select menu handler either finished or failed.
 */
public class StringSelectMenuEvent extends GenericSelectMenuEvent {
    public StringSelectMenuEvent(@NotNull JDA api, boolean success, String menu, @Nullable Exception exception, long elapsed) {
        super(api, success, menu, exception, elapsed);
    }
}
