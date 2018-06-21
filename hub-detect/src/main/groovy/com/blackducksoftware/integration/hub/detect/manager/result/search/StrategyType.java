package com.blackducksoftware.integration.hub.detect.manager.result.search;

public enum StrategyType {
    PODLOCK,
    CONDA_CLI,
    CPAN_CLI,
    PACKRAT_LOCK,
    DOCKER,
    GO_CLI,
    GO_LOCK,
    GO_DEPS,
    GO_VNDR,
    GRADLE_INSPECTOR,
    REBAR,
    MAVEN_POM_CLI,
    MAVEN_POM_WRAPPER_CLI,
    NPM_CLI,
    NPM_PACKAGELOCK,
    NPM_SHRINKWRAP,
    NUGET_PROJECT_INSPECTOR,
    NUGET_SOLUTION_INSPECTOR,
    COMPOSER_LOCK,
    PEAR_CLI,
    PIP_ENV,
    PIP_INSPECTOR,
    GEMLOCK,
    SBT_RESOLUTION_CACHE,
    YARN_LOCK
}
