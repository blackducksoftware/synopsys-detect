package com.synopsys.integration.polaris.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.log.BufferedIntLogger;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.polaris.common.cli.PolarisDownloadUtility;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.util.CleanupZipExpander;
import com.synopsys.integration.util.OperatingSystemType;

public class PolarisDownloadUtilityTest {
    private static final String FAKE_BUT_VALID_DOWNLOAD_URL = "http://www.google.com";

    @Test
    public void testActualDownload() throws IntegrationException {
        String polarisCLIDownloadUrl = System.getenv("POLARIS_SERVER_URL");
        String polarisCLIDownloadPath = System.getenv("POLARIS_CLI_DOWNLOAD_PATH");
        assumeTrue(StringUtils.isNotBlank(polarisCLIDownloadUrl));
        assumeTrue(StringUtils.isNotBlank(polarisCLIDownloadPath));
        File downloadTarget = new File(polarisCLIDownloadPath);

        IntLogger intLogger = new SilentIntLogger();
        PolarisDownloadUtility polarisDownloadUtility = PolarisDownloadUtility.defaultUtilityNoProxy(intLogger, new HttpUrl(polarisCLIDownloadUrl), downloadTarget);

        Optional<String> polarisCliPath = polarisDownloadUtility.getOrDownloadPolarisCliExecutable();
        assertTrue(polarisCliPath.isPresent());
        assertTrue(polarisCliPath.get().length() > 0);
    }

    @Test
    public void testInitialDownload() throws Exception {
        InputStream zipFileStream = getClass().getResourceAsStream("/swip_mac.zip");
        Response mockResponse = Mockito.mock(Response.class);
        Mockito.when(mockResponse.getContent()).thenReturn(zipFileStream);
        Mockito.when(mockResponse.getLastModified()).thenReturn(Long.MAX_VALUE);

        IntHttpClient mockIntHttpClient = Mockito.mock(IntHttpClient.class);
        Mockito.when(mockIntHttpClient.execute(Mockito.any(Request.class))).thenReturn(mockResponse);

        IntLogger intLogger = new SilentIntLogger();
        Path tempDirectory = Files.createTempDirectory(null);
        File downloadTarget = tempDirectory.toFile();
        downloadTarget.deleteOnExit();

        CleanupZipExpander cleanupZipExpander = new CleanupZipExpander(intLogger);
        PolarisDownloadUtility polarisDownloadUtility = new PolarisDownloadUtility(intLogger, OperatingSystemType.LINUX, mockIntHttpClient, cleanupZipExpander, new HttpUrl(PolarisDownloadUtilityTest.FAKE_BUT_VALID_DOWNLOAD_URL), downloadTarget);
        Optional<String> polarisCliPath = polarisDownloadUtility.getOrDownloadPolarisCliExecutable();

        assertTrue(polarisCliPath.isPresent());
        assertTrue(polarisCliPath.get().length() > 0);
    }

    @Test
    public void testNotDownloadIfNotUpdatedOnServer() throws Exception {
        Response mockResponse = Mockito.mock(Response.class);
        Mockito.when(mockResponse.getLastModified()).thenReturn(0L);

        IntHttpClient mockIntHttpClient = Mockito.mock(IntHttpClient.class);
        Mockito.when(mockIntHttpClient.execute(Mockito.any(Request.class))).thenReturn(mockResponse);

        BufferedIntLogger intLogger = new BufferedIntLogger();

        Path tempDirectory = Files.createTempDirectory(null);
        File downloadTarget = tempDirectory.toFile();
        downloadTarget.deleteOnExit();

        CleanupZipExpander cleanupZipExpander = new CleanupZipExpander(intLogger);
        PolarisDownloadUtility polarisDownloadUtility = new PolarisDownloadUtility(intLogger, OperatingSystemType.LINUX, mockIntHttpClient, cleanupZipExpander, new HttpUrl(PolarisDownloadUtilityTest.FAKE_BUT_VALID_DOWNLOAD_URL), downloadTarget);
        Optional<String> polarisCliPath = polarisDownloadUtility.getOrDownloadPolarisCliExecutable();

        assertFalse(polarisCliPath.isPresent());
        assertTrue(intLogger.getOutputString(LogLevel.DEBUG).contains("skipping download"));
    }

    @Test
    public void testDownloadIfServerUpdated() throws Exception {
        InputStream zipFileStream = getClass().getResourceAsStream("/swip_mac.zip");
        Response mockResponse = Mockito.mock(Response.class);
        Mockito.when(mockResponse.getContent()).thenReturn(zipFileStream);
        Mockito.when(mockResponse.getLastModified()).thenReturn(Long.MAX_VALUE);

        IntHttpClient mockIntHttpClient = Mockito.mock(IntHttpClient.class);
        Mockito.when(mockIntHttpClient.execute(Mockito.any(Request.class))).thenReturn(mockResponse);

        BufferedIntLogger intLogger = new BufferedIntLogger();

        Path tempDirectory = Files.createTempDirectory(null);
        File downloadTarget = tempDirectory.toFile();
        downloadTarget.deleteOnExit();

        File installDirectory = new File(downloadTarget, PolarisDownloadUtility.POLARIS_CLI_INSTALL_DIRECTORY);
        installDirectory.mkdirs();
        installDirectory.deleteOnExit();

        // create a directory that should be deleted by the update download/extract code
        File directoryOfPreviousExtraction = new File(installDirectory, "temp_polaris_cli_version");
        directoryOfPreviousExtraction.mkdirs();
        assertTrue(directoryOfPreviousExtraction.isDirectory());
        assertTrue(directoryOfPreviousExtraction.exists());

        CleanupZipExpander cleanupZipExpander = new CleanupZipExpander(intLogger);
        PolarisDownloadUtility polarisDownloadUtility = new PolarisDownloadUtility(intLogger, OperatingSystemType.LINUX, mockIntHttpClient, cleanupZipExpander, new HttpUrl(PolarisDownloadUtilityTest.FAKE_BUT_VALID_DOWNLOAD_URL), downloadTarget);
        Optional<String> polarisCliPath = polarisDownloadUtility.getOrDownloadPolarisCliExecutable();

        assertTrue(polarisCliPath.isPresent());
        assertTrue(polarisCliPath.get().length() > 0);
        assertFalse(directoryOfPreviousExtraction.exists());
        assertTrue(intLogger.getOutputString(LogLevel.WARN).contains("There were items"));
        assertTrue(intLogger.getOutputString(LogLevel.WARN).contains("that are being deleted"));
    }

}
