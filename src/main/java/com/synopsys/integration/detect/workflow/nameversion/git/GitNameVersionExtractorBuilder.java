/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.nameversion.git;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableResolver;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.workflow.nameversion.git.cli.GitCliExtractor;
import com.synopsys.integration.detect.workflow.nameversion.git.cli.GitUrlParser;
import com.synopsys.integration.detect.workflow.nameversion.git.parse.GitConfigNameVersionTransformer;
import com.synopsys.integration.detect.workflow.nameversion.git.parse.GitConfigNodeTransformer;
import com.synopsys.integration.detect.workflow.nameversion.git.parse.GitFileParser;
import com.synopsys.integration.detect.workflow.nameversion.git.parse.GitParseExtractor;

public class GitNameVersionExtractorBuilder {
    public static GitNameVersionExtractor build(FileFinder fileFinder, DetectExecutableRunner executableRunner, DetectExecutableResolver gitResolver) {
        return new GitNameVersionExtractor(gitCliExtractor(executableRunner), gitParseExtractor(), gitResolver, fileFinder);
    }

    private static GitFileParser gitFileParser() {
        return new GitFileParser();
    }

    private static GitConfigNameVersionTransformer gitConfigNameVersionTransformer() {
        return new GitConfigNameVersionTransformer(gitUrlParser());
    }

    private static GitConfigNodeTransformer gitConfigNodeTransformer() {
        return new GitConfigNodeTransformer();
    }

    private static GitParseExtractor gitParseExtractor() {
        return new GitParseExtractor(gitFileParser(), gitConfigNameVersionTransformer(), gitConfigNodeTransformer());
    }

    private static GitUrlParser gitUrlParser() {
        return new GitUrlParser();
    }

    private static GitCliExtractor gitCliExtractor(DetectExecutableRunner executableRunner) {
        return new GitCliExtractor(executableRunner, gitUrlParser());
    }
}
