/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.polaris;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.polaris.common.cli.PolarisDownloadUtility;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;

public class PolarisTool {
    private static final String POLARIS_DESCRIPTION_KEY = "POLARIS";

    private final DirectoryManager directoryManager;
    private final DetectableExecutableRunner executableRunner;
    private final StatusEventPublisher statusEventPublisher;
    private final PropertyConfiguration detectConfiguration;
    private final PolarisServerConfig polarisServerConfig;
    private final OperationSystem operationSystem;

    public PolarisTool(StatusEventPublisher statusEventPublisher, DirectoryManager directoryManager, DetectableExecutableRunner executableRunner, PropertyConfiguration detectConfiguration,
        PolarisServerConfig polarisServerConfig, OperationSystem operationSystem) {
        this.directoryManager = directoryManager;
        this.executableRunner = executableRunner;
        this.statusEventPublisher = statusEventPublisher;
        this.detectConfiguration = detectConfiguration;
        this.polarisServerConfig = polarisServerConfig;
        this.operationSystem = operationSystem;
    }

    public void runPolaris(IntLogger logger, File projectDirectory) {
        operationSystem.beginOperation(POLARIS_DESCRIPTION_KEY);
        logger.info("Polaris determined it should attempt to run.");
        String polarisUrl = detectConfiguration.getValueOrEmpty(DetectProperties.POLARIS_URL.getProperty()).orElse(null);
        logger.info("Will use the following polaris url: " + polarisUrl);

        AccessTokenPolarisHttpClient polarisHttpClient = polarisServerConfig.createPolarisHttpClient(logger);
        File toolsDirectory = directoryManager.getPermanentDirectory();

        PolarisDownloadUtility polarisDownloadUtility = PolarisDownloadUtility.fromPolaris(logger, polarisHttpClient, toolsDirectory);
        Optional<String> polarisCliPath = polarisDownloadUtility.getOrDownloadPolarisCliExecutable();

        //TODO this should be revised to use PolarisCliExecutable and PolarisCliRunner
        if (polarisCliPath.isPresent()) {
            Map<String, String> environmentVariables = new HashMap<>();
            environmentVariables.put("COVERITY_UNSUPPORTED", "1");
            environmentVariables.put("POLARIS_USER_INPUT_TIMEOUT_MINUTES", "1");
            polarisServerConfig.populateEnvironmentVariables(environmentVariables::put);

            logger.info("Found polaris cli: " + polarisCliPath.get());

            String additionalArgs = detectConfiguration.getValueOrEmpty(DetectProperties.POLARIS_ARGUMENTS.getProperty()).orElse(null);
            String commandOverride = detectConfiguration.getValueOrEmpty(DetectProperties.POLARIS_COMMAND.getProperty()).orElse(null);
            List<String> arguments = new ArrayList<>();
            if (StringUtils.isNotBlank(commandOverride)) {
                if (StringUtils.isNotBlank(additionalArgs)) {
                    logger.error("The provided polaris command will be used and the additional polaris arguments will be discarded. You should only set command or arguments, not both.");
                }
                arguments.addAll(Arrays.asList(commandOverride.split(" ")));
            } else if (StringUtils.isNotBlank(additionalArgs)) {
                arguments.add("analyze");
                arguments.addAll(Arrays.asList(additionalArgs.split(" ")));
            } else {
                arguments.add("analyze");
            }

            Executable swipExecutable = Executable.create(projectDirectory, environmentVariables, polarisCliPath.get(), arguments);
            try {
                ExecutableOutput output = executableRunner.execute(swipExecutable);
                if (output.getReturnCode() == 0) {
                    statusEventPublisher.publishStatusSummary(new Status(POLARIS_DESCRIPTION_KEY, StatusType.SUCCESS));
                    operationSystem.completeWithSuccess(POLARIS_DESCRIPTION_KEY);
                } else {
                    logger.error("Polaris returned a non-zero exit code.");
                    statusEventPublisher.publishStatusSummary(new Status(POLARIS_DESCRIPTION_KEY, StatusType.FAILURE));
                    operationSystem.completeWithFailure(POLARIS_DESCRIPTION_KEY);
                }

            } catch (ExecutableRunnerException e) {
                statusEventPublisher.publishStatusSummary(new Status(POLARIS_DESCRIPTION_KEY, StatusType.FAILURE));
                operationSystem.completeWithFailure(POLARIS_DESCRIPTION_KEY);
                logger.error("Couldn't run the executable: " + e.getMessage());
            }
        } else {
            logger.error("Check the logs - the Polaris CLI could not be found.");
            statusEventPublisher.publishStatusSummary(new Status(POLARIS_DESCRIPTION_KEY, StatusType.FAILURE));
            operationSystem.completeWithFailure(POLARIS_DESCRIPTION_KEY);
        }
    }

}
