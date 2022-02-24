package com.synopsys.integration.detect.configuration;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.base.NullableProperty;
import com.synopsys.integration.configuration.property.base.PassthroughProperty;
import com.synopsys.integration.configuration.property.base.TypedProperty;
import com.synopsys.integration.configuration.property.base.ValuedProperty;
import com.synopsys.integration.configuration.property.types.path.NullablePathProperty;
import com.synopsys.integration.configuration.property.types.path.PathListProperty;
import com.synopsys.integration.configuration.property.types.path.PathProperty;
import com.synopsys.integration.configuration.property.types.path.PathResolver;

public class DetectPropertyConfiguration {
    private final PropertyConfiguration propertyConfiguration;
    private final PathResolver pathResolver;

    public DetectPropertyConfiguration(PropertyConfiguration propertyConfiguration, PathResolver pathResolver) {
        this.propertyConfiguration = propertyConfiguration;
        this.pathResolver = pathResolver;
    }

    public Path getPathOrNull(NullablePathProperty property) {
        return propertyConfiguration.getValue(property).map(path -> path.resolvePath(pathResolver)).orElse(null);
    }

    public Path getPath(PathProperty property) {
        return propertyConfiguration.getValue(property).resolvePath(pathResolver);
    }

    public List<Path> getPaths(PathListProperty property) {
        return propertyConfiguration.getValue(property).stream()
            .map(path -> path.resolvePath(pathResolver))
            .collect(Collectors.toList());
    }

    public <V, R> R getNullableValue(NullableProperty<V, R> detectProperty) {
        return propertyConfiguration.getValue(detectProperty).orElse(null);
    }

    public <V, R> R getValue(ValuedProperty<V, R> detectProperty) {
        return propertyConfiguration.getValue(detectProperty);
    }

    @SafeVarargs
    public static <V, R> Optional<R> getFirstProvidedValueOrEmpty(PropertyConfiguration propertyConfiguration, NullableProperty<V, R>... properties) {
        for (NullableProperty<V, R> property : properties) {
            if (propertyConfiguration.wasPropertyProvided(property)) {
                return propertyConfiguration.getValueOrEmpty(property);
            }
        }

        return Optional.empty();
    }

    /**
     * Will get the first property in a list that was provided by the user.
     * If no property was provided, the default value of the first property will be used.
     */
    @SafeVarargs
    public static <V, R> R getFirstProvidedValueOrDefault(@NotNull PropertyConfiguration propertyConfiguration, @NotNull ValuedProperty<V, R>... properties) {
        Optional<R> value = getFirstProvidedValueOrEmpty(propertyConfiguration, properties);
        return value.orElseGet(() -> properties[0].convertValue(properties[0].getDefaultValue()));

    }

    /**
     * Will get the first property in a list that was provided by the user.
     * If no property was provided, the default will NOT be used.
     */
    @SafeVarargs
    public static <V, R> Optional<R> getFirstProvidedValueOrEmpty(@NotNull PropertyConfiguration propertyConfiguration, @NotNull ValuedProperty<V, R>... properties) {
        for (ValuedProperty<V, R> property : properties) {
            if (propertyConfiguration.wasPropertyProvided(property)) {
                return Optional.of(propertyConfiguration.getValueOrDefault(property));
            }
        }

        return Optional.empty();
    }

    public Map<String, String> getRaw() {
        return propertyConfiguration.getRaw();
    }

    public Map<String, String> getRaw(Set<String> keys) {
        return propertyConfiguration.getRaw(keys);
    }

    public Map<String, String> getRaw(PassthroughProperty passthroughProperty) {
        return propertyConfiguration.getRaw(passthroughProperty);
    }

    public <V, R> boolean wasPropertyProvided(TypedProperty<V, R> property) {
        return propertyConfiguration.wasPropertyProvided(property);
    }
}
