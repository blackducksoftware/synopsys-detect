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
    void testOrganizationGflagsHttp() throws MalformedURLException {
        GithubUrlParser parser = new GithubUrlParser("http://github.com/gflags/gflags/archive/v2.2.2.tar.gz");
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

    @Test
    void testMissingArchive() {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/google/glog");
        try {
            parser.getVersion();
            Assertions.fail("Expected MalformedURLException");
        } catch (MalformedURLException e) {
            // expected
        }
    }

    @Test
    void testUnexpectedArchive() {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/google/glog/files/v0.4.0.tar.gz");
        try {
            parser.getVersion();
            Assertions.fail("Expected MalformedURLException");
        } catch (MalformedURLException e) {
            // expected
        }
    }

    @Test
    void testMissingFilename() {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/google/glog/archive/");
        try {
            parser.getVersion();
            Assertions.fail("Expected MalformedURLException");
        } catch (MalformedURLException e) {
            // expected
        }
    }

    @Test
    void testMissingExtension() throws MalformedURLException {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/google/glog/archive/v0.4.0");
        try {
            parser.getVersion();
            Assertions.fail("Expected MalformedURLException");
        } catch (MalformedURLException e) {
            // expected
        }
    }

    // "https://github.com/bazelbuild/bazel-skylib/archive/2169ae1c374aab4a09aa90e65efe1a3aad4e279b.tar.gz"
    @Test
    void testOrganizationSkylib() throws MalformedURLException {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/bazelbuild/bazel-skylib/archive/2169ae1c374aab4a09aa90e65efe1a3aad4e279b.tar.gz");
        Assertions.assertEquals("bazelbuild", parser.getOrganization());
    }

    @Test
    void testRepoSkylib() throws MalformedURLException {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/bazelbuild/bazel-skylib/archive/2169ae1c374aab4a09aa90e65efe1a3aad4e279b.tar.gz");
        Assertions.assertEquals("bazel-skylib", parser.getRepo());
    }

    @Test
    void testVersionSkylib() throws MalformedURLException {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/bazelbuild/bazel-skylib/archive/2169ae1c374aab4a09aa90e65efe1a3aad4e279b.tar.gz");
        Assertions.assertEquals("2169ae1c374aab4a09aa90e65efe1a3aad4e279b", parser.getVersion());
    }

    @Test
    void testNonGithub() throws MalformedURLException {
        GithubUrlParser parser = new GithubUrlParser("https://www.libsdl.org/release/SDL2-2.0.8.zip");
        try {
            parser.getOrganization();
            Assertions.fail("Expected MalformedURLException");
        } catch (MalformedURLException e) {
            // expected
        }
        try {
            parser.getRepo();
            Assertions.fail("Expected MalformedURLException");
        } catch (MalformedURLException e) {
            // expected
        }
        try {
            parser.getVersion();
            Assertions.fail("Expected MalformedURLException");
        } catch (MalformedURLException e) {
            // expected
        }
    }

    // "https://github.com/bazelbuild/bazel-skylib/releases/download/1.0.2/bazel-skylib-1.0.2.tar.gz"
    @Test
    void testReleasesDownload() throws MalformedURLException {
        GithubUrlParser parser = new GithubUrlParser("https://github.com/bazelbuild/bazel-skylib/releases/download/1.0.2/bazel-skylib-1.0.2.tar.gz");
        Assertions.assertEquals("bazelbuild", parser.getOrganization());
        Assertions.assertEquals("bazel-skylib", parser.getRepo());
        Assertions.assertEquals("1.0.2", parser.getVersion());
    }
}
