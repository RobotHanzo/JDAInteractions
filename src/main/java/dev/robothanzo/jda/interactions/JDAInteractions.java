package dev.robothanzo.jda.interactions;

import dev.robothanzo.jda.interactions.annotations.Button;
import dev.robothanzo.jda.interactions.annotations.SelectMenu;
import dev.robothanzo.jda.interactions.annotations.context.MessageCtx;
import dev.robothanzo.jda.interactions.annotations.context.UserCtx;
import dev.robothanzo.jda.interactions.annotations.slash.Command;
import dev.robothanzo.jda.interactions.annotations.slash.Subcommand;
import dev.robothanzo.jda.interactions.annotations.slash.options.AutoCompleter;
import dev.robothanzo.jda.interactions.annotations.slash.options.IMapper;
import dev.robothanzo.jda.interactions.annotations.slash.options.Mapper;
import dev.robothanzo.jda.interactions.annotations.slash.options.Option;
import dev.robothanzo.jda.interactions.listeners.AutoCompleteListener;
import dev.robothanzo.jda.interactions.listeners.ButtonListener;
import dev.robothanzo.jda.interactions.listeners.MessageContextCommandListener;
import dev.robothanzo.jda.interactions.listeners.SelectMenuListener;
import dev.robothanzo.jda.interactions.listeners.SlashCommandListener;
import dev.robothanzo.jda.interactions.listeners.UserContextCommandListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class JDAInteractions {
    public static final Map<OptionType, Class<?>> OPTION_TYPE_CLASS_MAP = Map.of(
            OptionType.STRING, String.class,
            OptionType.INTEGER, Long.class,
            OptionType.NUMBER, Double.class,
            OptionType.BOOLEAN, Boolean.class,
            OptionType.USER, User.class,
            OptionType.ROLE, Role.class,
            OptionType.CHANNEL, GuildChannel.class
    );
    private final Reflections reflections;
    @Getter
    private final Map<String, Method> commands = new HashMap<>(); // commandPath is the key
    @Getter
    private final Map<String, Method> userContextCommands = new HashMap<>(); // commandName is the key
    @Getter
    private final Map<String, Method> messageContextCommands = new HashMap<>(); // commandName is the key
    @Getter
    private final Map<String, Method> buttons = new HashMap<>();
    @Getter
    private final Map<String, Method> selectMenu = new HashMap<>();
    @Getter
    private final Map<String, Method> autoCompleters = new HashMap<>();
    @Getter
    private final Map<Class<?>, IMapper<?, ?>> mappers = new HashMap<>();

    public JDAInteractions(Reflections reflections) {
        this.reflections = reflections;
        collectMappers();
        collectButtons();
        collectSelectMenus();
        collectAutoCompleters();
    }

    public JDAInteractions(String packages) {
        this.reflections = new Reflections(packages, Scanners.FieldsAnnotated, Scanners.MethodsAnnotated, Scanners.TypesAnnotated);
        collectMappers();
        collectButtons();
        collectSelectMenus();
        collectAutoCompleters();
    }

    public JDAInteractions() {
        this.reflections = new Reflections(Scanners.FieldsAnnotated, Scanners.MethodsAnnotated, Scanners.TypesAnnotated);
        collectMappers();
        collectButtons();
        collectSelectMenus();
        collectAutoCompleters();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private OptionType getOptionType(Parameter parameter) {
        if (!mappers.containsKey(parameter.getType())) {
            throw new IllegalArgumentException("No mapper found for " + parameter.getType());
        }
        return OPTION_TYPE_CLASS_MAP.entrySet().stream().filter(entry -> entry.getValue().equals(mappers.get(parameter.getType()).getSourceType())).findFirst().get().getKey();
    }

    private void collectMappers() {
        for (Class<?> mapper : reflections.getTypesAnnotatedWith(Mapper.class)) {
            try {
                Object mapperInstance = mapper.getDeclaredConstructors()[0].newInstance();
                if (mapperInstance instanceof IMapper) {
                    if (mappers.containsKey(((IMapper<?, ?>) mapperInstance).getTargetType())) {
                        throw new IllegalArgumentException("Duplicate mapper: " + mapper.getPackageName());
                    }
                    if (!OPTION_TYPE_CLASS_MAP.containsValue(((IMapper<?, ?>) mapperInstance).getSourceType())) {
                        throw new IllegalArgumentException("Invalid mapper (source is not a predefined type): " + mapper.getPackageName());
                    }
                    mappers.put(((IMapper<?, ?>) mapperInstance).getTargetType(), (IMapper<?, ?>) mapperInstance);
                } else {
                    throw new IllegalArgumentException("Mapper " + mapper.getPackageName() + " is not an IMapper");
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("Could not instantiate mapper " + mapper.getPackageName());
            }
        }
    }

    private void collectButtons() {
        for (Method method : reflections.getMethodsAnnotatedWith(Button.class)) {
            Button button = method.getAnnotation(Button.class);
            String buttonName = button.value().isEmpty() ? method.getName() : button.value();
            if (buttons.containsKey(buttonName)) {
                throw new IllegalArgumentException("Duplicate button: " + buttonName);
            }
            if (method.getParameters().length != 1 || method.getParameters()[0].getType() != ButtonInteractionEvent.class) {
                throw new IllegalArgumentException("Invalid button (must contain exactly one parameter of type ButtonInteractionEvent): " + buttonName);
            }
            buttons.put(buttonName, method);
        }
    }

    private void collectSelectMenus() {
        for (Method method : reflections.getMethodsAnnotatedWith(SelectMenu.class)) {
            SelectMenu menu = method.getAnnotation(SelectMenu.class);
            String menuName = menu.value().isEmpty() ? method.getName() : menu.value();
            if (selectMenu.containsKey(menuName)) {
                throw new IllegalArgumentException("Duplicate select menu: " + menuName);
            }
            if (method.getParameters().length != 1 || method.getParameters()[0].getType() != SelectMenuInteractionEvent.class) {
                throw new IllegalArgumentException("Invalid button (must contain exactly one parameter of type SelectMenuInteractionEvent): " + menuName);
            }
            selectMenu.put(menuName, method);
        }
    }

    private void collectAutoCompleters() {
        for (Method method : reflections.getMethodsAnnotatedWith(AutoCompleter.class)) {
            AutoCompleter autoCompleter = method.getAnnotation(AutoCompleter.class);
            String autoCompleterName = autoCompleter.value().isEmpty() ? method.getName() : autoCompleter.value();
            if (autoCompleters.containsKey(autoCompleterName)) {
                throw new IllegalArgumentException("Duplicate auto completer: " + autoCompleterName);
            }
            if (method.getParameters().length != 1 || method.getParameters()[0].getType() != CommandAutoCompleteInteractionEvent.class) {
                throw new IllegalArgumentException("Invalid auto completer (must contain exactly one parameter of type CommandAutoCompleteInteractionEvent): " + autoCompleterName);
            }
            autoCompleters.put(autoCompleterName, method);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private OptionData getOptionFromParameter(Parameter parameter) {
        Option option = parameter.getAnnotation(Option.class);
        String optionName = option.value().isEmpty() ? parameter.getName().toLowerCase(Locale.ROOT) : option.value();
        OptionType optionType;
        Class<?> type = parameter.getType();
        if (OPTION_TYPE_CLASS_MAP.containsValue(type)) {
            optionType = OPTION_TYPE_CLASS_MAP.entrySet().stream().filter(entry -> entry.getValue().equals(type)).findFirst().get().getKey();
        } else {
            optionType = getOptionType(parameter);
        }

        if (option.autoComplete() && (!autoCompleters.containsKey(option.autoCompleter().isEmpty() ? optionName : option.autoCompleter()))) {
            throw new IllegalArgumentException("Auto completer not found for an autocomplete enabled option: " + optionName);
        }

        OptionData optionData = new OptionData(optionType, optionName,
                option.description().isEmpty() ? optionName : option.description(),
                !option.optional(), option.autoComplete());
        if (optionType == OptionType.INTEGER) {
            if (option.minLong() != Long.MIN_VALUE) {
                optionData.setMinValue(option.minLong());
            }
            if (option.maxLong() != Long.MAX_VALUE) {
                optionData.setMaxValue(option.maxLong());
            }
        }

        if (optionType == OptionType.NUMBER) {
            if (option.minDouble() != Double.MIN_VALUE) {
                optionData.setMinValue(option.minDouble());
            }
            if (option.maxDouble() != Double.MAX_VALUE) {
                optionData.setMaxValue(option.maxDouble());
            }
        }

        if (optionType == OptionType.CHANNEL) {
            optionData.setChannelTypes(option.channelTypes());
        }
        return optionData;
    }

    private OptionData[] collectOptions(Method method) {
        List<OptionData> options = new LinkedList<>();
        if (method.getParameters().length < 1 || !method.getParameters()[0].getType().equals(SlashCommandInteractionEvent.class)) {
            throw new IllegalArgumentException(
                    "Invalid slash command method " + method.getName() + ": no parameters (must have a SlashCommandInteractionEvent parameter as its first parameter)");
        }
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(Option.class)) {
                options.add(getOptionFromParameter(parameter));
            } else {
                if (!parameter.getType().equals(SlashCommandInteractionEvent.class)) {
                    throw new IllegalArgumentException("Method " + method.getName() + " has a parameter that is not annotated with Option or SlashCommandInteractionEvent");
                }
            }
        }
        return options.toArray(new OptionData[0]);
    }

    private SubcommandData[] collectSubcommands(Class<?> clazz, String rootPath) {
        Map<String, SubcommandData> subcommands = new HashMap<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Subcommand.class)) {
                Subcommand subcommand = method.getAnnotation(Subcommand.class);
                String subcommandName = subcommand.value().isEmpty() ? method.getName().toLowerCase(Locale.ROOT) : subcommand.value();
                if (subcommands.containsKey(subcommandName)) {
                    throw new IllegalArgumentException("Duplicated subcommand: " + subcommandName);
                }
                subcommands.put(subcommandName,
                        new SubcommandData(subcommandName, subcommand.description().isEmpty() ? subcommandName : subcommand.description())
                                .addOptions(collectOptions(method)));
                this.commands.put(rootPath + "/" + subcommandName, method);
            }
        }
        return subcommands.values().toArray(SubcommandData[]::new);
    }

    private SubcommandGroupData[] collectSubcommandGroups(Class<?> rootClass, String rootPath) {
        Map<String, SubcommandGroupData> subcommandGroups = new HashMap<>();
        for (Class<?> clazz : rootClass.getDeclaredClasses()) {
            if (clazz.isAnnotationPresent(Subcommand.class)) {
                Subcommand subcommand = clazz.getAnnotation(Subcommand.class);
                String subcommandName = subcommand.value().isEmpty() ? clazz.getSimpleName().toLowerCase(Locale.ROOT) : subcommand.value();
                if (subcommandGroups.containsKey(subcommandName)) {
                    throw new IllegalArgumentException("Duplicated subcommand group: " + subcommandName);
                }
                subcommandGroups.put(subcommandName,
                        new SubcommandGroupData(subcommandName, subcommand.description().isEmpty() ? subcommandName : subcommand.description())
                                .addSubcommands(collectSubcommands(clazz, rootPath + "/" + subcommandName)));
            }
        }
        return subcommandGroups.values().toArray(SubcommandGroupData[]::new);
    }

    private CommandData[] collectCommands() {
        Map<String, CommandData> commands = new HashMap<>();
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Command.class)) {
            Command command = clazz.getAnnotation(Command.class);
            String commandName = command.value().isEmpty() ? clazz.getSimpleName().toLowerCase(Locale.ROOT) : command.value();
            if (commands.containsKey(commandName)) {
                throw new IllegalArgumentException("Duplicate slash command: " + commandName);
            }
            commands.put(commandName, Commands.slash(commandName, command.description().isEmpty() ? commandName : command.description())
                    .addSubcommandGroups(collectSubcommandGroups(clazz, commandName))
                    .addSubcommands(collectSubcommands(clazz, commandName)));
        }
        for (Method method : reflections.getMethodsAnnotatedWith(Command.class)) {
            Command command = method.getAnnotation(Command.class);
            String commandName = command.value().isEmpty() ? method.getName().toLowerCase(Locale.ROOT) : command.value();
            if (commands.containsKey(commandName)) {
                throw new IllegalArgumentException("Duplicate slash command: " + commandName);
            } else {
                commands.put(commandName, Commands.slash(commandName, command.description().isEmpty() ? commandName : command.description())
                        .addOptions(collectOptions(method)));
                this.commands.put(commandName, method);
            }
        }
        return commands.values().toArray(CommandData[]::new);
    }

    private CommandData[] collectContextCommands() {
        Map<String, CommandData> ctxCommands = new HashMap<>();
        for (Method method : reflections.getMethodsAnnotatedWith(MessageCtx.class)) {
            if (method.getParameters().length != 1) {
                throw new IllegalArgumentException("Invalid message context command (invalid amount of parameters, must be 1): " + method.getName());
            }
            if (!method.getParameters()[0].getType().equals(MessageContextInteractionEvent.class)) {
                throw new IllegalArgumentException("Invalid message context command (invalid parameter type, must be MessageContextInteractionEvent): " + method.getName());
            }
            MessageCtx ctx = method.getAnnotation(MessageCtx.class);
            String commandName = ctx.value().isEmpty() ? method.getName() : ctx.value();
            ctxCommands.put(commandName, Commands.message(commandName));
            this.messageContextCommands.put(commandName, method);
        }
        for (Method method : reflections.getMethodsAnnotatedWith(UserCtx.class)) {
            if (method.getParameters().length != 1) {
                throw new IllegalArgumentException("Invalid user context command (invalid amount of parameters, must be 1): " + method.getName());
            }
            if (!method.getParameters()[0].getType().equals(UserContextInteractionEvent.class)) {
                throw new IllegalArgumentException("Invalid user context command (invalid parameter type, must be UserContextInteractionEvent): " + method.getName());
            }
            UserCtx ctx = method.getAnnotation(UserCtx.class);
            String commandName = ctx.value().isEmpty() ? method.getName() : ctx.value();
            ctxCommands.put(commandName, Commands.user(commandName));
            this.userContextCommands.put(commandName, method);
        }
        return ctxCommands.values().toArray(CommandData[]::new);
    }

    public CommandListUpdateAction registerInteractions(ShardManager shardManager) {
        shardManager.addEventListener(
                new AutoCompleteListener(this),
                new ButtonListener(this),
                new MessageContextCommandListener(this),
                new SlashCommandListener(this),
                new UserContextCommandListener(this),
                new SelectMenuListener(this)
        );
        return Objects.requireNonNull(shardManager.getShardById(0)).updateCommands()
                .addCommands(collectCommands()).addCommands(collectContextCommands());
    }

    public CommandListUpdateAction registerInteractions(JDA jda) {
        jda.addEventListener(
                new AutoCompleteListener(this),
                new ButtonListener(this),
                new MessageContextCommandListener(this),
                new SlashCommandListener(this),
                new UserContextCommandListener(this),
                new SelectMenuListener(this)
        );
        return jda.updateCommands().addCommands(collectCommands()).addCommands(collectContextCommands());
    }

    public CommandListUpdateAction registerInteractions(Guild guild) {
        guild.getJDA().addEventListener(
                new AutoCompleteListener(this),
                new ButtonListener(this),
                new MessageContextCommandListener(this),
                new SlashCommandListener(this),
                new UserContextCommandListener(this),
                new SelectMenuListener(this)
        );
        return guild.updateCommands().addCommands(collectCommands()).addCommands(collectContextCommands());
    }
}
