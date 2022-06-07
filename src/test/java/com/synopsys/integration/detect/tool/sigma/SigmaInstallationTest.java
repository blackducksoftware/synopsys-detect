package com.synopsys.integration.detect.tool.sigma;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.blackduck.http.client.BlackDuckHttpClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.battery.docker.integration.BlackDuckTestConnection;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectInfoUtility;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.exception.IntegrationException;

@Tag("integration")
public class SigmaInstallationTest {
    @Disabled //TODO- remove this when Detect tests with a BD version >= 2022.7.0
    @Test
    public void testInstall() throws IOException, IntegrationException {
        BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckTestConnection.getBlackDuckServicesFactory();
        BlackDuckHttpClient blackDuckHttpClient = blackDuckServicesFactory.getBlackDuckHttpClient();

        DetectInfo detectInfo = Mockito.mock(DetectInfo.class);
        DetectInfoUtility detectInfoUtility = new DetectInfoUtility();
        Mockito.when(detectInfo.getCurrentOs()).thenReturn(detectInfoUtility.findOperatingSystemType());

        DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        File installationDir = Files.createTempDirectory("SigmaInstallationTest").toFile();
        Mockito.when(directoryManager.getPermanentDirectory(SigmaInstaller.SIGMA_INSTALL_DIR_NAME)).thenReturn(installationDir);

        SigmaInstaller sigmaInstaller = new SigmaInstaller(blackDuckHttpClient, detectInfo, blackDuckHttpClient.getBlackDuckUrl(), directoryManager);
        File sigma = sigmaInstaller.installOrUpdateScanner();
        File versionFile = new File(installationDir, SigmaInstaller.SIGMA_INSTALLED_VERSION_FILE_NAME);

        Assertions.assertNotNull(sigma);
        Assertions.assertEquals(SigmaInstaller.SIGMA_INSTALL_FILE_NAME, sigma.getName());
        Assertions.assertNotNull(versionFile);
        Assertions.assertNotEquals(0, FileUtils.readFileToString(versionFile, Charset.defaultCharset()).length());
    }
}
