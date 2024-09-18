package com.blackduck.integration.detect;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import com.blackduck.integration.detect.configuration.DetectConfigurationFactory;
import com.blackduck.integration.detect.configuration.DetectPropertyConfiguration;
import com.blackduck.integration.detect.configuration.connection.ConnectionFactory;
import com.blackduck.integration.detect.tool.cache.InstalledToolLocator;
import com.blackduck.integration.detect.tool.cache.InstalledToolManager;
import com.blackduck.integration.detect.workflow.ArtifactResolver;
import com.blackduck.integration.detect.workflow.DetectRunId;
import com.blackduck.integration.detect.workflow.blackduck.DetectFontLoader;
import com.blackduck.integration.detect.workflow.blackduck.font.DetectFontInstaller;
import com.blackduck.integration.detect.workflow.blackduck.font.DetectFontLocator;
import com.blackduck.integration.detect.workflow.blackduck.font.OnlineDetectFontLocator;
import com.blackduck.integration.detect.workflow.file.DirectoryManager;
import com.blackduck.integration.detect.workflow.file.DirectoryOptions;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.blackduck.integration.configuration.config.PropertyConfiguration;
import com.blackduck.integration.configuration.property.types.path.SimplePathResolver;

@Tag("integration")
public class FontLoaderTestIT {
    private File fontDirectory;
    private DetectFontLocator detectFontLocator;

    @BeforeEach
    public void createTempDirectory() throws Exception {
        fontDirectory = Files.createTempDirectory("junit_test_font_loader").toFile();
        PropertyConfiguration propertyConfiguration = new PropertyConfiguration(Collections.emptyList(), Collections.emptySortedMap());
        Gson gson = new Gson();
        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(
            new DetectPropertyConfiguration(propertyConfiguration, new SimplePathResolver()),
            gson
        );
        ConnectionFactory connectionFactory = new ConnectionFactory(detectConfigurationFactory.createConnectionDetails());
        ArtifactResolver artifactResolver = new ArtifactResolver(connectionFactory, gson);
        InstalledToolManager installedToolManager = new InstalledToolManager();
        InstalledToolLocator installedToolLocator = new InstalledToolLocator(Paths.get(""), new Gson());
        DetectFontInstaller installer = new DetectFontInstaller(artifactResolver, installedToolManager, installedToolLocator);
        DirectoryOptions directoryOptions = new DirectoryOptions(null, null, null, null, fontDirectory.toPath(), null, null);
        DirectoryManager directoryManager = new DirectoryManager(directoryOptions, DetectRunId.createDefault());
        detectFontLocator = new OnlineDetectFontLocator(installer, directoryManager);
    }

    @AfterEach
    public void cleanTempDirectory() {
        FileUtils.deleteQuietly(fontDirectory);
    }

    @Test
    public void loadsCJKFont() {
        DetectFontLoader fontLoader = new DetectFontLoader(detectFontLocator);
        PDFont font = fontLoader.loadFont(new PDDocument());
        Assertions.assertTrue(font.getName().contains("CJK"));
    }

    @Test
    public void loadsCJKBoldFont() {
        DetectFontLoader fontLoader = new DetectFontLoader(detectFontLocator);
        PDFont font = fontLoader.loadBoldFont(new PDDocument());
        Assertions.assertTrue(font.getName().contains("CJK"));
    }

    @Test
    public void installOnceTestFont() {
        DetectFontLoader fontLoader = new DetectFontLoader(detectFontLocator);
        PDFont regularFont = fontLoader.loadFont(new PDDocument());
        PDFont boldFont = fontLoader.loadBoldFont(new PDDocument());
        Assertions.assertTrue(regularFont.getName().contains("CJK"));
        Assertions.assertTrue(boldFont.getName().contains("CJK"));
    }
}
