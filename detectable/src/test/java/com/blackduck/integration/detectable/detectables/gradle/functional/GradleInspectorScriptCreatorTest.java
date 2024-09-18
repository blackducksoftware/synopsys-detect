package com.blackduck.integration.detectable.detectables.gradle.functional;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.blackduck.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptCreator;
import com.blackduck.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptOptions;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.blackduck.integration.detectable.detectable.exception.DetectableException;

import freemarker.template.Configuration;

public class GradleInspectorScriptCreatorTest {

    @TempDir
    public File tempDir;

    @Test
    void testOffline() throws DetectableException, IOException {
        List<String> excludedProjectNames = Arrays.asList("excludedProject");
        List<String> includedProjectNames = Arrays.asList("includedProject");
        List<String> excludedConfigurationNames = Arrays.asList("excludedConfig");
        List<String> includedConfigurationNames = Arrays.asList("excludedConfig");
        String gradleInspectorRepositoryUrl = null;
        GradleInspectorScriptOptions scriptOptions = new GradleInspectorScriptOptions(
            excludedProjectNames,
            includedProjectNames,
            Collections.emptyList(),
            Collections.emptyList(),
            excludedConfigurationNames,
            includedConfigurationNames,
            gradleInspectorRepositoryUrl
        );

        Configuration configuration = createFreemarkerConfiguration();
        GradleInspectorScriptCreator gradleInspectorScriptCreator = new GradleInspectorScriptCreator(configuration);
        File targetFile = new File(tempDir, "init-detect.gradle");

        String airGapLibraryPaths = "airGap/library/dir";
        gradleInspectorScriptCreator.createOfflineGradleInspector(targetFile, scriptOptions, airGapLibraryPaths);

        String generatedScriptContent = FileUtils.readFileToString(targetFile, StandardCharsets.UTF_8);

        assertTrue(generatedScriptContent.contains("dirs 'airGap/library/dir'"));
        assertTrue(generatedScriptContent.contains("File('airGap/library/dir').eachFile"));
    }

    private Configuration createFreemarkerConfiguration() throws IOException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_26);
        configuration.setDirectoryForTemplateLoading(new File("../src/main/resources"));
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLogTemplateExceptions(true);

        return configuration;
    }
}
