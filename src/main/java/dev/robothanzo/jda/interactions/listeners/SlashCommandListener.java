package dev.robothanzo.jda.interactions.listeners;

import dev.robothanzo.jda.interactions.JDAInteractions;
import dev.robothanzo.jda.interactions.annotations.slash.options.IMapper;
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
            case CHANNEL -> source.getAsGuildChannel();
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
            int currentIndex = 0;
            Method method = jdaInteractions.getCommands().get(event.getCommandPath());
            for (Parameter parameter : method.getParameters()) {
                if (parameter.getType().equals(SlashCommandInteractionEvent.class)) {
                    parameters.add(event);
                    continue;
                }
                if (JDAInteractions.OPTION_TYPE_CLASS_MAP.containsValue(parameter.getType())) {
                    parameters.add(convertArgument(event.getOptions().get(currentIndex)));
                } else {
                    IMapper<?, ?> mapper = jdaInteractions.getMappers().get(parameter.getType());
                    parameters.add(mapper.map(mapper.getSourceType().cast(convertArgument(event.getOptions().get(currentIndex)))));
                }
                currentIndex++;
            }
            method.invoke(method.getDeclaringClass().getDeclaredConstructors()[0].newInstance(), parameters.toArray());
            log.info("Processed command {} in {}ms", event.getCommandPath(), (new Date().getTime() - beganProcessing.getTime()));
        }
    }
}
