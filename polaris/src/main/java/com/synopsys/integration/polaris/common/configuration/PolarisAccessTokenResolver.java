/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.builder.BuilderStatus;
import com.synopsys.integration.log.IntLogger;

import static com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder.*;

public class PolarisAccessTokenResolver {
    private final IntLogger logger;
    private final BuilderStatus builderStatus;
    private final String accessToken;
    private final String polarisHome;
    private final String accessTokenFilePath;
    private final String userHomePath;

    public PolarisAccessTokenResolver(IntLogger logger, BuilderStatus builderStatus, String accessToken, String polarisHome, String accessTokenFilePath, String userHomePath) {
        this.logger = logger;
        this.builderStatus = builderStatus;
        this.accessToken = accessToken;
        this.polarisHome = polarisHome;
        this.accessTokenFilePath = accessTokenFilePath;
        this.userHomePath = userHomePath;
    }

    public Optional<String> resolveAccessToken() {
        if (StringUtils.isNotBlank(accessToken)) {
            return Optional.of(accessToken);
        }

        logger.warn("The access token was not set explicitly, so it must be configured on the filesystem.");
        if (StringUtils.isNotBlank(accessTokenFilePath)) {
            File accessTokenFile = new File(accessTokenFilePath);
            if (validAccessTokenFile(accessTokenFile)) {
                return extractAccessToken(accessTokenFile);
            }
        }

        logger.warn("The access token file was not set explicitly, so it must be configured in a Polaris home directory.");
        File polarisHomeDirectory = null;
        if (StringUtils.isNotBlank(polarisHome)) {
            polarisHomeDirectory = new File(polarisHome);
        } else if (StringUtils.isNotBlank(userHomePath)) {
            File userHomeDirectory = new File(userHomePath);
            polarisHomeDirectory = new File(userHomeDirectory, POLARIS_CONFIG_DIRECTORY_DEFAULT);
            if (!polarisHomeDirectory.exists()) {
                polarisHomeDirectory = new File(userHomeDirectory, SWIP_CONFIG_DIRECTORY_DEFAULT);
            }
        }

        if (null == polarisHomeDirectory || !polarisHomeDirectory.exists() || !polarisHomeDirectory.isDirectory()) {
            String errorMessage = "A valid Polaris home directory could not be found.";
            logger.error(errorMessage);
            builderStatus.addErrorMessage(errorMessage);
            return Optional.empty();
        }

        File accessTokenFile = new File(polarisHomeDirectory, POLARIS_ACCESS_TOKEN_FILENAME_DEFAULT);
        if (validAccessTokenFile(accessTokenFile)) {
            return extractAccessToken(accessTokenFile);
        }

        logger.error("No access token could be found.");
        return Optional.empty();
    }

    private boolean validAccessTokenFile(File accessTokenFile) {
        return accessTokenFile.exists() && accessTokenFile.isFile() && accessTokenFile.length() > 0 && accessTokenFile.length() < 1000;
    }

    private Optional<String> extractAccessToken(File accessTokenFile) {
        try {
            String accessToken = StringUtils.trimToEmpty(FileUtils.readFileToString(accessTokenFile, StandardCharsets.UTF_8));

            logger.info(String.format("Using access token from %s file.", accessTokenFile.getAbsolutePath()));
            return Optional.of(accessToken);
        } catch (IOException e) {
            String errorMessage = String.format("Could not read the access token file %s: %s", accessTokenFile.getAbsolutePath(), e.getMessage());
            logger.error(errorMessage);
            builderStatus.addErrorMessage(errorMessage);
        }

        return Optional.empty();
    }

}
