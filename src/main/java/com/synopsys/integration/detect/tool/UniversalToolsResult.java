/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool;

import com.synopsys.integration.util.NameVersion;

public class UniversalToolsResult {
    public NameVersion getNameVersion() {
        return nameVersion;
    }

    private enum UniversalToolsResultType {
        FAILED,
        SUCCESS
    }

    private final UniversalToolsResultType resultType;
    private final NameVersion nameVersion;

    public UniversalToolsResult(final UniversalToolsResultType resultType, final NameVersion nameVersion) {
        this.resultType = resultType;
        this.nameVersion = nameVersion;
    }

    public static UniversalToolsResult failure(final NameVersion nameVersion) {
        return new UniversalToolsResult(UniversalToolsResultType.FAILED, nameVersion);
    }

    public static UniversalToolsResult success(final NameVersion nameVersion) {
        return new UniversalToolsResult(UniversalToolsResultType.SUCCESS, nameVersion);
    }

    public boolean anyFailed() {
        return resultType == UniversalToolsResultType.FAILED;
    }
}
