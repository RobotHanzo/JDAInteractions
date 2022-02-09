package dev.robothanzo.jda.interactions.listeners;

import dev.robothanzo.jda.interactions.JDAInteractions;
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
            method.invoke(method.getDeclaringClass().getDeclaredConstructors()[0].newInstance(), event);
            log.info("Processed select menu {} in {}ms", event.getSelectMenu().getId(), (new Date().getTime() - beganProcessing.getTime()));
        }
    }
}
