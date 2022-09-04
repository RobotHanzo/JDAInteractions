package dev.robothanzo.jda.interactions.listeners;

import dev.robothanzo.jda.interactions.JDAInteractions;
import dev.robothanzo.jda.interactions.events.UserContextCommandEvent;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Date;


@AllArgsConstructor
@Slf4j
public class UserContextCommandListener extends ListenerAdapter {
    private JDAInteractions jdaInteractions;

    @Override
    @SneakyThrows
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        Date beganProcessing = new Date();
        if (jdaInteractions.getUserContextCommands().containsKey(event.getName())) {
            Method method = jdaInteractions.getUserContextCommands().get(event.getName());
            try {
                method.invoke(method.getDeclaringClass().getDeclaredConstructors()[0].newInstance(), event);
                long elapsed = new Date().getTime() - beganProcessing.getTime();
                log.info("Processed user context command {} in {}ms", event.getName(), elapsed);
                event.getJDA().getEventManager().handle(new UserContextCommandEvent(event.getJDA(), true, event.getName(), null, elapsed));
            } catch (Exception e) {
                long elapsed = new Date().getTime() - beganProcessing.getTime();
                log.error("Execution of user context command {} failed after {}ms", event.getName(), elapsed, e);
                event.getJDA().getEventManager().handle(new UserContextCommandEvent(event.getJDA(), false, event.getName(), e, elapsed));
            }
        }
    }
}
