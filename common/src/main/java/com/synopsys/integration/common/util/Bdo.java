package com.synopsys.integration.common.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//Black Duck Optional
public class Bdo<T> {
    private final T value;

    private Bdo(T value) {
        this.value = value;
    }

    public static <T> Bdo<T> of(T value) {
        return new Bdo<>(value);
    }

    public static <T> Bdo<T> of(Optional<T> value) {
        return Bdo.of(value.orElse(null));
    }

    public static <T> Bdo<T> empty() {
        return new Bdo<>(null);
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(value);
    }

    public <U> Bdo<U> map(Function<? super T, U> mapper) {
        return Bdo.of(mapper.apply(value));
    }

    public Bdo<T> peek(Consumer<T> consumer) {
        consumer.accept(value);
        return this;
    }

    public boolean isPresent() {
        return toOptional().isPresent();
    }

    public boolean isNotPresent() {
        return !isPresent();
    }

    public T get() {
        return toOptional().get();
    }

    public Bdo<T> or(Optional<T> value) {
        return or(Bdo.of(value));
    }

    public Bdo<T> or(@NotNull Bdo<T> value) {
        if (isPresent()) {
            return this;
        } else {
            return value;
        }
    }

    public Bdo<T> or(@Nullable T value) {
        if (isPresent()) {
            return this;
        } else {
            return Bdo.of(value);
        }
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return toOptional().orElseThrow(exceptionSupplier);
    }

    public <U> Bdo<U> flatMap(Function<T, Optional<U>> operator) {
        if (isPresent()) {
            return Bdo.of(operator.apply(get()));
        } else {
            return Bdo.empty();
        }
    }
}
