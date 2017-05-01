package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import java.nio.charset.StandardCharsets

import org.apache.commons.lang3.StringUtils
import org.junit.Assert
import org.junit.Test

class ProjectsParserTest {
    @Test
    public void testExtractingSubprojects() {
        InputStream projectsInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream('gradle/hub-rest-backend-projects')
        String projectsContent = projectsInputStream.getText(StandardCharsets.UTF_8.name())

        InputStream expectedInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream('gradle/hub-rest-backend-projects-expected')
        String expectedContent = expectedInputStream.getText(StandardCharsets.UTF_8.name())
        List<String> expected = expectedContent.split('\n').collect { StringUtils.trimToEmpty(it) }.findAll { StringUtils.isNotBlank(it) }

        ProjectsParser projectsParser = new ProjectsParser()
        List<String> actual = projectsParser.extractSubProjectNames(projectsContent)

        Assert.assertEquals(expected, actual)
    }

    @Test
    public void testExtractingSubprojectsWhenThereAreNone() {
        InputStream projectsInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream('gradle/hub-artifactory-projects')
        String projectsContent = projectsInputStream.getText(StandardCharsets.UTF_8.name())

        ProjectsParser projectsParser = new ProjectsParser()
        List<String> actual = projectsParser.extractSubProjectNames(projectsContent)

        Assert.assertTrue(actual.size() == 0)
    }
}