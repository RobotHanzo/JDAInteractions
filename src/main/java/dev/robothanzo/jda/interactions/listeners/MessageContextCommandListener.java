package dev.robothanzo.jda.interactions.listeners;

import dev.robothanzo.jda.interactions.JDAInteractions;
import dev.robothanzo.jda.interactions.events.MessageContextCommandEvent;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Date;


@AllArgsConstructor
@Slf4j
public class MessageContextCommandListener extends ListenerAdapter {
    private JDAInteractions jdaInteractions;

    @Override
    @SneakyThrows
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        Date beganProcessing = new Date();
        if (jdaInteractions.getMessageContextCommands().containsKey(event.getName())) {
            Method method = jdaInteractions.getMessageContextCommands().get(event.getName());
            try {
                method.invoke(method.getDeclaringClass().getDeclaredConstructors()[0].newInstance(), event);
                long elapsed = new Date().getTime() - beganProcessing.getTime();
                log.info("Processed message context command {} in {}ms", event.getName(), elapsed);
                event.getJDA().getEventManager().handle(new MessageContextCommandEvent(event.getJDA(), true, event.getName(), null, elapsed));
            } catch (Exception e) {
                long elapsed = new Date().getTime() - beganProcessing.getTime();
                log.error("Execution of message context command {} failed after {}ms", event.getName(), elapsed, e);
                event.getJDA().getEventManager().handle(new MessageContextCommandEvent(event.getJDA(), false, event.getName(), e, elapsed));
            }
        }
    }
}
