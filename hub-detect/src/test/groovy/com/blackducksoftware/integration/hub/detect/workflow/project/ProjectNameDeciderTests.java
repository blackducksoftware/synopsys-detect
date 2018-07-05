package com.blackducksoftware.integration.hub.detect.workflow.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.workflow.project.BomToolNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.project.BomToolProjectInfo;
import com.blackducksoftware.integration.util.NameVersion;

public class ProjectNameDeciderTests {

    @Test
    public void choosesLowestDepth() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 2;
        add(BomToolGroupType.NPM, lowestDepth, "npm0", "npm0v", possibilities);
        add(BomToolGroupType.MAVEN, lowestDepth + 1, "maven", "npm0v", possibilities);
        add(BomToolGroupType.GRADLE, lowestDepth + 2, "npm1", "npm0v", possibilities);
        add(BomToolGroupType.NPM, lowestDepth + 1, "npm2", "npm0v", possibilities);

        assertProject("npm0", null, possibilities);
    }

    @Test
    public void choosesUniqueOverMultiple() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 2;
        add(BomToolGroupType.NPM, lowestDepth, "npm1", "0", possibilities);
        add(BomToolGroupType.NPM, lowestDepth, "npm2", "0", possibilities);
        add(BomToolGroupType.GRADLE, lowestDepth, "gradle", "0", possibilities);

        assertProject("gradle", null, possibilities);
    }

    @Test
    public void choosesAlphabeticalArbitrary() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 1;
        add(BomToolGroupType.GRADLE, lowestDepth, "f", "0", possibilities);
        add(BomToolGroupType.NPM, lowestDepth, "a", "0", possibilities);
        add(BomToolGroupType.MAVEN, lowestDepth, "b", "0", possibilities);

        assertProject("a", null, possibilities);
    }

    @Test
    public void choosesNoneAmongMultiple() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 1;
        add(BomToolGroupType.NPM, lowestDepth, "f-npm", "0", possibilities);
        add(BomToolGroupType.NPM, lowestDepth, "a-npm", "0", possibilities);
        add(BomToolGroupType.NPM, lowestDepth, "b-npm", "0", possibilities);

        assertNoProject(null, possibilities);
    }

    @Test
    public void choosesPreferredEvenDeeper() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 0;
        add(BomToolGroupType.NPM, lowestDepth, "npm", "0", possibilities);
        add(BomToolGroupType.GRADLE, lowestDepth + 1, "gradle", "0", possibilities);

        assertProject("gradle", BomToolGroupType.GRADLE, possibilities);
    }

    @Test
    public void choosesShallowestPrefferred() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 0;
        add(BomToolGroupType.NPM, lowestDepth, "npm1", "0", possibilities);
        add(BomToolGroupType.NPM, lowestDepth, "npm2", "0", possibilities);
        add(BomToolGroupType.GRADLE, lowestDepth, "c-gradle1", "0", possibilities);
        add(BomToolGroupType.GRADLE, lowestDepth + 1, "a-gradle2", "0", possibilities);
        add(BomToolGroupType.GRADLE, lowestDepth + 1, "b-gradle3", "0", possibilities);

        assertProject("c-gradle1", BomToolGroupType.GRADLE, possibilities);
    }

    @Test
    public void choosesNonWithNoPreferredBomToolFound() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 0;
        add(BomToolGroupType.NPM, lowestDepth, "a", "0", possibilities);
        add(BomToolGroupType.GRADLE, lowestDepth, "b", "0", possibilities);

        assertNoProject(BomToolGroupType.MAVEN, possibilities);
    }

    @Test
    public void choosesNoPreferredBomToolWhenMultipleFound() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 0;
        add(BomToolGroupType.NPM, lowestDepth, "a", "0", possibilities);
        add(BomToolGroupType.GRADLE, lowestDepth, "b", "0", possibilities);
        add(BomToolGroupType.GRADLE, lowestDepth, "b", "0", possibilities);

        assertNoProject(BomToolGroupType.GRADLE, possibilities);
    }

    private void assertProject(final String projectName, final BomToolGroupType preferred, final List<BomToolProjectInfo> possibilities) throws DetectUserFriendlyException {
        final BomToolNameVersionDecider decider = new BomToolNameVersionDecider();
        final Optional<NameVersion> chosen = decider.decideProjectNameVersion(possibilities, preferred);

        Assert.assertTrue(chosen.isPresent());
        Assert.assertEquals(chosen.get().getName(), projectName);
    }

    private void assertNoProject(final BomToolGroupType preferred, final List<BomToolProjectInfo> possibilities) throws DetectUserFriendlyException {
        final BomToolNameVersionDecider decider = new BomToolNameVersionDecider();
        final Optional<NameVersion> chosen = decider.decideProjectNameVersion(possibilities, preferred);

        Assert.assertFalse(chosen.isPresent());
    }

    private void add(final BomToolGroupType type, final int depth, final String projectName, final String projectVersion, final List<BomToolProjectInfo> list) {
        final NameVersion nameVersion = new NameVersion(projectName, projectVersion);
        final BomToolProjectInfo possibility = new BomToolProjectInfo(type, depth, nameVersion);
        list.add(possibility);
    }

}
