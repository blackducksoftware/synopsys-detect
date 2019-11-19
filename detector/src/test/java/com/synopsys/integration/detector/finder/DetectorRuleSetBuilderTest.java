package com.synopsys.integration.detector.finder;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleDetectable;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenPomDetectable;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliDetectable;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.detector.rule.DetectorRuleSetBuilder;

public class DetectorRuleSetBuilderTest {
    @Test
    public void testOrderGivenUsed() {
        DetectorRuleSetBuilder ruleSetBuilder = new DetectorRuleSetBuilder();
        ruleSetBuilder.addDetector(DetectorType.GRADLE, "Gradle", GradleDetectable.class, (e) -> null).build();
        ruleSetBuilder.addDetector(DetectorType.NPM, "Npm", NpmCliDetectable.class, (e) -> null).build();
        DetectorRuleSet ruleSet = ruleSetBuilder.build();

        Assert.assertEquals(2, ruleSet.getOrderedDetectorRules().size());
        Assert.assertEquals("Gradle", ruleSet.getOrderedDetectorRules().get(0).getName());
        Assert.assertEquals("Npm", ruleSet.getOrderedDetectorRules().get(1).getName());
    }

    @Test
    public void testReorderedWithYield() {
        DetectorRuleSetBuilder ruleSetBuilder = new DetectorRuleSetBuilder();
        DetectorRule gradle = ruleSetBuilder.addDetector(DetectorType.GRADLE, "Gradle",GradleDetectable.class,  (e) -> null).build();
        DetectorRule npm = ruleSetBuilder.addDetector(DetectorType.NPM, "Npm", NpmCliDetectable.class, (e) -> null).build();
        ruleSetBuilder.yield(gradle).to(npm);
        DetectorRuleSet ruleSet = ruleSetBuilder.build();

        Assert.assertEquals(2, ruleSet.getOrderedDetectorRules().size());
        Assert.assertEquals("Npm", ruleSet.getOrderedDetectorRules().get(0).getName());
        Assert.assertEquals("Gradle", ruleSet.getOrderedDetectorRules().get(1).getName());
    }

    @Test
    public void testReorderedWithFallback() {
        DetectorRuleSetBuilder ruleSetBuilder = new DetectorRuleSetBuilder();
        DetectorRule gradle = ruleSetBuilder.addDetector(DetectorType.GRADLE, "Gradle",GradleDetectable.class,  (e) -> null).build();
        DetectorRule npm = ruleSetBuilder.addDetector(DetectorType.NPM, "Npm", NpmCliDetectable.class, (e) -> null).build();
        ruleSetBuilder.fallback(npm).to(gradle);
        DetectorRuleSet ruleSet = ruleSetBuilder.build();

        Assert.assertEquals(2, ruleSet.getOrderedDetectorRules().size());
        Assert.assertEquals("Npm", ruleSet.getOrderedDetectorRules().get(0).getName());
        Assert.assertEquals("Gradle", ruleSet.getOrderedDetectorRules().get(1).getName());
    }

    @Test(expected = RuntimeException.class)
    public void testMultipleFallbacksThrows() {
        DetectorRuleSetBuilder ruleSetBuilder = new DetectorRuleSetBuilder();
        DetectorRule gradle = ruleSetBuilder.addDetector(DetectorType.GRADLE, "Gradle", GradleDetectable.class, (e) -> null).build();
        DetectorRule maven = ruleSetBuilder.addDetector(DetectorType.MAVEN, "Maven", MavenPomDetectable.class, (e) -> null).build();
        DetectorRule npm = ruleSetBuilder.addDetector(DetectorType.NPM, "Npm", NpmCliDetectable.class, (e) -> null).build();

        ruleSetBuilder.fallback(npm).to(gradle);
        ruleSetBuilder.fallback(npm).to(maven);

        ruleSetBuilder.build();
    }

    @Test(expected = RuntimeException.class)
    public void testCircularYieldThrows() {
        DetectorRuleSetBuilder ruleSetBuilder = new DetectorRuleSetBuilder();
        DetectorRule gradle = ruleSetBuilder.addDetector(DetectorType.GRADLE, "Gradle", GradleDetectable.class, (e) -> null).build();
        DetectorRule npm = ruleSetBuilder.addDetector(DetectorType.NPM, "Npm", NpmCliDetectable.class, (e) -> null).build();

        ruleSetBuilder.yield(gradle).to(npm);
        ruleSetBuilder.yield(npm).to(gradle);

        ruleSetBuilder.build();
    }
}
