package dev.robothanzo.jda.interactions.events.context;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event will be called after the context command handler has either finished or failed.
 */
public class GenericContextCommandEvent extends Event {
    private final boolean success;
    private final String command;
    @Nullable
    private final Exception exception;
    private final long elapsed;

    public GenericContextCommandEvent(@NotNull JDA api, boolean success, String command, @Nullable Exception exception, long elapsed) {
        super(api);
        this.success = success;
        this.command = command;
        this.exception = exception;
        this.elapsed = elapsed;
    }
}
