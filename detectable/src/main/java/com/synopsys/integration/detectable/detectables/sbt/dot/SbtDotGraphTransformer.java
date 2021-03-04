package com.synopsys.integration.detectable.detectables.sbt.dot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

public class SbtDotGraphTransformer {
    private final SbtProjectMatcher sbtProjectMatcher;
    private final SbtGraphParserTransformer sbtGraphParserTransformer;
    private final ExternalIdFactory externalIdFactory;

    public SbtDotGraphTransformer(final SbtProjectMatcher sbtProjectMatcher, final SbtGraphParserTransformer sbtGraphParserTransformer, final ExternalIdFactory externalIdFactory) {
        this.sbtProjectMatcher = sbtProjectMatcher;
        this.sbtGraphParserTransformer = sbtGraphParserTransformer;
        this.externalIdFactory = externalIdFactory;
    }

    public List<CodeLocation> createCodeLocations(List<SbtProject> allProjects, List<File> dotFiles) throws IOException {
        List<CodeLocation> codeLocations = new ArrayList<>();
        for (File dotGraph : dotFiles) {
            GraphParser graphParser = new GraphParser(FileUtils.openInputStream(dotGraph));
            SbtProjectMatch matchingProject = sbtProjectMatcher.determineProjectID(graphParser, allProjects);
            DependencyGraph graph = sbtGraphParserTransformer.transformDotToGraph(graphParser, matchingProject.getNodeIdsAtRoot());

            ExternalId projectExternalId = null;
            if (matchingProject.getRelatedProject() != null) {
                projectExternalId = externalIdFactory.createMavenExternalId(matchingProject.getRelatedProject().getGroup(), matchingProject.getRelatedProject().getName(), matchingProject.getRelatedProject().getVersion());
            }
            codeLocations.add(new CodeLocation(graph, projectExternalId, dotGraph.getParentFile()));
        }
        return codeLocations;
    }
}
