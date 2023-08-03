package dev.robothanzo.jda.interactions.listeners;

import dev.robothanzo.jda.interactions.JDAInteractions;
import dev.robothanzo.jda.interactions.events.select.StringSelectMenuEvent;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Date;

@Slf4j
@AllArgsConstructor
public class StringSelectMenuListener extends ListenerAdapter {
    private JDAInteractions jdaInteractions;

    @Override
    @SneakyThrows
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        Date beganProcessing = new Date();
        if (jdaInteractions.getStringSelectMenus().containsKey(event.getSelectMenu().getCustomId())) {
            Method method = jdaInteractions.getStringSelectMenus().get(event.getSelectMenu().getCustomId());
            try {
                method.invoke(method.getDeclaringClass().getDeclaredConstructors()[0].newInstance(), event);
                long elapsed = new Date().getTime() - beganProcessing.getTime();
                log.info("Processed string select menu {} in {}ms", event.getSelectMenu().getCustomId(), elapsed);
                event.getJDA().getEventManager().handle(new StringSelectMenuEvent(event.getJDA(), true, event.getSelectMenu().getCustomId(), null, elapsed));
            } catch (Exception e) {
                long elapsed = new Date().getTime() - beganProcessing.getTime();
                log.error("Execution of string select menu {} failed after {}ms", event.getSelectMenu().getCustomId(), elapsed, e);
                event.getJDA().getEventManager().handle(new StringSelectMenuEvent(event.getJDA(), false, event.getSelectMenu().getCustomId(), e, elapsed));
            }
        }
    }
}
