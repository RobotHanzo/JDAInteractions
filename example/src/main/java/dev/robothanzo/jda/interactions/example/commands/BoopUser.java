package dev.robothanzo.jda.interactions.example.commands;

import dev.robothanzo.jda.interactions.annotations.context.UserCtx;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public class BoopUser {
    @UserCtx
    public void Boop(UserContextInteractionEvent event) {
        event.getInteraction().reply("You have booped " + event.getUser().getAsMention()).queue();
    }
}
