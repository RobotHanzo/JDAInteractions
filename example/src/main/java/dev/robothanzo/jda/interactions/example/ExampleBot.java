package dev.robothanzo.jda.interactions.example;

import dev.robothanzo.jda.interactions.JDAInteractions;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.util.Objects;

public class ExampleBot {
    public static Dotenv dotenv = Dotenv.load();
    public static JDA jda;

    public static void main(String[] args) throws LoginException, InterruptedException {
        jda = JDABuilder.createDefault(dotenv.get("TOKEN")).build();
        JDAInteractions jdaInteractions = new JDAInteractions("dev.robothanzo.jda.interactions.example");
        if (dotenv.get("GUILD", null) != null) {
            jda.awaitReady();
            jdaInteractions.registerInteractions(Objects.requireNonNull(jda.getGuildById(dotenv.get("GUILD")))).queue();
        } else {
            jdaInteractions.registerInteractions(jda).queue();
        }
    }
}
