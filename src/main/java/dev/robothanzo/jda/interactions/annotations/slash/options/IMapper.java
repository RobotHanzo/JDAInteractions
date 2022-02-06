package dev.robothanzo.jda.interactions.annotations.slash.options;

/**
 * Maps a pre-defined type (S) to a custom type (T).
 * Must be annotated with {@link Mapper} or it won't register automatically.
 * Must have one and only one public constructor with no parameters.
 */
public interface IMapper<S, T> {
    Class<S> getSourceType();

    Class<T> getTargetType();

    T map(Object source);
}
