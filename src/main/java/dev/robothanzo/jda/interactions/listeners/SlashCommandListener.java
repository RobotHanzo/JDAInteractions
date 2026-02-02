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
        if (jdaInteractions.getCommands().containsKey(event.getFullCommandName())) {
            List<Object> parameters = new LinkedList<>();
            Method method = jdaInteractions.getCommands().get(event.getFullCommandName());
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
                        log.warn("Option {} was not found for command {} but it is not optional, a null was passed instead", optionName, event.getFullCommandName());
                        continue;
                    }
                }
                // If the parameter type directly maps to an OptionType (including primitives/wrappers),
                // use the built-in conversion and coerce primitives/wrappers as needed.
                var opt = Objects.requireNonNull(event.getOption(optionName));
                var mapped = JDAInteractions.getOptionTypeForClass(parameter.getType());
                if (mapped.isPresent()) {
                    Object converted = convertArgument(opt);
                    Class<?> ptype = parameter.getType();
                    if (converted == null) {
                        parameters.add(null);
                        continue;
                    }
                    switch (mapped.get()) {
                        case INTEGER -> {
                            // convert Long -> Integer/Long as required
                            if (ptype == Integer.class || ptype == int.class) {
                                parameters.add(((Long) converted).intValue());
                            } else {
                                parameters.add(converted);
                            }
                        }
                        case NUMBER -> {
                            // convert Double -> Float/Double as required
                            if (ptype == Float.class || ptype == float.class) {
                                parameters.add(((Double) converted).floatValue());
                            } else {
                                parameters.add(converted);
                            }
                        }
                        default -> parameters.add(converted);
                    }
                } else {
                    IMapper<?, ?> mapper = jdaInteractions.getMappers().get(parameter.getType());
                    parameters.add(mapper.map(mapper.getSourceType().cast(convertArgument(opt))));
                }
            }
            try {
                method.invoke(method.getDeclaringClass().getDeclaredConstructors()[0].newInstance(), parameters.toArray());
                long elapsed = new Date().getTime() - beganProcessing.getTime();
                log.info("Processed command {} in {}ms", event.getFullCommandName(), elapsed);
                event.getJDA().getEventManager().handle(new SlashCommandEvent(event.getJDA(), true, event.getFullCommandName(), null, elapsed));
            } catch (Exception e) {
                long elapsed = new Date().getTime() - beganProcessing.getTime();
                log.error("Execution of command {} failed after {}ms", event.getFullCommandName(), elapsed, e);
                event.getJDA().getEventManager().handle(new SlashCommandEvent(event.getJDA(), false, event.getFullCommandName(), e, elapsed));
            }
        }
    }
}
