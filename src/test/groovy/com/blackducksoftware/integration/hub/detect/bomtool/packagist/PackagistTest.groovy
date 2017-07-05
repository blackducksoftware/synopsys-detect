package com.blackducksoftware.integration.hub.detect.bomtool.packagist

import static org.junit.Assert.assertTrue

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.Application
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.util.FileFinder
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest
class PackagistTest {

    @Autowired
    FileFinder fileFinder

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Autowired
    DetectConfiguration detectConfiguration

    @Test
    public void packagistParserTest() throws IOException {
        PackagistParser testParser = new PackagistParser()

        testParser.fileFinder = fileFinder
        testParser.detectConfiguration = detectConfiguration

        String location = getClass().getResource("/packagist/").getFile()
        def composerLockFile = new File("${location}${File.separator}composer.lock")
        def composerJsonFile = new File("${location}${File.separator}composer.json")
        DependencyNode actual = testParser.getDependencyNodeFromProject(composerJsonFile, composerLockFile)

        File expected = new File(getClass().getResource("/packagist/PackagistTestDependencyNode.txt").getFile())

        assertTrue(actual.toString().contentEquals(expected.text))
    }
}
