package com.synopsys.integration.detect.configuration;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.base.NullableProperty;
import com.synopsys.integration.configuration.property.base.TypedProperty;
import com.synopsys.integration.configuration.property.base.ValuedProperty;
import com.synopsys.integration.configuration.property.types.bool.NullableBooleanProperty;
import com.synopsys.integration.configuration.property.types.enumallnone.property.AllEnumListProperty;
import com.synopsys.integration.configuration.property.types.enumallnone.property.AllNoneEnumListProperty;
import com.synopsys.integration.configuration.property.types.enumallnone.property.NoneEnumListProperty;
import com.synopsys.integration.configuration.property.types.path.NullablePathProperty;
import com.synopsys.integration.configuration.property.types.path.PathProperty;
import com.synopsys.integration.configuration.property.types.path.PathResolver;
import com.synopsys.integration.configuration.property.types.string.NullableStringProperty;
import com.synopsys.integration.detect.configuration.properties.DetectProperty;
import com.synopsys.integration.detect.configuration.properties.NullableStringDetectProperty;
import com.synopsys.integration.detect.configuration.properties.PassthroughDetectProperty;
import com.synopsys.integration.detect.configuration.properties.PathListDetectProperty;
import com.synopsys.integration.detectable.detectables.yarn.YarnDependencyType;

public class DetectPropertyConfiguration {
    private final PropertyConfiguration propertyConfiguration;
    private final PathResolver pathResolver;

    public DetectPropertyConfiguration(PropertyConfiguration propertyConfiguration, PathResolver pathResolver) {
        this.propertyConfiguration = propertyConfiguration;
        this.pathResolver = pathResolver;
    }

    public Path getPathOrNull(DetectProperty<NullablePathProperty> property) {
        return propertyConfiguration.getValue(property.getProperty()).map(path -> path.resolvePath(pathResolver)).orElse(null);
    }

    public Path getPath(DetectProperty<PathProperty> property) {
        return propertyConfiguration.getValue(property.getProperty()).resolvePath(pathResolver);
    }

    public List<Path> getPaths(PathListDetectProperty property) {
        return propertyConfiguration.getValue(property.getProperty()).stream()
            .map(path -> path.resolvePath(pathResolver))
            .collect(Collectors.toList());
    }

    public <V, R, T extends NullableProperty<V, R>> R getNullableValue(DetectProperty<T> detectProperty) {
        return propertyConfiguration.getValue(detectProperty.getProperty()).orElse(null);
    }

    public <V, R, T extends ValuedProperty<V, R>> R getValue(DetectProperty<T> detectProperty) {
        return propertyConfiguration.getValue(detectProperty.getProperty());
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

    public Map<String, String> getRaw(PassthroughDetectProperty phonehomePassthrough) {
        return propertyConfiguration.getRaw(phonehomePassthrough.getProperty());
    }

    public Map<String, String> getRaw(Set<String> keys) {
        return propertyConfiguration.getRaw(keys);
    }

    public <V, R, T extends TypedProperty<V, R>> boolean wasPropertyProvided(DetectProperty<T> property) {
        return propertyConfiguration.wasPropertyProvided(property.getProperty());
    }
}
