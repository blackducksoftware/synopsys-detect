/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.polaris.common.cli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.compress.archivers.ArchiveException;

import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.util.CleanupZipExpander;
import com.synopsys.integration.util.OperatingSystemType;

public class PolarisDownloadUtility {
    public static final Integer DEFAULT_POLARIS_TIMEOUT = 120;

    public static final String LINUX_DOWNLOAD_URL_FORMAT = "/api/tools/%s_cli-linux64.zip";
    public static final String WINDOWS_DOWNLOAD_URL_FORMAT = "/api/tools/%s_cli-win64.zip";
    public static final String MAC_DOWNLOAD_URL_FORMAT = "/api/tools/%s_cli-macosx.zip";

    public static final String POLARIS_CLI_INSTALL_DIRECTORY = "Polaris_CLI_Installation";
    public static final String VERSION_FILENAME = "polarisVersion.txt";

    private final IntLogger logger;
    private final OperatingSystemType operatingSystemType;
    private final IntHttpClient intHttpClient;
    private final CleanupZipExpander cleanupZipExpander;
    private final HttpUrl polarisServerUrl;
    private final File installDirectory;

    public PolarisDownloadUtility(IntLogger logger, OperatingSystemType operatingSystemType, IntHttpClient intHttpClient, CleanupZipExpander cleanupZipExpander, HttpUrl polarisServerUrl, File downloadTargetDirectory) {
        if (null == polarisServerUrl) {
            throw new IllegalArgumentException("A Polaris server url must be provided.");
        }

        this.logger = logger;
        this.operatingSystemType = operatingSystemType;
        this.intHttpClient = intHttpClient;
        this.cleanupZipExpander = cleanupZipExpander;
        this.polarisServerUrl = polarisServerUrl;
        installDirectory = new File(downloadTargetDirectory, PolarisDownloadUtility.POLARIS_CLI_INSTALL_DIRECTORY);

        installDirectory.mkdirs();
        if (!installDirectory.exists() || !installDirectory.isDirectory() || !installDirectory.canWrite()) {
            throw new IllegalArgumentException("The provided directory must exist and be writable.");
        }
    }

    public static PolarisDownloadUtility defaultUtility(IntLogger logger, HttpUrl polarisServerUrl, ProxyInfo proxyInfo, File downloadTargetDirectory) {
        OperatingSystemType operatingSystemType = OperatingSystemType.determineFromSystem();
        IntHttpClient intHttpClient = new IntHttpClient(logger, PolarisDownloadUtility.DEFAULT_POLARIS_TIMEOUT, false, proxyInfo);
        CleanupZipExpander cleanupZipExpander = new CleanupZipExpander(logger);
        return new PolarisDownloadUtility(logger, operatingSystemType, intHttpClient, cleanupZipExpander, polarisServerUrl, downloadTargetDirectory);
    }

    public static PolarisDownloadUtility defaultUtilityNoProxy(IntLogger logger, HttpUrl polarisServerUrl, File downloadTargetDirectory) {
        OperatingSystemType operatingSystemType = OperatingSystemType.determineFromSystem();
        IntHttpClient intHttpClient = new IntHttpClient(logger, PolarisDownloadUtility.DEFAULT_POLARIS_TIMEOUT, false, ProxyInfo.NO_PROXY_INFO);
        CleanupZipExpander cleanupZipExpander = new CleanupZipExpander(logger);
        return new PolarisDownloadUtility(logger, operatingSystemType, intHttpClient, cleanupZipExpander, polarisServerUrl, downloadTargetDirectory);
    }

    public static PolarisDownloadUtility fromPolaris(IntLogger logger, AccessTokenPolarisHttpClient polarisHttpClient, File downloadTargetDirectory) {
        OperatingSystemType operatingSystemType = OperatingSystemType.determineFromSystem();
        IntHttpClient intHttpClient = new IntHttpClient(logger, polarisHttpClient.getTimeoutInSeconds(), polarisHttpClient.isAlwaysTrustServerCertificate(), polarisHttpClient.getProxyInfo());
        CleanupZipExpander cleanupZipExpander = new CleanupZipExpander(logger);
        return new PolarisDownloadUtility(logger, operatingSystemType, intHttpClient, cleanupZipExpander, polarisHttpClient.getPolarisServerUrl(), downloadTargetDirectory);
    }

