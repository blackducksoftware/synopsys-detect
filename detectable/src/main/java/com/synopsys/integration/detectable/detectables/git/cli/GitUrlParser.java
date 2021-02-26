/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.git.cli;

import java.net.MalformedURLException;

import org.apache.commons.lang3.StringUtils;

public class GitUrlParser {
    // Parses urls such as: https://github.com/blackducksoftware/synopsys-detect
    public String getRepoName(final String remoteUrlString) throws MalformedURLException {
        final String[] pieces = remoteUrlString.split("[/:]");
        if (pieces.length >= 2) {
            final String organization = pieces[pieces.length - 2];
            final String repo = pieces[pieces.length - 1];
            final String name = String.format("%s/%s", organization, repo);
            return StringUtils.removeEnd(StringUtils.removeStart(name, "/"), ".git");
        } else {
            throw new MalformedURLException("Failed to extract repository name from url. Not logging url for security.");
        }
    }
}
