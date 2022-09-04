package dev.robothanzo.jda.interactions.listeners;

import dev.robothanzo.jda.interactions.JDAInteractions;
import dev.robothanzo.jda.interactions.events.ButtonEvent;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.JDAImpl;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Date;


@Slf4j
@AllArgsConstructor
public class ButtonListener extends ListenerAdapter {
    private JDAInteractions jdaInteractions;

    @Override
    @SneakyThrows
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        Date beganProcessing = new Date();
        if (jdaInteractions.getButtons().containsKey(event.getButton().getId())) {
            Method method = jdaInteractions.getButtons().get(event.getButton().getId());
            try {
                method.invoke(method.getDeclaringClass().getDeclaredConstructors()[0].newInstance(), event);
                log.info("Processed button {} in {}ms", event.getButton().getId(), (new Date().getTime() - beganProcessing.getTime()));
                ((JDAImpl) event.getJDA()).handleEvent(new ButtonEvent(event.getJDA(), true, event.getButton().getId(), null));
            } catch (Exception e) {
                log.error("Execution of button {} failed after {}ms", event.getButton().getId(), (new Date().getTime() - beganProcessing.getTime()), e);
                ((JDAImpl) event.getJDA()).handleEvent(new ButtonEvent(event.getJDA(), false, event.getButton().getId(), e));
            }
        }
    }
}