    /**
     * The Polaris CLI will be download if it has not previously been downloaded or
     * if it has been updated on the server. The absolute path to the swip_cli
     * executable will be returned if it was downloaded or found successfully,
     * otherwise an Optional.empty will be returned and the log will contain
     * details concerning the failure.
     */
    public Optional<String> getOrDownloadPolarisCliExecutable() {
        File binDirectory = getOrDownloadPolarisCliBin().orElse(null);

        if (binDirectory != null && binDirectory.exists() && binDirectory.isDirectory()) {
            try {
                File polarisCliExecutable = getPolarisCli(binDirectory);
                logger.info("Polaris CLI downloaded/found successfully: " + polarisCliExecutable.getCanonicalPath());
                return Optional.of(polarisCliExecutable.getCanonicalPath());
            } catch (Exception e) {
                logger.error("The Polaris CLI executable could not be found: " + e.getMessage());
            }
        }

        return Optional.empty();
    }

    public Optional<File> getOrDownloadPolarisCliBin() {
        File versionFile = null;
        try {
            versionFile = getOrCreateVersionFile();
        } catch (IOException e) {
            logger.error("Could not create the version file: " + e.getMessage());
            return Optional.empty();
        }

        String downloadUrlFormat = getDownloadUrlFormat();
        return getOrDownloadPolarisCliBin(versionFile, downloadUrlFormat);
    }

    public Optional<File> getOrDownloadPolarisCliBin(File versionFile, String downloadUrlFormat) {
        File binDirectory = null;
        try {
            binDirectory = downloadIfModified(versionFile, downloadUrlFormat);
        } catch (Exception e) {
            logger.error("The Polaris CLI could not be downloaded successfully: " + e.getMessage());
        }

        return Optional.ofNullable(binDirectory);
    }

    public Optional<String> getOrDownloadPolarisCliHome() {
        return getOrDownloadPolarisCliBin()
                   .map(File::getParentFile)
                   .flatMap(file -> {
                       String pathToPolarisCliHome = null;

                       try {
                           pathToPolarisCliHome = file.getCanonicalPath();
                       } catch (IOException e) {
                           logger.error("The Polaris CLI home could not be found: " + e.getMessage());
                       }

                       return Optional.ofNullable(pathToPolarisCliHome);
                   });
    }

    public Optional<String> findPolarisCliInBin(File binDirectory) {
        String polarisCli = null;

        try {
            polarisCli = getPolarisCli(binDirectory).getCanonicalPath();
        } catch (final Exception e) {
            logger.error("The Polaris CLI could not be found in directory " + binDirectory.toString() + ". Please ensure the directory exists and contains the Polaris CLI.");
        }

        return Optional.ofNullable(polarisCli);
    }

    public File getOrCreateVersionFile() throws IOException {
        File versionFile = new File(installDirectory, PolarisDownloadUtility.VERSION_FILENAME);
        if (!versionFile.exists()) {
            logger.info("The version file has not been created yet so creating it now.");
            versionFile.createNewFile();
            versionFile.setLastModified(0L);
        }

        return versionFile;
    }

    public String getDownloadUrlFormat() {
        if (OperatingSystemType.MAC == operatingSystemType) {
            return polarisServerUrl + PolarisDownloadUtility.MAC_DOWNLOAD_URL_FORMAT;
        } else if (OperatingSystemType.WINDOWS == operatingSystemType) {
            return polarisServerUrl + PolarisDownloadUtility.WINDOWS_DOWNLOAD_URL_FORMAT;
        } else {
            return polarisServerUrl + PolarisDownloadUtility.LINUX_DOWNLOAD_URL_FORMAT;
        }
    }

