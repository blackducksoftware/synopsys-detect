/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.maven.parsing;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.projectinspector.ProjectInspectorParser;

public class MavenProjectInspectorExtractor {
    private final DetectableExecutableRunner executableRunner;
    private final ProjectInspectorParser projectInspectorParser;

    public MavenProjectInspectorExtractor(DetectableExecutableRunner executableRunner, ProjectInspectorParser projectInspectorParser) {
        this.executableRunner = executableRunner;
        this.projectInspectorParser = projectInspectorParser;
    }

    public Extraction extract(File targetDirectory, File outputDirectory, ExecutableTarget inspector) throws ExecutableFailedException {
        File outputFile = new File(outputDirectory, "inspection.json");

        List<String> arguments = new LinkedList<>();
        arguments.add("inspect");
        arguments.add("--dir");
        arguments.add(targetDirectory.toString());
        arguments.add("--output-file");
        arguments.add(outputFile.toString());
        //arguments.add("--strategy");
        //arguments.add("MSBUILD");

        executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(targetDirectory, inspector, arguments));

        try {
            String outputContents = FileUtils.readFileToString(outputFile, StandardCharsets.UTF_8);
            List<CodeLocation> codeLocations = projectInspectorParser.parse(outputContents);
            return new Extraction.Builder().success(codeLocations).build();
        } catch (IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
