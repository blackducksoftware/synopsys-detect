package com.synopsys.integration.configuration.property;

@FunctionalInterface
public interface PropertySetter<T extends Property> {

    public void set(T property);
}