    private File downloadIfModified(File versionFile, String downloadUrlFormat) throws IOException, IntegrationException, ArchiveException {
        long lastTimeDownloaded = versionFile.lastModified();
        logger.debug(String.format("last time downloaded: %d", lastTimeDownloaded));

        HttpUrl swipDownloadUrl = new HttpUrl(String.format(downloadUrlFormat, "swip"));
        Request swipDownloadRequest = new Request.Builder(swipDownloadUrl).build();
        try (Response downloadResponse = intHttpClient.execute(swipDownloadRequest)) {
            if (!downloadResponse.isStatusCodeError()) {
                return getBinDirectoryFromResponse(downloadResponse, versionFile, lastTimeDownloaded);
            }
        }

        HttpUrl polarisDownloadUrl = new HttpUrl(String.format(downloadUrlFormat, "polaris"));
        Request polarisDownloadRequest = new Request.Builder(polarisDownloadUrl).build();
        try (Response downloadResponse = intHttpClient.execute(polarisDownloadRequest)) {
            if (!downloadResponse.isStatusCodeError()) {
                return getBinDirectoryFromResponse(downloadResponse, versionFile, lastTimeDownloaded);
            }
        }

        return getBinDirectory();
    }

    private File getBinDirectoryFromResponse(Response response, File versionFile, long lastTimeDownloaded) throws IOException, IntegrationException, ArchiveException {
        long lastModifiedOnServer = response.getLastModified();
        if (lastModifiedOnServer == lastTimeDownloaded) {
            logger.debug("The Polaris CLI has not been modified since it was last downloaded - skipping download.");
            return getBinDirectory();
        } else {
            logger.info("Downloading the Polaris CLI.");
            try (InputStream responseStream = response.getContent()) {
                cleanupZipExpander.expand(responseStream, installDirectory);
            }
            versionFile.setLastModified(lastModifiedOnServer);

            File binDirectory = getBinDirectory();
            makeBinFilesExecutable(binDirectory);

            logger.info("Polaris CLI downloaded successfully.");

            return binDirectory;
        }
    }

    // since we know that we only allow a single directory in installDirectory,
    // that single directory IS the expanded archive
    private File getBinDirectory() throws IntegrationException {
        File[] directories = installDirectory.listFiles(File::isDirectory);
        if (directories == null || directories.length == 0) {
            throw new IntegrationException(String.format("The %s directory is empty, so the Polaris CLI can not be run.", PolarisDownloadUtility.POLARIS_CLI_INSTALL_DIRECTORY));
        }

        if (directories.length > 1) {
            throw new IntegrationException(String.format("The %s directory should only be modified by polaris-common. Please delete all files from that directory and try again.", PolarisDownloadUtility.POLARIS_CLI_INSTALL_DIRECTORY));
        }

        File polarisCliDirectory = directories[0];
        File bin = new File(polarisCliDirectory, "bin");

        return bin;
    }

    private void makeBinFilesExecutable(File binDirectory) {
        Arrays.stream(binDirectory.listFiles()).forEach(file -> file.setExecutable(true));
    }

    private File getPolarisCli(File binDirectory) throws IntegrationException {
        Optional<File> polarisCli = checkFile(binDirectory, "polaris");
        Optional<File> swipCli = checkFile(binDirectory, "swip_cli");

        if (polarisCli.isPresent()) {
            return polarisCli.get();
        } else if (swipCli.isPresent()) {
            return swipCli.get();
        }

        throw new IntegrationException("The Polaris CLI does not appear to have been downloaded correctly - be sure to download it first.");
    }

    private Optional<File> checkFile(File binDirectory, String filePrefix) {
        String filename = filePrefix;
        if (OperatingSystemType.WINDOWS == operatingSystemType) {
            filename += ".exe";
        }
        File file = new File(binDirectory, filename);

        if (null != file && file.exists() && file.isFile() && file.length() > 0L) {
            return Optional.of(file);
        } else {
            return Optional.empty();
        }
    }

}
