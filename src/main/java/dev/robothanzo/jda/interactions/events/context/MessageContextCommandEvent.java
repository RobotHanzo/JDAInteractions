package dev.robothanzo.jda.interactions.events.context;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event will be called after the message context command handler has either finished or failed.
 */
@Getter
public class MessageContextCommandEvent extends GenericContextCommandEvent {
    public MessageContextCommandEvent(@NotNull JDA api, boolean success, String command, @Nullable Exception exception, long elapsed) {
        super(api, success, command, exception, elapsed);
    }
}
