package com.blackducksoftware.integration.hub.detect.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.manager.BomToolProjectInfo;
import com.blackducksoftware.integration.hub.detect.manager.BomToolProjectInfoDecider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.util.NameVersion;

public class ProjectNameDeciderTests {

    @Test
    public void choosesLowestDepth() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 2;
        add(BomToolType.NPM, lowestDepth, "npm0", "npm0v", possibilities);
        add(BomToolType.MAVEN, lowestDepth + 1, "maven", "npm0v", possibilities);
        add(BomToolType.GRADLE, lowestDepth + 2, "npm1", "npm0v", possibilities);
        add(BomToolType.NPM, lowestDepth + 1, "npm2", "npm0v", possibilities);

        assertProject("npm0", Optional.empty(), possibilities);
    }

    @Test
    public void choosesUniqueOverMultiple() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 2;
        add(BomToolType.NPM, lowestDepth, "npm1", "0", possibilities);
        add(BomToolType.NPM, lowestDepth, "npm2", "0", possibilities);
        add(BomToolType.GRADLE, lowestDepth, "gradle", "0", possibilities);

        assertProject("gradle", Optional.empty(), possibilities);
    }

    @Test
    public void choosesAlphabeticalArbitrary() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 1;
        add(BomToolType.GRADLE, lowestDepth, "f", "0", possibilities);
        add(BomToolType.NPM, lowestDepth, "a", "0", possibilities);
        add(BomToolType.MAVEN, lowestDepth, "b", "0", possibilities);

        assertProject("a", Optional.empty(), possibilities);
    }

    @Test
    public void choosesNoneAmongMultiple() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 1;
        add(BomToolType.NPM, lowestDepth, "f-npm", "0", possibilities);
        add(BomToolType.NPM, lowestDepth, "a-npm", "0", possibilities);
        add(BomToolType.NPM, lowestDepth, "b-npm", "0", possibilities);

        assertNoProject(Optional.empty(), possibilities);
    }

    @Test
    public void choosesPreferredEvenDeeper() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 0;
        add(BomToolType.NPM, lowestDepth, "npm", "0", possibilities);
        add(BomToolType.GRADLE, lowestDepth + 1, "gradle", "0", possibilities);

        assertProject("gradle", Optional.of(BomToolType.GRADLE), possibilities);
    }

    @Test
    public void choosesShallowestPrefferred() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 0;
        add(BomToolType.NPM, lowestDepth, "npm1", "0", possibilities);
        add(BomToolType.NPM, lowestDepth, "npm2", "0", possibilities);
        add(BomToolType.GRADLE, lowestDepth, "c-gradle1", "0", possibilities);
        add(BomToolType.GRADLE, lowestDepth + 1, "a-gradle2", "0", possibilities);
        add(BomToolType.GRADLE, lowestDepth + 1, "b-gradle3", "0", possibilities);

        assertProject("c-gradle1", Optional.of(BomToolType.GRADLE), possibilities);
    }

    @Test
    public void choosesNonWithNoPreferredBomToolFound() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 0;
        add(BomToolType.NPM, lowestDepth, "a", "0", possibilities);
        add(BomToolType.GRADLE, lowestDepth, "b", "0", possibilities);

        assertNoProject(Optional.of(BomToolType.MAVEN), possibilities);
    }

    @Test
    public void choosesNoPreferredBomToolWhenMultipleFound() throws DetectUserFriendlyException {
        final List<BomToolProjectInfo> possibilities = new ArrayList<>();

        final int lowestDepth = 0;
        add(BomToolType.NPM, lowestDepth, "a", "0", possibilities);
        add(BomToolType.GRADLE, lowestDepth, "b", "0", possibilities);
        add(BomToolType.GRADLE, lowestDepth, "b", "0", possibilities);

        assertNoProject(Optional.of(BomToolType.GRADLE), possibilities);
    }

    private void assertProject(final String projectName, final Optional<BomToolType> preferred, final List<BomToolProjectInfo> possibilities) throws DetectUserFriendlyException {
        final BomToolProjectInfoDecider decider = new BomToolProjectInfoDecider();
        final Optional<NameVersion> chosen = decider.decideProjectName(possibilities, preferred);

        Assert.assertTrue(chosen.isPresent());
        Assert.assertEquals(chosen.get().getName(), projectName);
    }

    private void assertNoProject(final Optional<BomToolType> preferred, final List<BomToolProjectInfo> possibilities) throws DetectUserFriendlyException {
        final BomToolProjectInfoDecider decider = new BomToolProjectInfoDecider();
        final Optional<NameVersion> chosen = decider.decideProjectName(possibilities, preferred);

        Assert.assertFalse(chosen.isPresent());
    }

    private void add(final BomToolType type, final int depth, final String projectName, final String projectVersion, final List<BomToolProjectInfo> list) {
        final NameVersion nameVersion = new NameVersion(projectName, projectVersion);
        final BomToolProjectInfo possibility = new BomToolProjectInfo(type, depth, nameVersion);
        list.add(possibility);
    }

}
