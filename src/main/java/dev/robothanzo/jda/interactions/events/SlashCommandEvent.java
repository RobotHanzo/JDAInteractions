package dev.robothanzo.jda.interactions.events;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event will be called after the slash command handler has either finished or failed.
 */
@Getter
public class SlashCommandEvent extends Event {
    private final boolean success;
    private final String command;
    @Nullable
    private final Exception exception;

    public SlashCommandEvent(@NotNull JDA api, boolean success, String command, @Nullable Exception exception) {
        super(api);
        this.success = success;
        this.command = command;
        this.exception = exception;
    }
}
