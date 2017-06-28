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

    @Test
    public void packagistParserTest() throws IOException {
        PackagistParser testParser = new PackagistParser()

        testParser.fileFinder = fileFinder
        testParser.projectInfoGatherer = projectInfoGatherer

        String location = getClass().getResource("/packagist/").getFile()
        DependencyNode actual = testParser.getDependencyNodeFromProject(location)

        File expected = new File(getClass().getResource("/packagist/PackagistTestDependencyNode.txt").getFile())

        assertTrue(actual.toString().contentEquals(expected.text))
    }
}
