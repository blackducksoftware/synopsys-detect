package com.synopsys.integration.detectable.detectables.setuptools;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.setuptools.parse.SetupToolsParsedResult;
import com.synopsys.integration.detectable.detectables.setuptools.parse.SetupToolsParser;
import com.synopsys.integration.detectable.detectables.setuptools.transform.SetupToolsGraphTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.Extraction.Builder;

public class SetupToolsExtractor {
    
    private final SetupToolsGraphTransformer setupToolsTransformer;

    public SetupToolsExtractor(SetupToolsGraphTransformer setupToolsTransformer) {
        this.setupToolsTransformer = setupToolsTransformer;
    }

    public Extraction extract(List<SetupToolsParser> parsers, ExecutableTarget pipExe) {
        try {
            SetupToolsParsedResult parsedResult = new SetupToolsParsedResult();
            
            for (SetupToolsParser setupToolsParser : parsers) {
                setupToolsParser.parse(parsedResult);
            }
            
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
