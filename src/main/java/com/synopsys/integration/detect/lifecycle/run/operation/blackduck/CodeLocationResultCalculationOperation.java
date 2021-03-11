/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResultCalculator;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResults;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationEventPublisher;
import com.synopsys.integration.exception.IntegrationException;

public class CodeLocationResultCalculationOperation {
    private final CodeLocationResultCalculator codeLocationResultCalculator;
    private final CodeLocationEventPublisher codeLocationEventPublisher;

    public CodeLocationResultCalculationOperation(CodeLocationResultCalculator codeLocationResultCalculator, CodeLocationEventPublisher codeLocationEventPublisher) {
        this.codeLocationResultCalculator = codeLocationResultCalculator;
        this.codeLocationEventPublisher = codeLocationEventPublisher;
    }

    public CodeLocationResults execute(CodeLocationAccumulator codeLocationAccumulator) throws DetectUserFriendlyException, IntegrationException {
        CodeLocationResults codeLocationResults = codeLocationResultCalculator.calculateCodeLocationResults(codeLocationAccumulator);
        codeLocationEventPublisher.publishCodeLocationsCompleted(codeLocationResults.getAllCodeLocationNames());
        return codeLocationResults;
    }
}
