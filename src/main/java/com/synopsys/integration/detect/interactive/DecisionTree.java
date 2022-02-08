package com.synopsys.integration.detect.interactive;

/**
 * A piece of interactive mode, through which the user interacts to set a
 * logical grouping of properties.
 *
 * Implementers should be constructed with any data that they need outside of
 * the properties that are populated within the
 * {@link com.synopsys.integration.detect.interactive.InteractivePropertySourceBuilder}
 * and only ever implement
 * {@link com.synopsys.integration.detect.interactive.DecisionTree#traverse}
 * to mutate it as creating multiple methods within a DecisionTree defeats the
 * point of the interface.
 */
public interface DecisionTree {
    void traverse(InteractivePropertySourceBuilder propertySourceBuilder, InteractiveWriter writer);
}
