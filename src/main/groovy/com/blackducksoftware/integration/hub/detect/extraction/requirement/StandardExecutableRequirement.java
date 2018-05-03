package com.blackducksoftware.integration.hub.detect.extraction.requirement;

import java.io.File;

public class StandardExecutableRequirement extends Requirement<File> {

    public StandardExecutableType executableType;

    public enum StandardExecutableType {
        CONDA,
        CPAN,
        CPANM,
        DOCKER,
        BASH,
        GO,
        REBAR3,
        PEAR
    }

}
