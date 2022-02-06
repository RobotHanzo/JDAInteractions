package dev.robothanzo.jda.interactions.example.commands.time;

import dev.robothanzo.jda.interactions.annotations.slash.options.IMapper;
import dev.robothanzo.jda.interactions.annotations.slash.options.Mapper;

import java.time.Duration;
import java.util.Locale;

@Mapper
public class DurationMapper implements IMapper<String, Duration> {
    @Override
    public Class<String> getSourceType() {
        return String.class;
    }

    @Override
    public Class<Duration> getTargetType() {
        return Duration.class;
    }

    @Override
    public Duration map(Object source) {
        return Duration.parse("PT" + source.toString().toUpperCase(Locale.ROOT));
    }
}
