package com.synopsys.integration.detectable.detectables.gradle.inspection;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportParser;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportTransformer;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleRootMetadataParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.util.NameVersion;

public class GradleInspectorExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileFinder fileFinder;
    private final GradleRunner gradleRunner;
    private final GradleReportParser gradleReportParser;
    private final GradleReportTransformer gradleReportTransformer;
    private final GradleRootMetadataParser gradleRootMetadataParser;
    private final ToolVersionLogger toolVersionLogger;

    public GradleInspectorExtractor(
        FileFinder fileFinder,
        GradleRunner gradleRunner,
        GradleReportParser gradleReportParser,
        GradleReportTransformer gradleReportTransformer,
        GradleRootMetadataParser gradleRootMetadataParser,
        ToolVersionLogger toolVersionLogger
    ) {
        this.fileFinder = fileFinder;
        this.gradleRunner = gradleRunner;
        this.gradleReportParser = gradleReportParser;
        this.gradleReportTransformer = gradleReportTransformer;
        this.gradleRootMetadataParser = gradleRootMetadataParser;
        this.toolVersionLogger = toolVersionLogger;
    }

    public Extraction extract(File directory, ExecutableTarget gradleExe, @Nullable String gradleCommand, ProxyInfo proxyInfo, File gradleInspector, File outputDirectory)
        throws ExecutableFailedException {
        try {
            toolVersionLogger.log(directory, gradleExe);
            gradleRunner.runGradleDependencies(directory, gradleExe, gradleInspector, gradleCommand, proxyInfo, outputDirectory);

            File rootProjectMetadataFile = fileFinder.findFile(outputDirectory, "rootProjectMetadata.txt");
            List<File> reportFiles = fileFinder.findFiles(outputDirectory,"*_dependencyGraph.txt");
            List<CodeLocation> codeLocations = new ArrayList<>();

            File[] files = new File[reportFiles.size()];
            reportFiles.toArray(files);
            List<File> reportFilesSorted = Arrays.asList(sortFilesByDepth(files));

            reportFilesSorted.stream()
                .map(gradleReportParser::parseReport)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(gradleReportTransformer::transform)
                .forEach(codeLocations::add);

            Optional<NameVersion> projectNameVersion = Optional.empty();
            if (rootProjectMetadataFile != null) {
                projectNameVersion = parseRootProjectMetadataFile(rootProjectMetadataFile);
            } else {
                logger.warn("Gradle inspector did not create a meta data report so no project version information was found.");
            }

            return new Extraction.Builder()
                .success(codeLocations)
                .nameVersionIfPresent(projectNameVersion)
                .build();
        } catch (IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private Optional<NameVersion> parseRootProjectMetadataFile(File rootProjectMetadataFile) {
        try {
            List<String> rootProjectMetadataLines = FileUtils.readLines(rootProjectMetadataFile, StandardCharsets.UTF_8);
            return Optional.of(gradleRootMetadataParser.parseRootProjectNameVersion(rootProjectMetadataLines));
        } catch (IOException e) {
            logger.warn("Failed to parse file {}", rootProjectMetadataFile.getAbsolutePath());
            return Optional.empty();
        }
    }

    private File[] sortFilesByDepth(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int n1 = extractDepthNumber(o1.getName());
                int n2 = extractDepthNumber(o2.getName());
                return n1 - n2;
            }

            private int extractDepthNumber(String name) {
                int i;
                try {
                    int s = name.indexOf("depth") + 5;
                    int e = name.indexOf("_dependencyGraph");
                    String number = name.substring(s, e);
                    i = Integer.parseInt(number);
                } catch(Exception e) {
                    logger.error("The file name is not analogous to the structure expected: " + name);
                    i = 0; //  default to 0
                }
                return i;
            }
        });

        return files;
    }

}
