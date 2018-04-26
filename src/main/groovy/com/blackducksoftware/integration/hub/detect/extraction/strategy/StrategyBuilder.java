package com.blackducksoftware.integration.hub.detect.extraction.strategy;

import com.blackducksoftware.integration.hub.detect.type.ExecutableType;

public class StrategyBuilder<C, E> {

    public StrategyBuilder(final Class<C> contextClass, final Class<E> extractorClass) {

    }

    public interface Then<T, C> {
        void action(T value, C context);
    }

    public ExecutableRequirementBuilder requireExecutable(final ExecutableType type) {
        return new ExecutableRequirementBuilder(this, type);
    }

    public FileRequirementBuilder requireFile(final String file) {
        return new FileRequirementBuilder(this, file);
    }

    public class ExecutableRequirementBuilder {
        private final StrategyBuilder<C, E> parent;
        public ExecutableType type;
        public ExecutableRequirementBuilder(final StrategyBuilder<C, E> parent, final ExecutableType type) {
            this.type = type;
            this.parent = parent;

        }
        public StrategyBuilder<C, E> then(final Then<String, C> action) {
            return parent;
        }
    }

    public class FileRequirementBuilder {
        private final StrategyBuilder<C, E> parent;
        public String filename;
        public FileRequirementBuilder(final StrategyBuilder<C, E> parent, final String filename) {
            this.filename = filename;
            this.parent = parent;
        }
        public StrategyBuilder<C, E> then(final Then<String, C> action) {
            return parent;
        }
    }

}
