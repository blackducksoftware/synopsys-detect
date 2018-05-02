package com.blackducksoftware.integration.hub.detect.extraction.bomtool.gradle;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleParseResult;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleReportParser;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class GradleInspectorExtractor extends Extractor<GradleInspectorContext> {

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableRunner executableRunner;

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    GradleReportParser gradleReportParser;

    @Override
    public Extraction extract(final GradleInspectorContext context) {
        try {
            String gradleCommand = detectConfiguration.getGradleBuildCommand();
            gradleCommand = gradleCommand.replaceAll("dependencies", "").trim();

            final List<String> arguments = new ArrayList<>();
            if (StringUtils.isBlank(gradleCommand)) {
                arguments.addAll(Arrays.asList(gradleCommand.split(" ")));
            }
            arguments.add("dependencies");
            arguments.add(String.format("--init-script=%s", context.gradleInspector));

            //logger.info("using ${gradleInspectorManager.getInitScriptPath()} as the path for the gradle init script");
            final Executable executable = new Executable(context.directory, context.gradleExe, arguments);
            executableRunner.execute(executable);

            final File buildDirectory = new File(context.directory, "build");
            final File blackduckDirectory = new File(buildDirectory, "blackduck");

            final List<File> codeLocationFiles = detectFileManager.findFiles(blackduckDirectory, "*_dependencyGraph.txt");

            final List<DetectCodeLocation> codeLocations = new ArrayList<>();
            for (final File file : codeLocationFiles) {
                final InputStream stream = new FileInputStream(file);
                final GradleParseResult result = gradleReportParser.parseDependencies(stream);
                stream.close();
                final DetectCodeLocation codeLocation = result.codeLocation;
                codeLocations.add(codeLocation);
            }
            if (detectConfiguration.getCleanupDetectFiles()) {
                FileUtils.deleteDirectory(blackduckDirectory);
            }
            return new Extraction(ExtractionResult.Success, codeLocations);
        } catch (final Exception e) {
            return new Extraction(ExtractionResult.Exception, e);
        }
    }

}
