package com.blackducksoftware.integration.hub.detect.extraction.strategy;

import com.blackducksoftware.integration.hub.detect.type.ExecutableType;

public class StrategyBuilder<C> {

    public ExecutableRequirementBuilder requireExecutable(final ExecutableType type) {
        return new ExecutableRequirementBuilder(this, type);
    }

    public FileRequirementBuilder<C> requireFile(final String file) {
        return new FileRequirementBuilder<>(this, file);
    }

    public StrategyContextBuilder<C> asContext(final Class<C> type) {
        return new StrategyContextBuilder<>();
    }

    public interface Then<T, C> {
        void action(T value, C context);
    }

    public class ExecutableRequirementBuilder {
        private final StrategyBuilder parent;
        private String key;
        public ExecutableType type;
        public ExecutableRequirementBuilder(final StrategyBuilder parent, final ExecutableType type) {
            this.type = type;
            this.parent = parent;

        }
        public String getKey() {
            return key;
        }
        public void setKey(final String key) {
            this.key = key;
        }
        public StrategyBuilder then(final Then<String, C> action) {
            this.setKey(key);
            return parent;
        }
    }

    public class FileRequirementBuilder<C> {
        private final StrategyBuilder parent;
        private String key;
        public String filename;
        public FileRequirementBuilder(final StrategyBuilder parent, final String filename) {
            this.filename = filename;
            this.parent = parent;
        }
        public String getKey() {
            return key;
        }
        public void setKey(final String key) {
            this.key = key;
        }
        public StrategyBuilder then(final Then<String, C> action) {
            this.setKey(key);
            return parent;
        }
    }

}
