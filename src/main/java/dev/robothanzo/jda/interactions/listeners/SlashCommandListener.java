package dev.robothanzo.jda.interactions.listeners;

import dev.robothanzo.jda.interactions.JDAInteractions;
import dev.robothanzo.jda.interactions.annotations.slash.options.IMapper;
import dev.robothanzo.jda.interactions.annotations.slash.options.Option;
import dev.robothanzo.jda.interactions.events.SlashCommandEvent;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.internal.JDAImpl;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@AllArgsConstructor
@Slf4j
public class SlashCommandListener extends ListenerAdapter {
    private JDAInteractions jdaInteractions;

    private Object convertArgument(OptionMapping source) {
        return switch (source.getType()) {
            case STRING -> source.getAsString();
            case INTEGER -> source.getAsLong();
            case BOOLEAN -> source.getAsBoolean();
            case NUMBER -> source.getAsDouble();
            case USER -> source.getAsUser();
            case CHANNEL -> source.getAsChannel();
            case ROLE -> source.getAsRole();
            default -> null;
        };
    }

    @SneakyThrows
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Date beganProcessing = new Date();
        if (jdaInteractions.getCommands().containsKey(event.getCommandPath())) {
            List<Object> parameters = new LinkedList<>();
            Method method = jdaInteractions.getCommands().get(event.getCommandPath());
            for (Parameter parameter : method.getParameters()) {
                if (parameter.getType().equals(SlashCommandInteractionEvent.class)) {
                    parameters.add(event);
                    continue;
                }
                Option option = parameter.getAnnotation(Option.class);
                String optionName = option.value().isEmpty() ? parameter.getName().toLowerCase(Locale.ROOT) : option.value();
                if (option.optional()) {
                    if (event.getOption(optionName) == null) {
                        parameters.add(null);
                        continue;
                    }
                } else {
                    if (event.getOption(optionName) == null) {
                        parameters.add(null);
                        log.warn("Option {} was not found for command {} but it is not optional, a null was passed instead", optionName, event.getCommandPath());
                        continue;
                    }
                }
                if (JDAInteractions.OPTION_TYPE_CLASS_MAP.containsValue(parameter.getType())) {
                    parameters.add(convertArgument(Objects.requireNonNull(event.getOption(optionName))));
                } else {
                    IMapper<?, ?> mapper = jdaInteractions.getMappers().get(parameter.getType());
                    parameters.add(mapper.map(mapper.getSourceType().cast(convertArgument(
                            Objects.requireNonNull(event.getOption(optionName))))));
                }
            }
            try {
                method.invoke(method.getDeclaringClass().getDeclaredConstructors()[0].newInstance(), parameters.toArray());
                log.info("Processed command {} in {}ms", event.getCommandPath(), (new Date().getTime() - beganProcessing.getTime()));
                ((JDAImpl) event.getJDA()).handleEvent(new SlashCommandEvent(event.getJDA(), true, event.getCommandPath(), null));
            } catch (Exception e) {
                log.error("Execution of command {} failed after {}ms", event.getCommandPath(), new Date().getTime() - beganProcessing.getTime(), e);
                ((JDAImpl) event.getJDA()).handleEvent(new SlashCommandEvent(event.getJDA(), false, event.getCommandPath(), e));
            }
        }
    }
}
