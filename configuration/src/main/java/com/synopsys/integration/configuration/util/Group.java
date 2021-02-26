/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.util;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public interface Group {
    @NotNull
    String getName();

    Optional<Group> getSuperGroup();
}