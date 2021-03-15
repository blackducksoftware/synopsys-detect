/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResultCalculator;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResults;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationEventPublisher;
import com.synopsys.integration.detect.workflow.status.Operation;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;

public class CodeLocationResultCalculationOperation {
    private static final String OPERATION_NAME = "Code Location Result Calculation";
    private final CodeLocationResultCalculator codeLocationResultCalculator;
    private final CodeLocationEventPublisher codeLocationEventPublisher;
    private final StatusEventPublisher statusEventPublisher;

    public CodeLocationResultCalculationOperation(CodeLocationResultCalculator codeLocationResultCalculator, CodeLocationEventPublisher codeLocationEventPublisher, StatusEventPublisher statusEventPublisher) {
        this.codeLocationResultCalculator = codeLocationResultCalculator;
        this.codeLocationEventPublisher = codeLocationEventPublisher;
        this.statusEventPublisher = statusEventPublisher;
    }

    public CodeLocationResults execute(CodeLocationAccumulator codeLocationAccumulator) {
        CodeLocationResults codeLocationResults = codeLocationResultCalculator.calculateCodeLocationResults(codeLocationAccumulator);
        codeLocationEventPublisher.publishCodeLocationsCompleted(codeLocationResults.getAllCodeLocationNames());
        statusEventPublisher.publishOperation(new Operation(OPERATION_NAME, StatusType.SUCCESS));
        return codeLocationResults;
    }
}
