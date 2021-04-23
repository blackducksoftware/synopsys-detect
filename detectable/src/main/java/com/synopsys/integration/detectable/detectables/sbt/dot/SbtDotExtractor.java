/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.sbt.dot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.util.OperatingSystemType;

public class SbtDotExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Adding this arg to sbt command line let's it run in the background (IDETECT-2595)
    // Ref: https://github.com/sbt/sbt/issues/701
    public static final String SBT_ARG_TO_ENABLE_BACKGROUND_EXECUTION_LINUX = "-Djline.terminal=jline.UnsupportedTerminal";
    private final DetectableExecutableRunner executableRunner;
    private final SbtDotOutputParser sbtDotOutputParser;
    private final SbtRootNodeFinder sbtRootNodeFinder;
    private final SbtGraphParserTransformer sbtGraphParserTransformer;
    private final SbtDotGraphNodeParser graphNodeParser;

    public SbtDotExtractor(DetectableExecutableRunner executableRunner, SbtDotOutputParser sbtDotOutputParser, SbtRootNodeFinder sbtRootNodeFinder,
        SbtGraphParserTransformer sbtGraphParserTransformer, SbtDotGraphNodeParser graphNodeParser) {
        this.executableRunner = executableRunner;
        this.sbtDotOutputParser = sbtDotOutputParser;
        this.sbtRootNodeFinder = sbtRootNodeFinder;
        this.sbtGraphParserTransformer = sbtGraphParserTransformer;
        this.graphNodeParser = graphNodeParser;
    }

    public Extraction extract(File directory, ExecutableTarget sbt) {
        try {
            List<String> arguments = new ArrayList<>();
            if (OperatingSystemType.determineFromSystem() != OperatingSystemType.WINDOWS) {
                arguments.add(SBT_ARG_TO_ENABLE_BACKGROUND_EXECUTION_LINUX);
            }
            arguments.add("dependencyDot");

            Executable dotExecutable = ExecutableUtils.createFromTarget(directory, sbt, arguments);
            ExecutableOutput dotOutput = executableRunner.executeSuccessfully(dotExecutable);
            List<File> dotGraphs = sbtDotOutputParser.parseGeneratedGraphFiles(dotOutput.getStandardOutputAsList());

            Extraction.Builder extraction = new Extraction.Builder();
            for (File dotGraph : dotGraphs) {
                GraphParser graphParser = new GraphParser(FileUtils.openInputStream(dotGraph));
                Set<String> rootIDs = sbtRootNodeFinder.determineRootIDs(graphParser);
                File projectFolder = dotGraph.getParentFile().getParentFile();//typically found in project-folder/target/<>.dot so .parent.parent == project folder

                if (rootIDs.size() == 1) {
                    String projectId = rootIDs.stream().findFirst().get();
                    DependencyGraph graph = sbtGraphParserTransformer.transformDotToGraph(graphParser, projectId);
                    Dependency projectDependency = graphNodeParser.nodeToDependency(projectId);
                    extraction.codeLocations(new CodeLocation(graph, projectDependency.getExternalId(), projectFolder));
                    if (projectFolder.equals(directory)) {
                        extraction.projectName(projectDependency.getName());
                        extraction.projectVersion(projectDependency.getVersion());
                    }
                } else {
                    logger.warn("Unable to determine which node was the project in an SBT graph: " + dotGraph.toString());
                    logger.warn("This may mean you have extraneous dependencies and should consider removing them. The dependencies are: " + String.join(",", rootIDs));
                    DependencyGraph graph = sbtGraphParserTransformer.transformDotToGraph(graphParser, rootIDs);
                    extraction.codeLocations(new CodeLocation(graph, projectFolder));
                }
            }
            return extraction.success().build();
        } catch (ExecutableFailedException e) {
            return Extraction.fromFailedExecutable(e);
        } catch (IOException | DetectableException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
