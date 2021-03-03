/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.status;

import java.io.File;
import java.util.List;

public class UnrecognizedPaths {
    private final String group;
    private final List<File> messages;

    public UnrecognizedPaths(final String group, final List<File> messages) {
        this.group = group;
        this.messages = messages;
    }

    public String getGroup() {
        return group;
    }

    public List<File> getPaths() {
        return messages;
    }
}
