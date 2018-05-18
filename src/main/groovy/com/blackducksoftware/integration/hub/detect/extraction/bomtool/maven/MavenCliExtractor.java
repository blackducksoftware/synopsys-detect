package com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven.parse.MavenCodeLocationPackager;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven.parse.MavenParseResult;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class MavenCliExtractor extends Extractor<MavenCliContext> {

    @Autowired
    private MavenCodeLocationPackager mavenCodeLocationPackager;

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    protected ExecutableRunner executableRunner;

    @Autowired
    protected DetectFileFinder detectFileFinder;

    @Override
    public Extraction extract(final MavenCliContext context) {
        try {
            String mavenCommand = detectConfiguration.getMavenBuildCommand();
            if (StringUtils.isNotBlank(mavenCommand)) {
                mavenCommand = mavenCommand.replace("dependency:tree", "");
                if (StringUtils.isNotBlank(mavenCommand)) {
                    mavenCommand = mavenCommand.trim();
                }
            }

            final List<String> arguments = new ArrayList<>();
            if (StringUtils.isNotBlank(mavenCommand)) {
                arguments.addAll(Arrays.asList(mavenCommand.split(" ")));
            }
            if (StringUtils.isNotBlank(detectConfiguration.getMavenScope())) {
                arguments.add(String.format("-Dscope=%s", detectConfiguration.getMavenScope()));
            }
            arguments.add("dependency:tree");



            final Executable mvnExecutable = new Executable(context.directory, context.mavenExe, arguments);
            final ExecutableOutput mvnOutput = executableRunner.execute(mvnExecutable);

            final String excludedModules = detectConfiguration.getMavenExcludedModuleNames();
            final String includedModules = detectConfiguration.getMavenIncludedModuleNames();
            final List<MavenParseResult> mavenResults = mavenCodeLocationPackager.extractCodeLocations(context.directory.toString(), mvnOutput.getStandardOutput(), excludedModules, includedModules);

            final List<File> additionalTargets = detectFileFinder.findFilesToDepth(context.directory, "target", detectConfiguration.getSearchDepth());
            if (null != additionalTargets && !additionalTargets.isEmpty()) {
                for (final File additionalTarget : additionalTargets) {
                    //hubSignatureScanner.registerPathToScan(ScanPathSource.MAVEN_SOURCE, additionalTarget);
                }
            }

            final List<DetectCodeLocation> codeLocations = mavenResults.stream().map(it -> it.codeLocation).collect(Collectors.toList());

            final Optional<MavenParseResult> firstWithName = mavenResults.stream().filter(it -> StringUtils.isNoneBlank(it.projectName)).findFirst();
            final Extraction.Builder builder = new Extraction.Builder().success(codeLocations);
            if (firstWithName.isPresent()) {
                builder.projectName(firstWithName.get().projectName);
                builder.projectVersion(firstWithName.get().projectVersion);
            }
            return builder.build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
