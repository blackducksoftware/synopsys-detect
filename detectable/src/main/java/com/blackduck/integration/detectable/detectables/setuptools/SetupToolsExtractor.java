package com.blackduck.integration.detectable.detectables.setuptools;

import org.apache.commons.lang3.StringUtils;

import com.blackduck.integration.bdio.graph.DependencyGraph;
import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.blackduck.integration.detectable.detectables.setuptools.parse.SetupToolsParsedResult;
import com.blackduck.integration.detectable.detectables.setuptools.parse.SetupToolsParser;
import com.blackduck.integration.detectable.detectables.setuptools.transform.SetupToolsGraphTransformer;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.Extraction.Builder;

public class SetupToolsExtractor {
    
    private final SetupToolsGraphTransformer setupToolsTransformer;

    public SetupToolsExtractor(SetupToolsGraphTransformer setupToolsTransformer) {
        this.setupToolsTransformer = setupToolsTransformer;
    }

    public Extraction extract(SetupToolsParser parser, ExecutableTarget pipExe) {
        try {
            SetupToolsParsedResult parsedResult = parser.parse();
            
            DependencyGraph dependencyGraph = setupToolsTransformer.transform(pipExe, parsedResult);

            CodeLocation codeLocation = new CodeLocation(dependencyGraph);

            String projectName = parsedResult.getProjectName();
            String projectVersion = parsedResult.getProjectVersion();

            Builder builder = new Extraction.Builder();
            builder.success(codeLocation);
            
            if (!StringUtils.isEmpty(projectName)) {
                builder.projectName(projectName);
            }
            
            if (!StringUtils.isEmpty(projectVersion)) {
                builder.projectVersion(projectVersion);
            }
            
            return builder.build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
