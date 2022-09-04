package dev.robothanzo.jda.interactions.listeners;

import dev.robothanzo.jda.interactions.JDAInteractions;
import dev.robothanzo.jda.interactions.annotations.slash.options.Option;
import dev.robothanzo.jda.interactions.events.AutoCompleteEvent;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.JDAImpl;
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
        if (jdaInteractions.getCommands().containsKey(event.getCommandPath())) {
            for (Parameter parameter : jdaInteractions.getCommands().get(event.getCommandPath()).getParameters()) {
                if (parameter.isAnnotationPresent(Option.class)) {
                    Option option = parameter.getAnnotation(Option.class);
                    String optionName = option.value().isEmpty() ? parameter.getName().toLowerCase(Locale.ROOT) : option.value();
                    if (optionName.equals(event.getFocusedOption().getName())) {
                        String autoCompleterName = option.autoCompleter().isEmpty() ? optionName : option.autoCompleter();
                        if (jdaInteractions.getAutoCompleters().containsKey(autoCompleterName)) {
                            Method method = jdaInteractions.getAutoCompleters().get(autoCompleterName);
                            try {
                                method.invoke(method.getDeclaringClass().getDeclaredConstructors()[0].newInstance(), event);
                                log.info("Processed auto completion {} in {}ms", autoCompleterName, (new Date().getTime() - beganProcessing.getTime()));
                                ((JDAImpl) event.getJDA()).handleEvent(new AutoCompleteEvent(event.getJDA(), true, autoCompleterName, null));
                            } catch (Exception e) {
                                log.error("Execution of auto completion {} failed after {}ms", event.getName(), (new Date().getTime() - beganProcessing.getTime()), e);
                                ((JDAImpl) event.getJDA()).handleEvent(new AutoCompleteEvent(event.getJDA(), false, autoCompleterName, e));
                            }
                        }
                    }
                }
            }
        }
    }
}
