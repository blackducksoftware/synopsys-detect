package com.synopsys.integration.polaris.common.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.synopsys.integration.rest.HttpUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import com.google.gson.Gson;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.polaris.common.cli.PolarisCliExecutable;
import com.synopsys.integration.polaris.common.cli.PolarisCliResponseUtility;
import com.synopsys.integration.polaris.common.cli.PolarisCliRunner;
import com.synopsys.integration.polaris.common.cli.PolarisDownloadUtility;
import com.synopsys.integration.polaris.common.cli.model.CliCommonResponseModel;
import com.synopsys.integration.polaris.common.cli.model.CommonToolInfo;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder;
import com.synopsys.integration.polaris.common.exception.PolarisIntegrationException;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;

public class JobServiceTestIT {
    private CliCommonResponseModel cliCommonResponseModel;
    private JobService jobService;
    private IntLogger logger;

    @BeforeEach
    public void createJobAndJobService() throws ExecutableRunnerException, PolarisIntegrationException {
        final PolarisServerConfigBuilder polarisServerConfigBuilder = PolarisServerConfig.newBuilder();
        polarisServerConfigBuilder.setUrl(System.getenv("POLARIS_URL"));
        polarisServerConfigBuilder.setAccessToken(System.getenv("POLARIS_ACCESS_TOKEN"));
        polarisServerConfigBuilder.setGson(new Gson());

        assumeTrue(StringUtils.isNotBlank(polarisServerConfigBuilder.getUrl()));
        assumeTrue(StringUtils.isNotBlank(polarisServerConfigBuilder.getAccessToken()));

        final PolarisServerConfig polarisServerConfig = polarisServerConfigBuilder.build();
        logger = new PrintStreamIntLogger(System.out, LogLevel.INFO);

        final AccessTokenPolarisHttpClient accessTokenPolarisHttpClient = polarisServerConfig.createPolarisHttpClient(logger);
        final File emptyInstallLocation = new File("/tmp/polaris_installation");
        emptyInstallLocation.deleteOnExit();
        final PolarisDownloadUtility polarisDownloadUtility = PolarisDownloadUtility.fromPolaris(logger, polarisServerConfig.createPolarisHttpClient(logger), emptyInstallLocation);
        final Optional<String> potentialPolarisCLiExecutablePath = polarisDownloadUtility.getOrDownloadPolarisCliExecutable();

        assumeTrue(potentialPolarisCLiExecutablePath.isPresent(), "The Polaris CLI could not be downloaded");

        final String polarisCliExecutablePath = potentialPolarisCLiExecutablePath.get();

        final PolarisCliRunner polarisCliRunner = new PolarisCliRunner(logger);
        final File emptyProjectDirectory = new File("/tmp/test_directory");
        emptyProjectDirectory.deleteOnExit();
        assumeTrue(emptyProjectDirectory.exists() || emptyProjectDirectory.mkdirs(), "Test directory does not exist and could not be created");

        final Map<String, String> environmentMap = new HashMap<>(System.getenv());
        polarisServerConfig.populateEnvironmentVariables(environmentMap::put);
        final PolarisCliExecutable polarisCliExecutable = new PolarisCliExecutable(new File(polarisCliExecutablePath), emptyProjectDirectory, environmentMap, Arrays.asList("analyze"));

        final ExecutableOutput executableOutput = polarisCliRunner.execute(polarisCliExecutable);
        assumeTrue(executableOutput.getReturnCode() == 0, "'polaris analyze' returned a nonzero exit code");

        final PolarisCliResponseUtility polarisCliResponseUtility = PolarisCliResponseUtility.defaultUtility(logger);
        cliCommonResponseModel = polarisCliResponseUtility.getPolarisCliResponseModelFromDefaultLocation(emptyProjectDirectory.getAbsolutePath());

        final PolarisServicesFactory polarisServicesFactory = polarisServerConfig.createPolarisServicesFactory(logger);
        jobService = polarisServicesFactory.createJobService();
    }

    @Test
    public void testWaitForJobToCompleteByUrl() {
        final List<CommonToolInfo> tools = cliCommonResponseModel.getTools();
        assertFalse(tools.isEmpty(), "No tools found in the cli-scan.json response model");

        final String jobStatusUrl = cliCommonResponseModel.getTools().get(0).getJobStatusUrl();

        logger.info("Waiting for job at URL: " + jobStatusUrl);

        try {
            jobService.waitForJobStateIsCompletedOrDieByUrl(new HttpUrl(jobStatusUrl), 30L, JobService.DEFAULT_WAIT_INTERVAL);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (IntegrationException ex) {
            if (ex.getMessage() == null || !ex.getMessage().contains("did not end in the provided timeout")) {
                fail("An unexpected exception occurred -- Job did not succeed and the wait did not time out, something may have gone wrong.", ex);
            }
        }
    }

    @Test
    public void testWaitForJobToCompleteById() {
        final List<CommonToolInfo> tools = cliCommonResponseModel.getTools();
        assertFalse(tools.isEmpty(), "No tools found in the cli-scan.json response model");

        final String jobId = cliCommonResponseModel.getTools().get(0).getJobId();

        logger.info("Waiting for job at URL: " + jobId);

        try {
            jobService.waitForJobStateIsCompletedOrDieById(jobId, 30L, JobService.DEFAULT_WAIT_INTERVAL);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (IntegrationException ex) {
            if (ex.getMessage() == null || !ex.getMessage().contains("did not end in the provided timeout")) {
                fail("An unexpected exception occurred -- Job did not succeed and the wait did not time out, something may have gone wrong.", ex);
            }
        }
    }

}
