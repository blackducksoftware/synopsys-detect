package com.synopsys.integration.detectable.detectables.bazel.pipeline.step.parse;

import java.net.MalformedURLException;

import org.jetbrains.annotations.NotNull;

public class GithubUrlParser {
    public String getOrganization(String url) throws MalformedURLException {
        int indexOrganization = getIndexOrganization(url);
        return getOrganization(url, indexOrganization);
    }

    public String getRepo(String url) throws MalformedURLException {
        int indexRepo = getIndexRepo(url);
        String repoEtc = url.substring(indexRepo);
        int slashAfterRepo = repoEtc.indexOf('/');
        return repoEtc.substring(0, slashAfterRepo);
    }

    // "https://github.com/bazelbuild/bazel-skylib/releases/download/1.0.2/bazel-skylib-1.0.2.tar.gz"
    public String getVersion(String url) throws MalformedURLException {
        int indexSlashArchive = url.indexOf("/archive/");
        int indexSlashReleasesDownload = url.indexOf("/releases/download/");
        if ((indexSlashArchive < 0) && (indexSlashReleasesDownload < 0)) {
            throw new MalformedURLException("Missing archive between repo and filename");
        }
        // TODO split up: one method per type
        if (indexSlashArchive >= 0) {
            int indexFilename = indexSlashArchive + "/archive/".length();
            if (indexFilename > url.length()) {
                throw new MalformedURLException("Missing filename");
            }
            String filename = url.substring(indexFilename);
            int indexExtension = getIndexExtension(filename);
            return filename.substring(0, indexExtension);
        } else {
            int indexVersionDir = indexSlashReleasesDownload + "/releases/download/".length();
            if (indexVersionDir > url.length()) {
                throw new MalformedURLException("Missing version directory");
            }
            String versionEtc = url.substring(indexVersionDir);
            int indexSlashAfterVersionDir = versionEtc.indexOf('/');
            return versionEtc.substring(0, indexSlashAfterVersionDir);
        }
    }

    @NotNull
    private String getOrganization(String url, int indexOrganization) {
        String organizationRepoPath = url.substring(indexOrganization);
        int indexSlashAfterOrganization = organizationRepoPath.indexOf('/');
        return organizationRepoPath.substring(0, indexSlashAfterOrganization);
    }

    private int getIndexRepo(String url) throws MalformedURLException {
        int indexOrganization = getIndexOrganization(url);
        String organization = getOrganization(url, indexOrganization);
        return getIndexRepo(indexOrganization, organization);
    }

    private int getIndexOrganization(String url) throws MalformedURLException {
        int indexPostSchemeColon = url.indexOf("://github.com/");
        if (indexPostSchemeColon < 1) {
            throw new MalformedURLException("Missing scheme://github.com/ prefix");
        }
        return indexPostSchemeColon + "://github.com/".length();
    }

    private int getIndexRepo(int indexOrganization, String organization) throws MalformedURLException {
        return indexOrganization + organization.length() + 1;
    }

    private int getIndexExtension(String filename) throws MalformedURLException {
        if (filename.endsWith(".tar.gz")) {
            return filename.length() - ".tar.gz".length();
        }
        if (filename.endsWith(".zip")) {
            return filename.length() - ".zip".length();
        }
        if (filename.endsWith(".gz")) {
            return filename.length() - ".gz".length();
        }
        throw new MalformedURLException("Unrecognized file extension");
    }
}
