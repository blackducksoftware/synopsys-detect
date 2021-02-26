/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.nuget;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.PassedResultBuilder;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.explanation.FoundInspector;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.InspectorNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "C#", forge = "NuGet.org",
    requirementsMarkdown = "File: a project file with one of the following extensions: .csproj, .fsproj, .vbproj, .asaproj, .dcproj, .shproj, .ccproj, " +
                               ".sfproj, .njsproj, .vcxproj, .vcproj, .xproj, .pyproj, .hiveproj, .pigproj, .jsproj, .usqlproj, .deployproj, " +
                               ".msbuildproj, .sqlproj, .dbproj, .rproj")
public class NugetProjectDetectable extends Detectable {
    static final List<String> SUPPORTED_PROJECT_PATTERNS = Arrays.asList(
        // C#
        "*.csproj",
        // F#
        "*.fsproj",
        // VB
        "*.vbproj",
        // Azure Stream Analytics
        "*.asaproj",
        // Docker Compose
        "*.dcproj",
        // Shared Projects
        "*.shproj",
        // Cloud Computing
        "*.ccproj",
        // Fabric Application
        "*.sfproj",
        // Node.js
        "*.njsproj",
        // VC++
        "*.vcxproj",
        // VC++
        "*.vcproj",
        // .NET Core
        "*.xproj",
        // Python
        "*.pyproj",
        // Hive
        "*.hiveproj",
        // Pig
        "*.pigproj",
        // JavaScript
        "*.jsproj",
        // U-SQL
        "*.usqlproj",
        // Deployment
        "*.deployproj",
        // Common Project System Files
        "*.msbuildproj",
        // SQL
        "*.sqlproj",
        // SQL Project Files
        "*.dbproj",
        // RStudio
        "*.rproj"
    );

    private final FileFinder fileFinder;
    private final NugetInspectorOptions nugetInspectorOptions;
    private final NugetInspectorResolver nugetInspectorResolver;
    private final NugetInspectorExtractor nugetInspectorExtractor;

    private NugetInspector inspector;
    private List<File> projectFiles = new ArrayList<>();

    public NugetProjectDetectable(final DetectableEnvironment detectableEnvironment, final FileFinder fileFinder, final NugetInspectorOptions nugetInspectorOptions, final NugetInspectorResolver nugetInspectorResolver,
        final NugetInspectorExtractor nugetInspectorExtractor) {
        super(detectableEnvironment);
        this.fileFinder = fileFinder;
        this.nugetInspectorOptions = nugetInspectorOptions;
        this.nugetInspectorResolver = nugetInspectorResolver;
        this.nugetInspectorExtractor = nugetInspectorExtractor;
    }

    @Override
    public DetectableResult applicable() {
        projectFiles = fileFinder.findFiles(environment.getDirectory(), SUPPORTED_PROJECT_PATTERNS);

        if (projectFiles != null && projectFiles.size() > 0) {
            PassedResultBuilder passedResultBuilder = new PassedResultBuilder();
            projectFiles.forEach(passedResultBuilder::foundFile);
            return passedResultBuilder.build();
        } else {
            return new FilesNotFoundDetectableResult(SUPPORTED_PROJECT_PATTERNS);
        }
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        inspector = nugetInspectorResolver.resolveNugetInspector();

        if (inspector == null) {
            return new InspectorNotFoundDetectableResult("nuget");
        }

        return new PassedDetectableResult(new FoundInspector(inspector.getClass().getSimpleName())); //TODO: Inspector should describe itself.
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return nugetInspectorExtractor.extract(projectFiles, extractionEnvironment.getOutputDirectory(), inspector, nugetInspectorOptions);
    }

}