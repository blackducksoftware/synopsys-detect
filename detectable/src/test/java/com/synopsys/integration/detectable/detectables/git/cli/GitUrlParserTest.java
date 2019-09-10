package com.synopsys.integration.detectable.detectables.git.cli;

import java.net.MalformedURLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.exception.IntegrationException;

class GitUrlParserTest {
    @Test
    void sshUrl() throws MalformedURLException, IntegrationException {
        final GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "ssh://user@synopsys.com:12345/blackducksoftware/synopsys-detect";
        final String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", repoName);
    }

    @Test
    void gitUrl() throws MalformedURLException, IntegrationException {
        final GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "git@github.com:blackducksoftware/synopsys-detect.git";
        final String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", repoName);
    }

    @Test
    void httpsUrl() throws MalformedURLException, IntegrationException {
        final GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "https://github.com/blackducksoftware/synopsys-detect";
        final String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", repoName);
    }
}