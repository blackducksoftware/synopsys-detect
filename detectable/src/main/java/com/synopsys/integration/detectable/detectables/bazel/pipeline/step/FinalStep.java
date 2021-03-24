/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.List;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.exception.IntegrationException;

public interface FinalStep {
    List<Dependency> finish(List<String> input) throws IntegrationException;
}
