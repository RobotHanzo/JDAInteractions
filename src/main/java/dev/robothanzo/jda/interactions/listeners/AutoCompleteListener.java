package dev.robothanzo.jda.interactions.listeners;

import dev.robothanzo.jda.interactions.JDAInteractions;
import dev.robothanzo.jda.interactions.annotations.slash.options.Option;
import dev.robothanzo.jda.interactions.events.AutoCompleteEvent;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.Locale;

@Slf4j
@AllArgsConstructor
public class AutoCompleteListener extends ListenerAdapter {
    private JDAInteractions jdaInteractions;

    @Override
    @SneakyThrows
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        Date beganProcessing = new Date();
        if (jdaInteractions.getCommands().containsKey(event.getFullCommandName())) {
            for (Parameter parameter : jdaInteractions.getCommands().get(event.getFullCommandName()).getParameters()) {
                if (parameter.isAnnotationPresent(Option.class)) {
                    Option option = parameter.getAnnotation(Option.class);
                    String optionName = option.value().isEmpty() ? parameter.getName().toLowerCase(Locale.ROOT) : option.value();
                    if (optionName.equals(event.getFocusedOption().getName())) {
                        String autoCompleterName = option.autoCompleter().isEmpty() ? optionName : option.autoCompleter();
                        if (jdaInteractions.getAutoCompleters().containsKey(autoCompleterName)) {
                            Method method = jdaInteractions.getAutoCompleters().get(autoCompleterName);
                            try {
                                method.invoke(method.getDeclaringClass().getDeclaredConstructors()[0].newInstance(), event);
                                long elapsed = new Date().getTime() - beganProcessing.getTime();
                                log.info("Processed auto completion {} in {}ms", autoCompleterName, elapsed);
                                event.getJDA().getEventManager().handle(new AutoCompleteEvent(event.getJDA(), true, autoCompleterName, null, elapsed));
                            } catch (Exception e) {
                                long elapsed = new Date().getTime() - beganProcessing.getTime();
                                log.error("Execution of auto completion {} failed after {}ms", event.getName(), elapsed, e);
                                event.getJDA().getEventManager().handle(new AutoCompleteEvent(event.getJDA(), false, autoCompleterName, e, elapsed));
                            }
                        }
                    }
                }
            }
        }
    }
}
