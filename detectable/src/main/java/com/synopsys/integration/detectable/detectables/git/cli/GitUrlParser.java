package com.synopsys.integration.detectable.detectables.git.cli;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.exception.IntegrationException;

public class GitUrlParser {

    public String getRepoName(final String remoteUrlString) throws IntegrationException, MalformedURLException {
        final String remoteUrlPath;
        if (remoteUrlString.startsWith("ssh://")) {
            // Parses urls such as: ssh://user@synopsys.com:12345/blackducksoftware/synopsys-detect
            final int lastIndexOfSlash = remoteUrlString.lastIndexOf("/");
            final String projectName = remoteUrlString.substring(lastIndexOfSlash);
            final String remainder = remoteUrlString.substring(0, lastIndexOfSlash);
            final int remainderLastIndexOfSlash = remainder.lastIndexOf("/");
            final String organization = remainder.substring(remainderLastIndexOfSlash);
            remoteUrlPath = organization + projectName;
        } else if (remoteUrlString.contains("@")) {
            // Parses urls such as: git@github.com:blackducksoftware/synopsys-detect.git
            final String[] tokens = remoteUrlString.split(":");
            if (tokens.length != 2) {
                throw new IntegrationException(String.format("Failed to extract project name from: %s", remoteUrlString));
            }
            remoteUrlPath = tokens[1].trim();
        } else {
            // Parses urls such as: https://github.com/blackducksoftware/synopsys-detect
            final URL remoteURL = new URL(remoteUrlString);
            remoteUrlPath = remoteURL.getPath().trim();
        }

        return StringUtils.removeEnd(StringUtils.removeStart(remoteUrlPath, "/"), ".git");
    }
}
