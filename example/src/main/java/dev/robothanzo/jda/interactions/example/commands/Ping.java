package dev.robothanzo.jda.interactions.example.commands;

import dev.robothanzo.jda.interactions.annotations.slash.Command;
import dev.robothanzo.jda.interactions.annotations.slash.options.Option;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Ping {
    @Command(description = "Pong!")
    public void ping(SlashCommandInteractionEvent event, @Option(value = "show_latency", description = "Whether to show the latency") Boolean show_latency) {
        if(show_latency) {
            event.reply("Pong! Latency: " + event.getJDA().getGatewayPing() + "ms").queue();
        } else {
            event.reply("Pong!").queue();
        }
    }
}
