package com.synopsys.integration.detect.workflow.blackduck.font;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.types.path.SimplePathResolver;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectPropertyConfiguration;
import com.synopsys.integration.detect.configuration.connection.ConnectionFactory;
import com.synopsys.integration.detect.tool.cache.InstalledToolLocator;
import com.synopsys.integration.detect.tool.cache.InstalledToolManager;
import com.synopsys.integration.detect.workflow.ArtifactResolver;

@Tag("integration")
public class DetectFontInstallerTestIT {
    private File targetDirectory;

    @BeforeEach
    public void createTempDirectory() throws Exception {
        targetDirectory = Files.createTempDirectory("junit_font_installer_test").toFile();
    }

    @AfterEach
    public void cleanTempDirectory() {
        FileUtils.deleteQuietly(targetDirectory);
    }

    @Test
    public void testFontInstall() throws Exception {
        Gson gson = new Gson();
        PropertyConfiguration propertyConfiguration = new PropertyConfiguration(Collections.emptyList());
        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(
            new DetectPropertyConfiguration(propertyConfiguration, new SimplePathResolver()),
            gson
        );
        ConnectionFactory connectionFactory = new ConnectionFactory(detectConfigurationFactory.createConnectionDetails());
        ArtifactResolver artifactResolver = new ArtifactResolver(connectionFactory, gson);
        InstalledToolManager installedToolManager = new InstalledToolManager();
        InstalledToolLocator installedToolLocator = new InstalledToolLocator(Paths.get(""), new Gson());
        DetectFontInstaller installer = new DetectFontInstaller(artifactResolver, installedToolManager, installedToolLocator);
        installer.installFonts(targetDirectory);
        String[] fileList = targetDirectory.list();
        assertNotNull(fileList);
        assertTrue(fileList.length > 0);
    }
}
