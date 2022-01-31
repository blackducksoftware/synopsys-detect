package com.synopsys.integration.configuration.property.types.path;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ListValueParser;
import com.synopsys.integration.configuration.property.base.ValuedAlikeListProperty;
import com.synopsys.integration.configuration.property.base.ValuedListProperty;

public class PathListProperty extends ValuedAlikeListProperty<PathValue> {
    public PathListProperty(@NotNull String key,
        List<PathValue> defaultValue) {
        super(key, new ListValueParser<>(new PathValueParser()), defaultValue);
    }

    @Override
    public String describeType() {
        return "Path List";
    }
}
