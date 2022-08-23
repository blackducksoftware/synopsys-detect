package com.synopsys.integration.detectable.detectables.bazel.unit;

import java.net.MalformedURLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.parse.GithubUrlParser;

public class GithubUrlParserTest {

    @Test
    void testOrganizationGflags() throws MalformedURLException {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/gflags/gflags/archive/v2.2.2.tar.gz");
        Assertions.assertEquals("gflags", parser.getOrganization());
    }

    @Test
    void testRepoGflags() throws MalformedURLException {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/gflags/gflags/archive/v2.2.2.tar.gz");
        Assertions.assertEquals("gflags", parser.getRepo());
    }

    @Test
    void testVersionGflags() throws MalformedURLException {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/gflags/gflags/archive/v2.2.2.tar.gz");
        Assertions.assertEquals("v2.2.2", parser.getVersion());
    }

    @Test
    void testOrganizationGlog() throws MalformedURLException {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/google/glog/archive/v0.4.0.tar.gz");
        Assertions.assertEquals("google", parser.getOrganization());
    }

    @Test
    void testRepoGlog() throws MalformedURLException {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/google/glog/archive/v0.4.0.tar.gz");
        Assertions.assertEquals("glog", parser.getRepo());
    }

    @Test
    void testVersionGlog() throws MalformedURLException {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/google/glog/archive/v0.4.0.tar.gz");
        Assertions.assertEquals("v0.4.0", parser.getVersion());
    }

    @Test
    void testVersionZip() throws MalformedURLException {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/google/glog/archive/v0.4.0.zip");
        Assertions.assertEquals("v0.4.0", parser.getVersion());
    }

    @Test
    void testVersionGZip() throws MalformedURLException {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/google/glog/archive/v0.4.0.gz");
        Assertions.assertEquals("v0.4.0", parser.getVersion());
    }
}
