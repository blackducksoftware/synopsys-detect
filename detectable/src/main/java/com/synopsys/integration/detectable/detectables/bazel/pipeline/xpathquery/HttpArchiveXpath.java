package com.synopsys.integration.detectable.detectables.bazel.pipeline.xpathquery;

public class HttpArchiveXpath {
    // http_archive, go_repository, and git_repository are a related family of rules:
    // https://github.com/bazelbuild/bazel-gazelle/blob/master/repository.md
    // Each supports URLs in either a urls list, or a single url field: 3 rules * 2 forms = 6 queries:
    private static final String HTTP_ARCHIVE_XPATH_TO_URL_LIST = "/query/rule[@class='http_archive']/list[@name='urls']/string";
    private static final String HTTP_ARCHIVE_XPATH_TO_SINGLE_URL = "/query/rule[@class='http_archive']/string[@name='url']";
    private static final String HTTP_ARCHIVE_XPATH_EVERY_URL = HTTP_ARCHIVE_XPATH_TO_URL_LIST + "|" + HTTP_ARCHIVE_XPATH_TO_SINGLE_URL;

    private static final String GO_REPO_XPATH_TO_URL_LIST = "/query/rule[@class='go_repository']/list[@name='urls']/string";
    private static final String GO_REPO_XPATH_TO_SINGLE_URL = "/query/rule[@class='go_repository']/string[@name='url']";
    private static final String GO_REPO_XPATH_EVERY_URL = GO_REPO_XPATH_TO_URL_LIST + "|" + GO_REPO_XPATH_TO_SINGLE_URL;

    private static final String GIT_REPO_XPATH_TO_URL_LIST = "/query/rule[@class='git_repository']/list[@name='urls']/string";
    private static final String GIT_REPO_XPATH_TO_SINGLE_URL = "/query/rule[@class='git_repository']/string[@name='url']";
    private static final String GIT_REPO_XPATH_EVERY_URL = GIT_REPO_XPATH_TO_URL_LIST + "|" + GIT_REPO_XPATH_TO_SINGLE_URL;

    public static final String QUERY = HTTP_ARCHIVE_XPATH_EVERY_URL + "|" + GO_REPO_XPATH_EVERY_URL + "|" + GIT_REPO_XPATH_EVERY_URL;

}
