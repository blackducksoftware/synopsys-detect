package com.synopsys.integration.configuration.property;

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
