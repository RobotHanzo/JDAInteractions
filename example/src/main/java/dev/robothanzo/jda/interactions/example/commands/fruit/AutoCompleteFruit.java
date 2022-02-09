package dev.robothanzo.jda.interactions.example.commands.fruit;

import dev.robothanzo.jda.interactions.annotations.slash.options.AutoCompleter;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.LinkedList;
import java.util.List;

public class AutoCompleteFruit {
    public static List<String> fruits = List.of("apple", "banana", "cherry", "durian", "elderberry", "fig", "grape",
            "honeydew", "jackfruit", "kiwi", "lemon", "lime", "mango", "nectarine", "orange", "papaya", "peach",
            "pear", "pineapple", "plum", "pomegranate", "raspberry", "strawberry", "tangerine", "watermelon"); // Suggested by GitHub Copilot lol

    @AutoCompleter
    public void fruit(CommandAutoCompleteInteractionEvent event) {
        List<Command.Choice> choices = new LinkedList<>();
        for (String fruit : fruits) {
            if (fruit.startsWith(event.getFocusedOption().getValue())) {
                choices.add(new Command.Choice(fruit.substring(0, 1).toUpperCase() + fruit.substring(1), fruit));
            }
        }
        event.replyChoices(choices).queue();
    }
}
