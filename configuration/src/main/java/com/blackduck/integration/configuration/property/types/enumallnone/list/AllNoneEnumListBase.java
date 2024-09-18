package com.blackduck.integration.configuration.property.types.enumallnone.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackduck.integration.configuration.property.types.enumextended.ExtendedEnumValue;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.Nullable;

public class AllNoneEnumListBase<E extends Enum<E>, B extends Enum<B>> implements AllNoneEnumCollection<B> {
    private final List<ExtendedEnumValue<E, B>> providedValues;
    private final Class<B> enumClass;
    private final E noneValue;
    private final E allValue;

    public AllNoneEnumListBase(List<ExtendedEnumValue<E, B>> providedValues, Class<B> enumClass, @Nullable E noneValue, @Nullable E allValue) {
        this.providedValues = providedValues;
        this.enumClass = enumClass;
        this.noneValue = noneValue;
        this.allValue = allValue;
    }

    public boolean containsNone() {
        if (noneValue == null)
            return false;
        return providedValues.stream()
            .map(ExtendedEnumValue::getExtendedValue)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .anyMatch(noneValue::equals);
    }

    public boolean containsAll() {
        if (allValue == null)
            return false;
        return providedValues.stream()
            .map(ExtendedEnumValue::getExtendedValue)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .anyMatch(allValue::equals);
    }

    public boolean containsValue(B value) {
        if (value == null)
            return false;
        return providedValues.stream()
            .map(ExtendedEnumValue::getBaseValue)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .anyMatch(value::equals);
    }

    public boolean isEmpty() {
        return providedValues.isEmpty();
    }

    public List<B> toPresentValues() {
        return providedValues.stream()
            .map(ExtendedEnumValue::getBaseValue)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    public List<B> representedValues() {
        if (containsNone()) {
            return new ArrayList<>();
        } else if (containsAll()) {
            return EnumUtils.getEnumList(enumClass);
        } else {
            return toPresentValues();
        }
    }

    public Set<B> representedValueSet() {
        return new HashSet<>(representedValues());
    }

    public List<ExtendedEnumValue<E, B>> toProvidedValues() {
        return providedValues;
    }
}