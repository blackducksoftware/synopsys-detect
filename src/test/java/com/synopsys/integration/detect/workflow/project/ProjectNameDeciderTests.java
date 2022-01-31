package com.synopsys.integration.detect.workflow.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.workflow.nameversion.DetectorNameVersionDecider;
import com.synopsys.integration.detect.workflow.nameversion.DetectorProjectInfo;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.util.NameVersion;

public class ProjectNameDeciderTests {

    @Test
    public void choosesLowestDepth() {
        List<DetectorProjectInfo> possibilities = new ArrayList<>();

        int lowestDepth = 2;
        add(DetectorType.NPM, lowestDepth, "npm0", "npm0v", possibilities);
        add(DetectorType.MAVEN, lowestDepth + 1, "maven", "npm0v", possibilities);
        add(DetectorType.GRADLE, lowestDepth + 2, "npm1", "npm0v", possibilities);
        add(DetectorType.NPM, lowestDepth + 1, "npm2", "npm0v", possibilities);

        assertProject("npm0", null, possibilities);
    }

    @Test
    public void choosesUniqueOverMultiple() {
        List<DetectorProjectInfo> possibilities = new ArrayList<>();

        int lowestDepth = 2;
        add(DetectorType.NPM, lowestDepth, "npm1", "0", possibilities);
        add(DetectorType.NPM, lowestDepth, "npm2", "0", possibilities);
        add(DetectorType.GRADLE, lowestDepth, "gradle", "0", possibilities);

        assertProject("gradle", null, possibilities);
    }

    @Test
    public void choosesAlphabeticalArbitrary() {
        List<DetectorProjectInfo> possibilities = new ArrayList<>();

        int lowestDepth = 1;
        add(DetectorType.GRADLE, lowestDepth, "f", "0", possibilities);
        add(DetectorType.NPM, lowestDepth, "a", "0", possibilities);
        add(DetectorType.MAVEN, lowestDepth, "b", "0", possibilities);

        assertProject("a", null, possibilities);
    }

    @Test
    public void choosesNoneAmongMultiple() {
        List<DetectorProjectInfo> possibilities = new ArrayList<>();

        int lowestDepth = 1;
        add(DetectorType.NPM, lowestDepth, "f-npm", "0", possibilities);
        add(DetectorType.NPM, lowestDepth, "a-npm", "0", possibilities);
        add(DetectorType.NPM, lowestDepth, "b-npm", "0", possibilities);

        assertNoProject(null, possibilities);
    }

    @Test
    public void choosesPreferredEvenDeeper() {
        List<DetectorProjectInfo> possibilities = new ArrayList<>();

        int lowestDepth = 0;
        add(DetectorType.NPM, lowestDepth, "npm", "0", possibilities);
        add(DetectorType.GRADLE, lowestDepth + 1, "gradle", "0", possibilities);

        assertProject("gradle", DetectorType.GRADLE, possibilities);
    }

    @Test
    public void choosesShallowestPreferred() {
        List<DetectorProjectInfo> possibilities = new ArrayList<>();

        int lowestDepth = 0;
        add(DetectorType.NPM, lowestDepth, "npm1", "0", possibilities);
        add(DetectorType.NPM, lowestDepth, "npm2", "0", possibilities);
        add(DetectorType.GRADLE, lowestDepth, "c-gradle1", "0", possibilities);
        add(DetectorType.GRADLE, lowestDepth + 1, "a-gradle2", "0", possibilities);
        add(DetectorType.GRADLE, lowestDepth + 1, "b-gradle3", "0", possibilities);

        assertProject("c-gradle1", DetectorType.GRADLE, possibilities);
    }

    @Test
    public void choosesNonWithNoPreferredBomToolFound() {
        List<DetectorProjectInfo> possibilities = new ArrayList<>();

        int lowestDepth = 0;
        add(DetectorType.NPM, lowestDepth, "a", "0", possibilities);
        add(DetectorType.GRADLE, lowestDepth, "b", "0", possibilities);

        assertNoProject(DetectorType.MAVEN, possibilities);
    }

    @Test
    public void choosesNoPreferredBomToolWhenMultipleFound() {
        List<DetectorProjectInfo> possibilities = new ArrayList<>();

        int lowestDepth = 0;
        add(DetectorType.NPM, lowestDepth, "a", "0", possibilities);
        add(DetectorType.GRADLE, lowestDepth, "b", "0", possibilities);
        add(DetectorType.GRADLE, lowestDepth, "b", "0", possibilities);

        assertNoProject(DetectorType.GRADLE, possibilities);
    }

    private void assertProject(String projectName, DetectorType preferred, List<DetectorProjectInfo> possibilities) {
        DetectorNameVersionDecider decider = new DetectorNameVersionDecider();
        Optional<NameVersion> chosen = decider.decideProjectNameVersion(possibilities, preferred);

        Assertions.assertTrue(chosen.isPresent());
        Assertions.assertEquals(chosen.get().getName(), projectName);
    }

    private void assertNoProject(DetectorType preferred, List<DetectorProjectInfo> possibilities) {
        DetectorNameVersionDecider decider = new DetectorNameVersionDecider();
        Optional<NameVersion> chosen = decider.decideProjectNameVersion(possibilities, preferred);

        Assertions.assertFalse(chosen.isPresent());
    }

    private void add(DetectorType type, int depth, String projectName, String projectVersion, List<DetectorProjectInfo> list) {
        NameVersion nameVersion = new NameVersion(projectName, projectVersion);
        DetectorProjectInfo possibility = new DetectorProjectInfo(type, depth, nameVersion);
        list.add(possibility);
    }

}
