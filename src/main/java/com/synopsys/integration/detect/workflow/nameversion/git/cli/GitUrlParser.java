/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.nameversion.git.cli;

import java.net.MalformedURLException;

import org.apache.commons.lang3.StringUtils;

public class GitUrlParser {
    // Parses urls such as: https://github.com/blackducksoftware/synopsys-detect
    public String getRepoName(String remoteUrlString) throws MalformedURLException {
        String[] pieces = remoteUrlString.split("[/:]");
        if (pieces.length >= 2) {
            String organization = pieces[pieces.length - 2];
            String repo = pieces[pieces.length - 1];
            String name = String.format("%s/%s", organization, repo);
            return StringUtils.removeEnd(StringUtils.removeStart(name, "/"), ".git");
        } else {
            throw new MalformedURLException("Failed to extract repository name from url. Not logging url for security.");
        }
    }
}
