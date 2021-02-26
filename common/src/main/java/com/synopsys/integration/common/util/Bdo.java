/**
 * common
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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

    private Bdo(final T value) {
        this.value = value;
    }

    public static <T> Bdo<T> of(final T value) {
        return new Bdo<>(value);
    }

    public static <T> Bdo<T> of(final Optional<T> value) {
        return Bdo.of(value.orElse(null));
    }

    public static <T> Bdo<T> empty() {
        return new Bdo<>(null);
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(value);
    }

    public <U> Bdo<U> map(final Function<? super T, U> mapper) {
        return Bdo.of(mapper.apply(value));
    }

    public Bdo<T> peek(final Consumer<T> consumer) {
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

    public Bdo<T> or(final Optional<T> value) {
        return or(Bdo.of(value));
    }

    public Bdo<T> or(@NotNull final Bdo<T> value) {
        if (isPresent()) {
            return this;
        } else {
            return value;
        }
    }

    public Bdo<T> or(@Nullable final T value) {
        if (isPresent()) {
            return this;
        } else {
            return Bdo.of(value);
        }
    }

    public <X extends Throwable> T orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X {
        return toOptional().orElseThrow(exceptionSupplier);
    }

    public <U> Bdo<U> flatMap(final Function<T, Optional<U>> operator) {
        if (isPresent()) {
            return Bdo.of(operator.apply(get()));
        } else {
            return Bdo.empty();
        }
    }
}
