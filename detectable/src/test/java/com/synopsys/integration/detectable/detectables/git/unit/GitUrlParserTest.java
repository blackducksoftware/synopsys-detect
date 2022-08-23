package com.synopsys.integration.detectable.detectables.git.unit;

import java.net.MalformedURLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.git.cli.GitUrlParser;

class GitUrlParserTest {
    @Test
    void sshUrl() throws MalformedURLException {
        GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "ssh://user@synopsys.com:12345/blackducksoftware/synopsys-detect";
        String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", repoName);
    }

    @Test
    void gitUrl() throws MalformedURLException {
        GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "git://git.yoctoproject.org/poky.git";
        String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("git.yoctoproject.org/poky", repoName);
    }

    @Test
    void gitAtUrl() throws MalformedURLException {
        GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "git@github.com:blackducksoftware/synopsys-detect.git";
        String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", repoName);
    }

    @Test
    void httpsUrl() throws MalformedURLException {
        GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "https://github.com/blackducksoftware/synopsys-detect";
        String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", repoName);
    }

    @Test
    void httpsEncodedUsernamePasswordUrl() throws MalformedURLException {
        GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "https://USERNAME:PASSWORD@SERVER/test/path/to/blackducksoftware/synopsys-detect.git";
        String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", repoName);
    }
}