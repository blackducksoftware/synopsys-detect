package com.synopsys.integration.configuration.property.types.enumfilterable;

import java.util.Objects;
import java.util.Optional;

public class FilterableEnumValue<E extends Enum<E>> {
    private boolean all;
    private boolean none;
    private E value;

    private FilterableEnumValue(boolean all, boolean none, E value) {
        this.all = all;
        this.none = none;
        this.value = value;
    }

    public boolean isAll() {
        return all;
    }

    public boolean isNone() {
        return none;
    }

    public Optional<E> getValue() {
        return Optional.ofNullable(value);
    }

    public static <E extends Enum<E>> FilterableEnumValue<E> allValue() {
        return new FilterableEnumValue<>(true, false, null);
    }

    public static <E extends Enum<E>> FilterableEnumValue<E> noneValue() {
        return new FilterableEnumValue<>(false, true, null);
    }

    public static <E extends Enum<E>> FilterableEnumValue<E> value(E value) {
        return new FilterableEnumValue<>(false, false, value);
    }

    @Override
    public String toString() {
        if (isAll()) {
            return "ALL";
        } else if (isNone()) {
            return "NONE";
        } else {
            return value.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilterableEnumValue<?> that = (FilterableEnumValue<?>) o;
        return all == that.all &&
            none == that.none &&
            Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(all, none, value);
    }
}