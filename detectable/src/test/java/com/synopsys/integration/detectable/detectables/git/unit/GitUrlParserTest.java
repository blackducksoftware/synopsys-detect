package com.synopsys.integration.detectable.detectables.git.unit;

import java.net.MalformedURLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.git.GitUrlParser;

class GitUrlParserTest {
    @Test
    void testGetRepoName_sshUrl() throws MalformedURLException {
        GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "ssh://user@synopsys.com:12345/blackducksoftware/synopsys-detect";
        String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", repoName);
    }

    @Test
    void testGetRepoName_gitUrl() throws MalformedURLException {
        GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "git://git.yoctoproject.org/poky.git";
        String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("git.yoctoproject.org/poky", repoName);
    }

    @Test
    void testGetRepoName_gitAtUrl() throws MalformedURLException {
        GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "git@github.com:blackducksoftware/synopsys-detect.git";
        String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", repoName);
    }

    @Test
    void testGetRepoName_httpsUrl() throws MalformedURLException {
        GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "https://github.com/blackducksoftware/synopsys-detect";
        String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", repoName);
    }

    @Test
    void testGetRepoName_httpsEncodedUsernamePasswordUrl() throws MalformedURLException {
        GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "https://USERNAME:PASSWORD@SERVER/test/path/to/blackducksoftware/synopsys-detect.git";
        String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", repoName);
    }

    @Test
    void testRemoveCredentialsFromUrl_nonUri() {
        GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "git@github.com:blackducksoftware/synopsys-detect.git";
        String sanitized = gitUrlParser.removeCredentialsFromUri(remoteUrl);

        Assertions.assertEquals(remoteUrl, sanitized);
    }

    @Test
    void testRemoveCredentialsFromUrl_sshUri() {
        GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "ssh://user@synopsys.com:12345/blackducksoftware/synopsys-detect";
        String sanitized = gitUrlParser.removeCredentialsFromUri(remoteUrl);

        Assertions.assertEquals("ssh://synopsys.com:12345/blackducksoftware/synopsys-detect", sanitized);
    }

    @Test
    void testRemoveCredentialsFromUrl_httpsWithCredentials() {
        GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "https://user:pass@github.com/blackducksoftware/synopsys-detect.git";
        String sanitized = gitUrlParser.removeCredentialsFromUri(remoteUrl);

        Assertions.assertEquals("https://github.com/blackducksoftware/synopsys-detect.git", sanitized);
    }

    @Test
    void testRemoveCredentialsFromUrl_httpsWithoutCredentials() {
        GitUrlParser gitUrlParser = new GitUrlParser();
        final String remoteUrl = "https://github.com/blackducksoftware/synopsys-detect.git";
        String sanitized = gitUrlParser.removeCredentialsFromUri(remoteUrl);

        Assertions.assertEquals(remoteUrl, sanitized);
    }
}
