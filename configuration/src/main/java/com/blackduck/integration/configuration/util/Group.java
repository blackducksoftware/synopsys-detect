package com.blackduck.integration.configuration.util;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public interface Group {
    @NotNull
    String getName();

    Optional<Group> getSuperGroup();
}