/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.tool.swip;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigurationUtility;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.rest.connection.RestConnection;
import com.synopsys.integration.swip.common.SwipDownloadUtility;
import com.synopsys.integration.util.CleanupZipExpander;

public class SwipCliManager {
    private final DirectoryManager directoryManager;
    private final ExecutableRunner executableRunner;
    private final DetectConfigurationUtility detectConfigurationUtility;

    public SwipCliManager(final DirectoryManager directoryManager, final ExecutableRunner executableRunner, DetectConfigurationUtility detectConfigurationUtility) {
        this.directoryManager = directoryManager;
        this.executableRunner = executableRunner;
        this.detectConfigurationUtility = detectConfigurationUtility;
    }

    public void runSwip(final IntLogger logger, File swipProjectDirectory) throws DetectUserFriendlyException {
        RestConnection restConnection = detectConfigurationUtility.createUnauthenticatedRestConnection(SwipDownloadUtility.DEFAULT_SWIP_SERVER_URL);
        CleanupZipExpander cleanupZipExpander = new CleanupZipExpander(logger);
        File toolsDirectory = directoryManager.getPermanentDirectory();

        SwipDownloadUtility swipDownloadUtility = new SwipDownloadUtility(logger, restConnection, cleanupZipExpander, SwipDownloadUtility.DEFAULT_SWIP_SERVER_URL, toolsDirectory);
        Optional<String> swipCliPath = swipDownloadUtility.retrieveSwipCliExecutablePath();

        if (swipCliPath.isPresent()) {
            Map<String, String> environmentVariables = new HashMap<>();
            environmentVariables.put("COVERITY_UNSUPPORTED", "1");
            environmentVariables.put("SWIP_USER_INPUT_TIMEOUT_MINUTES", "1");

            logger.info("Found swip cli: " + swipCliPath.get());
            List<String> arguments = new ArrayList<>();
            arguments.add("analyze");
            arguments.add("-w");

            Executable swipExecutable = new Executable(swipProjectDirectory, environmentVariables, swipCliPath.get(), arguments);
            try {
                executableRunner.execute(swipExecutable);
            } catch (ExecutableRunnerException e) {
                logger.error("Couldn't run the executable: " + e.getMessage());
            }
        } else {
            logger.error("Check the logs - the Swip CLI could not be found.");
        }
    }

}