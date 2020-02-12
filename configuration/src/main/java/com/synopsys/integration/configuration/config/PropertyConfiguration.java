package com.synopsys.integration.configuration.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.util.Assert;

import com.synopsys.integration.configuration.config.resolution.NoPropertyResolution;
import com.synopsys.integration.configuration.config.resolution.PropertyResolution;
import com.synopsys.integration.configuration.config.resolution.PropertyResolutionInfo;
import com.synopsys.integration.configuration.config.resolution.SourcePropertyResolution;
import com.synopsys.integration.configuration.config.value.ExceptionPropertyValue;
import com.synopsys.integration.configuration.config.value.NoValuePropertyValue;
import com.synopsys.integration.configuration.config.value.PropertyValue;
import com.synopsys.integration.configuration.config.value.ValuedPropertyValue;
import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.base.NullableProperty;
import com.synopsys.integration.configuration.property.base.PassthroughProperty;
import com.synopsys.integration.configuration.property.base.TypedProperty;
import com.synopsys.integration.configuration.property.base.ValuedProperty;
import com.synopsys.integration.configuration.source.PropertySource;

public class PropertyConfiguration {
    private Map<String, PropertyResolution> resolutionCache = new HashMap<>();
    private Map<String, PropertyValue> valueCache = new HashMap<>();
    private List<PropertySource> orderedPropertySources;

    public PropertyConfiguration(List<PropertySource> orderedPropertySources) {
        this.orderedPropertySources = orderedPropertySources;
    }

    //region
    @NotNull
    public <T> Optional<T> getValueOrEmpty(@NotNull NullableProperty<T> property) {
        Assert.notNull(property, "Must provide a property.");
        try {
            return getValue(property);
        } catch (InvalidPropertyException e) {
            return Optional.empty();
        }
    }

    @NotNull
    public <T> T getValueOrDefault(@NotNull ValuedProperty<T> property) {
        Assert.notNull(property, "Must provide a property.");
        try {
            return getValue(property);
        } catch (InvalidPropertyException e) {
            return property.getDefault();
        }
    }

    @NotNull
    public <T> Optional<T> getValue(@NotNull NullableProperty<T> property) throws InvalidPropertyException {
        Assert.notNull(property, "Must provide a property.");
        PropertyValue<T> value = valueFromCache(property);
        if (value.getValue().isPresent()) {
            return value.getValue();
        } else if (value.getException().isPresent() && value.getResolutionInfo().isPresent()) {
            throw new InvalidPropertyException(property.getKey(), value.getResolutionInfo().get().getSource(), value.getException().get());
        } else {
            final Optional<T> empty = Optional.empty();
            return empty;
        }
    }

    @NotNull
    public <T> T getValue(@NotNull ValuedProperty<T> property) throws InvalidPropertyException {
        Assert.notNull(property, "Must provide a property.");
        PropertyValue<T> value = valueFromCache(property);
        if (value.getValue().isPresent()) {
            return value.getValue().get();
        } else if (value.getException().isPresent() && value.getResolutionInfo().isPresent()) {
            throw new InvalidPropertyException(property.getKey(), value.getResolutionInfo().get().getSource(), value.getException().get());
        } else {
            return property.getDefault();
        }
    }

    public boolean wasKeyProvided(@NotNull String key) {
        Assert.notNull(key, "Must provide a property.");
        return resolveFromCache(key).getResolutionInfo().isPresent();
    }

    public <T> boolean wasPropertyProvided(@NotNull TypedProperty<T> property) {
        Assert.notNull(property, "Must provide a property.");
        return wasKeyProvided(property.getKey());
    }

    public Optional<String> getPropertySource(@NotNull Property property) {
        Assert.notNull(property, "Must provide a property.");
        return resolveFromCache(property.getKey()).getResolutionInfo().map(PropertyResolutionInfo::getSource);
    }

    public Optional<String> getPropertyOrigin(@NotNull Property property) {
        Assert.notNull(property, "Must provide a property.");
        return resolveFromCache(property.getKey()).getResolutionInfo().map(PropertyResolutionInfo::getRaw);
    }

    @NotNull
    public Set<String> getKeys() {
        return orderedPropertySources.stream()
                   .map(PropertySource::getKeys)
                   .flatMap(Set::stream)
                   .collect(Collectors.toSet());
    }

    public <T> Optional<ValueParseException> getPropertyException(@NotNull TypedProperty<T> property) {
        Assert.notNull(property, "Must provide a property.");
        return valueFromCache(property).getException();
    }

    //endregion

