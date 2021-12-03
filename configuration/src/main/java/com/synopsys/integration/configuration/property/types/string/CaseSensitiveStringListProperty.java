package com.synopsys.integration.configuration.property.types.string;

import java.util.Collections;

import org.jetbrains.annotations.NotNull;

public class CaseSensitiveStringListProperty extends StringListProperty {
    public CaseSensitiveStringListProperty(@NotNull String key) {
        super(key, Collections.emptyList());
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }
}
