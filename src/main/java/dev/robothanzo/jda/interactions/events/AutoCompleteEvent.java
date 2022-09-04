package dev.robothanzo.jda.interactions.events;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event will be called after the auto completer has either finished or failed.
 */
@Getter
public class AutoCompleteEvent extends Event {
    private final boolean success;
    private final String completer;
    @Nullable
    private final Exception exception;
    private final long elapsed;

    public AutoCompleteEvent(@NotNull JDA api, boolean success, String completer, @Nullable Exception exception, long elapsed) {
        super(api);
        this.success = success;
        this.completer = completer;
        this.exception = exception;
        this.elapsed = elapsed;
    }
}
