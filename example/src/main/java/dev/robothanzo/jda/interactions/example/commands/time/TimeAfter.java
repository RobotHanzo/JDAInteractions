package dev.robothanzo.jda.interactions.example.commands.time;

import dev.robothanzo.jda.interactions.annotations.slash.Command;
import dev.robothanzo.jda.interactions.annotations.slash.options.Option;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.time.Duration;

public class TimeAfter {
    @Command(description = "Gets the time after a certain time")
    public void time_after(SlashCommandInteractionEvent event, @Option(value = "duration", description = "The duration to get the time after") Duration duration) {
        event.reply(TimeFormat.DATE_TIME_LONG.after(duration).toString()).queue();
    }
}
