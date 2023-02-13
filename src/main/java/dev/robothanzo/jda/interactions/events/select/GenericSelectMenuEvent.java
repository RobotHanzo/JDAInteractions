package dev.robothanzo.jda.interactions.events.select;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event will be called after the select menu handler either finished or failed.
 */
@Getter
public abstract class GenericSelectMenuEvent extends Event {
    private final boolean success;
    private final String menu;
    @Nullable
    private final Exception exception;
    private final long elapsed;

    public GenericSelectMenuEvent(@NotNull JDA api, boolean success, String menu, @Nullable Exception exception, long elapsed) {
        super(api);
        this.success = success;
        this.menu = menu;
        this.exception = exception;
        this.elapsed = elapsed;
    }
}
