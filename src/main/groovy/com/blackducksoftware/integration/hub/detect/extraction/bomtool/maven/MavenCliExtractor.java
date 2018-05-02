package com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenCodeLocationPackager;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
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
    protected DetectFileManager detectFileManager;

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

            List<DetectCodeLocation> codeLocations = null;


            final Executable mvnExecutable = new Executable(context.directory, context.rebarExe, arguments);
            final ExecutableOutput mvnOutput = executableRunner.execute(mvnExecutable);

            final String excludedModules = detectConfiguration.getMavenExcludedModuleNames();
            final String includedModules = detectConfiguration.getMavenIncludedModuleNames();
            codeLocations = mavenCodeLocationPackager.extractCodeLocations(context.directory.toString(), mvnOutput.getStandardOutput(), excludedModules, includedModules);

            final List<File> additionalTargets = detectFileManager.findFilesToDepth(context.directory, "target", detectConfiguration.getSearchDepth());
            if (null != additionalTargets && !additionalTargets.isEmpty()) {
                for (final File additionalTarget : additionalTargets) {
                    //hubSignatureScanner.registerPathToScan(ScanPathSource.MAVEN_SOURCE, additionalTarget);
                }
            }

            return new Extraction(ExtractionResult.Success, codeLocations);
        } catch (final Exception e) {
            return new Extraction(ExtractionResult.Failure, e);
        }
    }

}
