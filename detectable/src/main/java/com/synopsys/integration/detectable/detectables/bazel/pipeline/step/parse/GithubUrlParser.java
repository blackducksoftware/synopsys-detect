package com.synopsys.integration.detectable.detectables.bazel.pipeline.step.parse;

import java.net.MalformedURLException;

import org.jetbrains.annotations.NotNull;

/*
 * Parse organization, repo, and version from github urls like these:
 * https://github.com/gflags/gflags/archive/v2.2.2.tar.gz
 * https://github.com/bazelbuild/bazel-skylib/releases/download/1.0.2/bazel-skylib-1.0.2.tar.gz
 */
public class GithubUrlParser {

    public static final String ARCHIVE_SEGMENT = "/archive/";
    public static final String RELEASES_DOWNLOAD_SEGMENT = "/releases/download/";

    public String parseOrganization(String url) throws MalformedURLException {
        int indexOrganization = calculateIndexOrganization(url);
        return parseOrganization(url, indexOrganization);
    }

    public String parseRepo(String url) throws MalformedURLException {
        int indexRepo = calculateIndexRepo(url);
        String repoEtc = url.substring(indexRepo);
        int slashAfterRepo = repoEtc.indexOf('/');
        return repoEtc.substring(0, slashAfterRepo);
    }

    public String parseVersion(String url) throws MalformedURLException {
        int indexSlashArchive = url.indexOf(ARCHIVE_SEGMENT);
        int indexSlashReleasesDownload = url.indexOf(RELEASES_DOWNLOAD_SEGMENT);
        if ((indexSlashArchive < 0) && (indexSlashReleasesDownload < 0)) {
            throw new MalformedURLException("Missing archive between repo and filename");
        }
        if (indexSlashArchive >= 0) {
            return parseVersionForArchive(url, indexSlashArchive);
        } else {
            return parseVersionForReleasesDownload(url, indexSlashReleasesDownload);
        }
    }

    @NotNull
    private String parseVersionForReleasesDownload(String url, int indexSlashReleasesDownload) throws MalformedURLException {
        int indexVersionDir = indexSlashReleasesDownload + RELEASES_DOWNLOAD_SEGMENT.length();
        if (indexVersionDir > url.length()) {
            throw new MalformedURLException("Missing version directory");
        }
        String versionEtc = url.substring(indexVersionDir);
        int indexSlashAfterVersionDir = versionEtc.indexOf('/');
        return versionEtc.substring(0, indexSlashAfterVersionDir);
    }

    @NotNull
    private String parseVersionForArchive(String url, int indexSlashArchive) throws MalformedURLException {
        int indexFilename = indexSlashArchive + ARCHIVE_SEGMENT.length();
        if (indexFilename > url.length()) {
            throw new MalformedURLException("Missing filename");
        }
        String filename = url.substring(indexFilename);
        int indexExtension = calculateIndexExtension(filename);
        return filename.substring(0, indexExtension);
    }

    @NotNull
    private String parseOrganization(String url, int indexOrganization) {
        String organizationRepoPath = url.substring(indexOrganization);
        int indexSlashAfterOrganization = organizationRepoPath.indexOf('/');
        return organizationRepoPath.substring(0, indexSlashAfterOrganization);
    }

    private int calculateIndexRepo(String url) throws MalformedURLException {
        int indexOrganization = calculateIndexOrganization(url);
        String organization = parseOrganization(url, indexOrganization);
        return calculateIndexRepo(indexOrganization, organization);
    }

    private int calculateIndexOrganization(String url) throws MalformedURLException {
        int indexPostSchemeColon = url.indexOf("://github.com/");
        if (indexPostSchemeColon < 1) {
            throw new MalformedURLException("Missing scheme://github.com/ prefix");
        }
        return indexPostSchemeColon + "://github.com/".length();
    }

    private int calculateIndexRepo(int indexOrganization, String organization) throws MalformedURLException {
        return indexOrganization + organization.length() + 1;
    }

    private int calculateIndexExtension(String filename) throws MalformedURLException {
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
