package dev.robothanzo.jda.interactions.events;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event will be called after the button handler has either finished or failed.
 */
@Getter
public class ButtonEvent extends Event {
    private final boolean success;
    private final String button;
    @Nullable
    private final Exception exception;
    private final long elapsed;

    public ButtonEvent(@NotNull JDA api, boolean success, String button, @Nullable Exception exception, long elapsed) {
        super(api);
        this.success = success;
        this.button = button;
        this.exception = exception;
        this.elapsed = elapsed;
    }
}
