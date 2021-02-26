/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.cpan;

import java.io.File;
import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.cpan.parse.CpanListParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.ExecutableOutput;

public class CpanCliExtractor {
    private final CpanListParser cpanListParser;
    private final DetectableExecutableRunner executableRunner;

    public CpanCliExtractor(CpanListParser cpanListParser, DetectableExecutableRunner executableRunner) {
        this.cpanListParser = cpanListParser;
        this.executableRunner = executableRunner;
    }

    public Extraction extract(ExecutableTarget cpanExe, ExecutableTarget cpanmExe, File workingDirectory) {
        try {
            ExecutableOutput cpanListOutput = executableRunner.execute(ExecutableUtils.createFromTarget(workingDirectory, cpanExe, "-l"));
            List<String> listText = cpanListOutput.getStandardOutputAsList();

            ExecutableOutput showdepsOutput = executableRunner.execute(ExecutableUtils.createFromTarget(workingDirectory, cpanmExe, "--showdeps", "."));
            List<String> showdeps = showdepsOutput.getStandardOutputAsList();

            DependencyGraph dependencyGraph = cpanListParser.parse(listText, showdeps);
            CodeLocation detectCodeLocation = new CodeLocation(dependencyGraph);
            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
