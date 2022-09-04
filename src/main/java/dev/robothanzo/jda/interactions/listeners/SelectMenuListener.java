package dev.robothanzo.jda.interactions.listeners;

import dev.robothanzo.jda.interactions.JDAInteractions;
import dev.robothanzo.jda.interactions.events.SelectMenuEvent;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Date;

@Slf4j
@AllArgsConstructor
public class SelectMenuListener extends ListenerAdapter {
    private JDAInteractions jdaInteractions;

    @Override
    @SneakyThrows
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        Date beganProcessing = new Date();
        if (jdaInteractions.getSelectMenu().containsKey(event.getSelectMenu().getId())) {
            Method method = jdaInteractions.getSelectMenu().get(event.getSelectMenu().getId());
            try {
                method.invoke(method.getDeclaringClass().getDeclaredConstructors()[0].newInstance(), event);
                long elapsed = new Date().getTime() - beganProcessing.getTime();
                log.info("Processed select menu {} in {}ms", event.getSelectMenu().getId(), elapsed);
                event.getJDA().getEventManager().handle(new SelectMenuEvent(event.getJDA(), true, event.getSelectMenu().getId(), null, elapsed));
            } catch (Exception e) {
                long elapsed = new Date().getTime() - beganProcessing.getTime();
                log.error("Execution of select menu {} failed after {}ms", event.getSelectMenu().getId(), elapsed, e);
                event.getJDA().getEventManager().handle(new SelectMenuEvent(event.getJDA(), false, event.getSelectMenu().getId(), e, elapsed));
            }
        }
    }
}
