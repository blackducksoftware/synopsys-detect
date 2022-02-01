package com.synopsys.integration.configuration.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//data class PropertyHelpInfo(val short: String, val long: String?)
public class PropertyHelpInfo {
    @NotNull
    private final String shortText;
    @Nullable
    private final String longText;

    public PropertyHelpInfo(@NotNull String shortText, @Nullable String longText) {
        this.shortText = shortText;
        this.longText = longText;
    }

    @NotNull
    public String getShortText() {
        return shortText;
    }

    @Nullable
    public String getLongText() {
        return longText;
    }
}
