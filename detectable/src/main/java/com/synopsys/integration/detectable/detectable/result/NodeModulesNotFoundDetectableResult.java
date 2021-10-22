/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

public class NodeModulesNotFoundDetectableResult extends FailedDetectableResult {
    private final String directoryPath;
    private final String installCommand;

    public NodeModulesNotFoundDetectableResult(String directoryPath, String installCommand) {
        this.directoryPath = directoryPath;
        this.installCommand = installCommand;
    }

    @Override
    public String toDescription() {
        return String.format("A package.json was located in %s, but the node_modules folder was NOT located. Please run '%s' in that location and try again.", directoryPath, installCommand);
    }
}
