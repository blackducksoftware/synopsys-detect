package com.synopsys.integration.configuration.property;

import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.util.Category;
import com.synopsys.integration.configuration.util.Group;

public class PropertyBuilder<T extends Property> {

    private T property;

    public PropertyBuilder(T property) {
        this.property = property;
    }

    public T build(PropertySetter<T> propertySetter) {
        propertySetter.set(property);
        return property;
    }
}
