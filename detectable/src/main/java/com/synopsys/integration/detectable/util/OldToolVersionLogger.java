/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;

public class OldToolVersionLogger {
    private static final Logger logger = LoggerFactory.getLogger(OldToolVersionLogger.class);

    public static void log(DetectableExecutableRunner executableRunner, File directory, ExecutableTarget conanExe) {
        if (!logger.isDebugEnabled()) {
            return;
        }
        List<String> versionArgument = Arrays.asList("--version");
        try {
            executableRunner.execute(ExecutableUtils.createFromTarget(directory, conanExe, versionArgument));
            // At DEBUG, commands executed and their output are logged, so it would be redundant to log 'em again here
        } catch (Exception e) {
            logger.warn("Unable to determine {} version: {}", conanExe.toCommand(), e.getMessage());
        }
    }
}
