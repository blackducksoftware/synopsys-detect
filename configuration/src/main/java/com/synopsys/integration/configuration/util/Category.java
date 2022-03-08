package com.synopsys.integration.configuration.util;

import org.jetbrains.annotations.NotNull;

public abstract class Category {
    private final String name;

    protected Category(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return name;
    }
}