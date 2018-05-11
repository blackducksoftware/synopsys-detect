package com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class NugetProjectStrategy extends Strategy<NugetInspectorContext, NugetInspectorExtractor> {
    static final String[] SUPPORTED_PROJECT_PATTERNS = new String[] {
            //C#
            "*.csproj",
            //F#
            "*.fsproj",
            //VB
            "*.vbproj",
            //Azure Stream Analytics
            "*.asaproj",
            //Docker Compose
            "*.dcproj",
            //Shared Projects
            "*.shproj",
            //Cloud Computing
            "*.ccproj",
            //Fabric Application
            "*.sfproj",
            //Node.js
            "*.njsproj",
            //VC++
            "*.vcxproj",
            //VC++
            "*.vcproj",
            //.NET Core
            "*.xproj",
            //Python
            "*.pyproj",
            //Hive
            "*.hiveproj",
            //Pig
            "*.pigproj",
            //JavaScript
            "*.jsproj",
            //U-SQL
            "*.usqlproj",
            //Deployment
            "*.deployproj",
            //Common Project System Files
            "*.msbuildproj",
            //SQL
            "*.sqlproj",
            //SQL Project Files
            "*.dbproj",
            //RStudio
            "*.rproj"
    };
    static final String SUPPORTED_PROJECT_PATTERNS_DESC = Arrays.asList(SUPPORTED_PROJECT_PATTERNS).stream().collect(Collectors.joining(","));

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public NugetInspectorManager nugetInspectorManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    public NugetProjectStrategy() {
        super("Project", BomToolType.NUGET, NugetInspectorContext.class, NugetInspectorExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final NugetInspectorContext context) {
        for (final String filepattern : SUPPORTED_PROJECT_PATTERNS) {
            if (fileFinder.findFile(evaluation.getDirectory(), filepattern) != null) {
                return Applicable.doesApply();
            }
        }
        return Applicable.doesNotApply("No files found with pattern: " + SUPPORTED_PROJECT_PATTERNS_DESC);
    }

    public Extractable extractable(final EvaluationContext evaluation, final NugetInspectorContext context){
        context.inspectorExe = nugetInspectorManager.findNugetInspector(evaluation);

        if (context.inspectorExe == null) {
            return Extractable.canNotExtract("No Nuget executable was found.");
        }

        return Extractable.canExtract();
    }


}
