package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import java.nio.charset.StandardCharsets

import org.junit.Assert
import org.junit.Test

import com.blackducksoftware.integration.util.ExcludedIncludedFilter

class ProjectsParserTest {
    static final ExcludedIncludedFilter FILTER_NOTHING = new ExcludedIncludedFilter("", "")

    @Test
    public void testExtractingSubProjectsSimple() {
        InputStream projectsInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream('gradle/multiple-projects-simple-projects')
        String projectsContent = projectsInputStream.getText(StandardCharsets.UTF_8.name())

        ProjectsParser projectsParser = new ProjectsParser()
        GradleProjectName rootProjectName = new GradleProjectName()
        rootProjectName.name = 'root'
        Assert.assertEquals(0, rootProjectName.children.size())
        projectsParser.populateWithSubProjects(rootProjectName, projectsContent, FILTER_NOTHING)
        Assert.assertEquals(2, rootProjectName.children.size())
    }

    @Test
    public void testExtractingSubProjectsSimpleWithFiltering() {
        InputStream projectsInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream('gradle/multiple-projects-simple-projects')
        String projectsContent = projectsInputStream.getText(StandardCharsets.UTF_8.name())

        ProjectsParser projectsParser = new ProjectsParser()
        GradleProjectName rootProjectName = new GradleProjectName()
        rootProjectName.name = 'root'
        Assert.assertEquals(0, rootProjectName.children.size())
        projectsParser.populateWithSubProjects(rootProjectName, projectsContent, new ExcludedIncludedFilter("krill", null))
        Assert.assertEquals(1, rootProjectName.children.size())
    }

    @Test
    public void testExtractingSubProjectsComplex() {
        InputStream projectsInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream('gradle/hub-rest-backend-projects')
        String projectsContent = projectsInputStream.getText(StandardCharsets.UTF_8.name())

        ProjectsParser projectsParser = new ProjectsParser()
        GradleProjectName rootProjectName = new GradleProjectName()
        rootProjectName.name = 'root'
        Assert.assertEquals(0, rootProjectName.children.size())
        projectsParser.populateWithSubProjects(rootProjectName, projectsContent, FILTER_NOTHING)
        Assert.assertEquals(31, rootProjectName.children.size())

        InputStream expectedInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream('gradle/hub-rest-backend-projects-expected')
        String expected = expectedInputStream.getText(StandardCharsets.UTF_8.name()).trim()

        StringBuilder actualBuilder = new StringBuilder()
        buildTestString(actualBuilder, 0, rootProjectName)
        String actual = actualBuilder.toString().trim()

        Assert.assertEquals(expected, actual)
    }

    @Test
    public void testExtractingSubprojectsWhenThereAreNone() {
        InputStream projectsInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream('gradle/hub-artifactory-projects')
        String projectsContent = projectsInputStream.getText(StandardCharsets.UTF_8.name())

        ProjectsParser projectsParser = new ProjectsParser()
        GradleProjectName rootProjectName = new GradleProjectName()
        rootProjectName.name = 'root'
        Assert.assertEquals(0, rootProjectName.children.size())
        projectsParser.populateWithSubProjects(rootProjectName, projectsContent, FILTER_NOTHING)
        Assert.assertEquals(0, rootProjectName.children.size())
    }

    @Test
    public void parsingProjectName() {
        ProjectsParser projectsParser = new ProjectsParser()
        Assert.assertEquals(':scan:scan.cli.wrapper', projectsParser.parseProjectNameFromOutputLine("|    +--- Project ':scan:scan.cli.wrapper' - Hub").name)
        Assert.assertEquals(':doc:doc.web', projectsParser.parseProjectNameFromOutputLine("|    \\--- Project ':doc:doc.web' - Hub documentation web application").name)
        Assert.assertEquals(':activity', projectsParser.parseProjectNameFromOutputLine("+--- Project ':activity' - Hub").name)
        Assert.assertEquals(':docker:blackducksoftware:hub-tomcat', projectsParser.parseProjectNameFromOutputLine("|    |    +--- Project ':docker:blackducksoftware:hub-tomcat' - Hub").name)
    }

    void buildTestString(StringBuilder stringBuilder, int currentLevel, GradleProjectName gradleProjectName) {
        String prefix = '    '.multiply(currentLevel)
        stringBuilder.append(prefix + gradleProjectName.name + '\n')
        gradleProjectName.children.each {
            buildTestString(stringBuilder, currentLevel + 1, it)
        }
    }
}