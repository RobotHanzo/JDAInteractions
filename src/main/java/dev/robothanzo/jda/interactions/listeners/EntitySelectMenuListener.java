package dev.robothanzo.jda.interactions.listeners;

import dev.robothanzo.jda.interactions.JDAInteractions;
import dev.robothanzo.jda.interactions.events.select.StringSelectMenuEvent;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Date;

@Slf4j
@AllArgsConstructor
public class EntitySelectMenuListener extends ListenerAdapter {
    private JDAInteractions jdaInteractions;

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        Date beganProcessing = new Date();
        if (jdaInteractions.getEntitySelectMenus().containsKey(event.getSelectMenu().getId())) {
            Method method = jdaInteractions.getEntitySelectMenus().get(event.getSelectMenu().getId());
            try {
                method.invoke(method.getDeclaringClass().getDeclaredConstructors()[0].newInstance(), event);
                long elapsed = new Date().getTime() - beganProcessing.getTime();
                log.info("Processed entity select menu {} in {}ms", event.getSelectMenu().getId(), elapsed);
                event.getJDA().getEventManager().handle(new StringSelectMenuEvent(event.getJDA(), true, event.getSelectMenu().getId(), null, elapsed));
            } catch (Exception e) {
                long elapsed = new Date().getTime() - beganProcessing.getTime();
                log.error("Execution of entity select menu {} failed after {}ms", event.getSelectMenu().getId(), elapsed, e);
                event.getJDA().getEventManager().handle(new StringSelectMenuEvent(event.getJDA(), false, event.getSelectMenu().getId(), e, elapsed));
            }
        }
    }
}
