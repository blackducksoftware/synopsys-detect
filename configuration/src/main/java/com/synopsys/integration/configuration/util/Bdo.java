package com.synopsys.integration.configuration.util;

import java.util.Optional;
import java.util.function.Function;

public class Bdo<T> {
    public Optional<T> toOptional() {
        return optional;
    }

    public boolean isPresent() {
        return optional.isPresent();
    }

    public T get() {
        return optional.get();
    }

    private Optional<T> optional;

    public Bdo(final Optional<T> optional) {
        this.optional = optional;
    }

    public static <T> Bdo<T> of(final Optional<T> value) {
        return new Bdo<T>(value);
    }

    public static <T> Bdo<T> empty() {
        return new Bdo<T>(Optional.empty());
    }

    /**
     * Returns the first Optional that isPresent.
     */
    // TODO: Move to new Bdo (BlackDuckOptional vs BlackDuckStream)?
    @SafeVarargs
    public static <T> Bdo<T> or(final Bdo<T>... values) {
        for (final Bdo<T> value : values) {
            if (value.isPresent()) {
                return value;
            }
        }

        return Bdo.empty();
    }

    public <U> Bdo<U> flatMap(final Function<T, Optional<U>> operator) {
        if (isPresent()) {
            return new Bdo<U>(operator.apply(get()));
        } else {
            return Bdo.empty();
        }
    }
}
