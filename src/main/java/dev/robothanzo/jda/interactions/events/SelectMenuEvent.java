package dev.robothanzo.jda.interactions.events;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event will be called after the select menu handler has either finished or failed.
 */
@Getter
public class SelectMenuEvent extends Event {
    private final boolean success;
    private final String menu;
    @Nullable
    private final Exception exception;

    public SelectMenuEvent(@NotNull JDA api, boolean success, String menu, @Nullable Exception exception) {
        super(api);
        this.success = success;
        this.menu = menu;
        this.exception = exception;
    }
}
