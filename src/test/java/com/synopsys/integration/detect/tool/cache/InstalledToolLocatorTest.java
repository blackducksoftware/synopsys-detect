package com.synopsys.integration.detect.tool.cache;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

public class InstalledToolLocatorTest {
    @Test
    public void testInstallDummyTool() throws URISyntaxException {
        URI pathToToolCacheFileUri = this.getClass().getClassLoader().getResource("tool/cache/detect-installed-tools.json").toURI();
        Path pathToToolCacheFile = Paths.get(pathToToolCacheFileUri);
        InstalledToolLocator cachedToolInstaller = new InstalledToolLocator(pathToToolCacheFile, new Gson());
        Assertions.assertTrue(cachedToolInstaller.locateTool("docker-inspector").isPresent());
    }
}
