package com.blackducksoftware.integration.hub.detect.tool.polaris;

import java.io.File;

public class PolarisEnvironmentCheck {
    public static final String POLARIS_CONFIG_DIRECTORY = ".swip";
    public static final String POLARIS_ACCESS_TOKEN_FILENAME = ".access_token";

    public boolean canRun(final File homeDirectory) {
        if (null != homeDirectory && homeDirectory.exists() && homeDirectory.isDirectory()) {
            final File polarisConfig = new File(homeDirectory, POLARIS_CONFIG_DIRECTORY);
            if (null != polarisConfig && polarisConfig.exists() && polarisConfig.isDirectory()) {
                final File accessToken = new File(polarisConfig, POLARIS_ACCESS_TOKEN_FILENAME);
                if (null != accessToken && accessToken.exists() && accessToken.isFile() && accessToken.length() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

}