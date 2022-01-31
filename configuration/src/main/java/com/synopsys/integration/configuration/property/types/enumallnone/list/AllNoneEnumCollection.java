package com.synopsys.integration.configuration.property.types.enumallnone.list;

import java.util.List;
import java.util.Set;

public interface AllNoneEnumCollection<T extends Enum<T>> {
    boolean containsNone();

    boolean containsAll();

    boolean containsValue(T value);

    boolean isEmpty();

    List<T> toPresentValues();

    List<T> representedValues();

    Set<T> representedValueSet();
}
