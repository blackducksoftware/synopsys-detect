package com.synopsys.integration.polaris.common.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.polaris.common.cli.PolarisCliExecutable;
import com.synopsys.integration.polaris.common.cli.PolarisCliRunner;
import com.synopsys.integration.polaris.common.cli.PolarisDownloadUtility;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;

public class DownloadAndRunSetupTest {
    @Test
    public void testDownloadAndRun() throws ExecutableRunnerException {
        String polarisCLIDownloadPath = System.getenv("POLARIS_CLI_DOWNLOAD_PATH");
        Assumptions.assumeTrue(StringUtils.isNotBlank(polarisCLIDownloadPath));
        File downloadTarget = new File(polarisCLIDownloadPath);
        downloadTarget.mkdirs();

        String polarisAnalyzeTargetPath = System.getenv("POLARIS_ANALYZE_TARGET_PATH");
        Assumptions.assumeTrue(StringUtils.isNotBlank(polarisAnalyzeTargetPath));
        File analyzeTarget = new File(polarisAnalyzeTargetPath);
        Assumptions.assumeTrue(analyzeTarget.exists());

        IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO);

        PolarisServerConfigBuilder polarisServerConfigBuilder = PolarisServerConfig.newBuilder();
        polarisServerConfigBuilder.setProperties(System.getenv().entrySet());
        Assumptions.assumeTrue(polarisServerConfigBuilder.isValid());

        PolarisServerConfig polarisServerConfig = polarisServerConfigBuilder.build();
        AccessTokenPolarisHttpClient httpClient = polarisServerConfig.createPolarisHttpClient(logger);

        PolarisDownloadUtility polarisDownloadUtility = PolarisDownloadUtility.fromPolaris(logger, httpClient, downloadTarget);
        Optional<String> polarisCliPath = polarisDownloadUtility.getOrDownloadPolarisCliExecutable();
        assertTrue(polarisCliPath.isPresent());

        PolarisCliRunner polarisCliRunner = new PolarisCliRunner(logger);
        PolarisCliExecutable setupCli = PolarisCliExecutable.createAnalyze(new File(polarisCliPath.get()), new File(polarisAnalyzeTargetPath), Collections.emptyMap());
        ExecutableOutput executableOutput = polarisCliRunner.execute(setupCli);

        System.out.println(executableOutput.getReturnCode());
        System.out.println(executableOutput.getStandardOutput());
        System.out.println(executableOutput.getErrorOutput());
        assertEquals(0, executableOutput.getReturnCode());
    }

}
