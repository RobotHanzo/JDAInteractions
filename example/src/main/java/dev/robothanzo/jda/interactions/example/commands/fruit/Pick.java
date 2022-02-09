package dev.robothanzo.jda.interactions.example.commands.fruit;

import dev.robothanzo.jda.interactions.annotations.slash.Command;
import dev.robothanzo.jda.interactions.annotations.slash.Subcommand;
import dev.robothanzo.jda.interactions.annotations.slash.options.Option;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(description = "Pick something")
public class Pick {
    @Subcommand(description = "Pick groceries")
    public static class Groceries {
        @Subcommand(description = "Pick fruits")
        public void fruits(SlashCommandInteractionEvent event, @Option(value = "fruit", description = "Which fruit", autoComplete = true) String fruit) {
            event.reply("You picked a " + fruit).queue();
        }
    }
}
