package dev.robothanzo.jda.interactions.listeners;

import dev.robothanzo.jda.interactions.JDAInteractions;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Date;

@Slf4j
@AllArgsConstructor
public class AutoCompleteListener extends ListenerAdapter {
    private JDAInteractions jdaInteractions;

    @Override
    @SneakyThrows
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        Date beganProcessing = new Date();
        if (jdaInteractions.getAutoCompleters().containsKey(event.getFocusedOption().getName())) {
            Method method = jdaInteractions.getAutoCompleters().get(event.getFocusedOption().getName());
            method.invoke(method.getDeclaringClass().getDeclaredConstructors()[0].newInstance(), event);
            log.info("Processed auto completion {} in {}ms", event.getName(), (new Date().getTime() - beganProcessing.getTime()));
        }
    }
}