    //region Advanced Usage
    public Optional<String> getRaw(@NotNull Property property) {
        Assert.notNull(property, "Must supply a property get raw keys.");
        PropertyResolution propertyResolution = resolveFromCache(property.getKey());
        return propertyResolution.getResolutionInfo().map(PropertyResolutionInfo::getRaw);
    }

    @NotNull
    public Map<String, String> getRaw() {
        return getRaw((String key) -> true);
    }

    @NotNull
    public Map<String, String> getRaw(@NotNull Set<String> keys) {
        Assert.notNull(keys, "Must supply a set of keys to get raw keys");
        return getRaw(keys::contains);
    }

    @NotNull
    public Map<String, String> getRaw(@NotNull Predicate<String> predicate) {
        Assert.notNull(predicate, "Must supply a predicate to get raw keys");

        Set<String> keys = getKeys().stream()
                               .filter(predicate)
                               .filter(Objects::nonNull)
                               .collect(Collectors.toSet());

        Map<String, String> keyMap = new HashMap<>();
        keys.forEach(key -> {
            PropertyResolution resolution = resolveFromCache(key);
            if (resolution != null && resolution.getResolutionInfo().isPresent()) {
                String rawValue = resolution.getResolutionInfo().map(PropertyResolutionInfo::getRaw).get();
                keyMap.put(key, rawValue);
            }
        });
        return keyMap;
    }

    // Takes in a 'passthrough.key' and returns key map (whose keys have that value removed)
    // So value 'passthrough.key.example' is returned as 'example'
    @NotNull
    public Map<String, String> getRaw(@NotNull PassthroughProperty property) {
        Assert.notNull(property, "Must supply a passthrough to get raw keys");

        Map<String, String> rawValues = getRaw((String key) -> key.startsWith(property.getKey()));
        Map<String, String> trimmedKeys = new HashMap<>();
        for (String key : rawValues.keySet()) {
            if (rawValues.get(key) != null) {
                trimmedKeys.put(property.trimKey(key), rawValues.get(key));
            }
        }
        return trimmedKeys;
    }
    //endregion Advanced Usage

    //region Implementation Details
    private PropertyResolution resolveFromCache(@NotNull String key) {
        Assert.notNull(key, "Cannot resolve a null key.");
        if (!resolutionCache.containsKey(key)) {
            resolutionCache.put(key, resolveFromPropertySources(key));
        }

        PropertyResolution value = resolutionCache.get(key);
        Assert.notNull(value, "Could not resolve a value, something has gone wrong with properties!");
        return value;
    }

    private PropertyResolution resolveFromPropertySources(@NotNull String key) {
        Assert.notNull(key, "Cannot resolve a null key.");
        for (PropertySource propertySource : orderedPropertySources) {
            if (propertySource.hasKey(key)) {
                String rawValue = propertySource.getValue(key);
                if (rawValue != null) {
                    String name = propertySource.getName();
                    String origin = propertySource.getOrigin(key);
                    PropertyResolutionInfo propertyResolutionInfo = new PropertyResolutionInfo(name, origin, rawValue);
                    return new SourcePropertyResolution(propertyResolutionInfo);
                }
            }
        }
        return new NoPropertyResolution();
    }

    private <T> PropertyValue<T> valueFromCache(TypedProperty<T> property) {
        if (!valueCache.containsKey(property.getKey())) {
            valueCache.put(property.getKey(), valueFromResolution(property));
        }

        @SuppressWarnings("unchecked") PropertyValue<T> value = valueCache.get(property.getKey());
        Assert.notNull(value, "Could not source a value, something has gone wrong with properties!");
        return value;
    }

    private <T> PropertyValue<T> valueFromResolution(@NotNull TypedProperty<T> property) {
        Assert.notNull(property, "Cannot resolve a null property.");
        PropertyResolution propertyResolution = resolveFromCache(property.getKey());
        if (propertyResolution.getResolutionInfo().isPresent()) {
            return coerceValue(property, propertyResolution.getResolutionInfo().get());
        } else {
            return new NoValuePropertyValue<T>();
        }
    }

    private <T> PropertyValue<T> coerceValue(@NotNull TypedProperty<T> property, @NotNull PropertyResolutionInfo propertyResolutionInfo) {
        Assert.notNull(property, "Cannot resolve a null property.");
        Assert.notNull(propertyResolutionInfo, "Cannot coerce a null property resolution.");
        try {
            T value = property.getParser().parse(propertyResolutionInfo.getRaw());
            return new ValuedPropertyValue<>(value, propertyResolutionInfo);
        } catch (ValueParseException e) {
            return new ExceptionPropertyValue<T>(e);
        }
    }
    //endregion
}


