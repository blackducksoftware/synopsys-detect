package com.blackducksoftware.integration.hub.detect.bomtool.packagist

import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.DetectProperties
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class PackagistTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void packagistParserTest() throws IOException {
        DetectProperties detectProperties = new DetectProperties()
        detectProperties.packagistIncludeDevDependencies = true
        DetectConfiguration detectConfiguration = new DetectConfiguration()
        detectConfiguration.detectProperties = detectProperties

        PackagistParser packagistParser = new PackagistParser()
        packagistParser.detectConfiguration = detectConfiguration

        def composerLockFile = new File(getClass().getResource('/packagist/composer.lock').getFile())
        def composerJsonFile = new File(getClass().getResource('/packagist/composer.json').getFile())
        DependencyNode dependencyNode = packagistParser.getDependencyNodeFromProject(composerJsonFile, composerLockFile)
        final String actual = gson.toJson(dependencyNode);

        String expectedText = new File(getClass().getResource("/packagist/PackagistTestDependencyNode.txt").getFile()).text

        JSONAssert.assertEquals(expectedText, actual, false);
    }
}
