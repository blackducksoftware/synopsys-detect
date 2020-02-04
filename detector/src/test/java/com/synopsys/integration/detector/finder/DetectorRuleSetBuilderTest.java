package com.synopsys.integration.detector.finder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        final DetectorRuleSetBuilder ruleSetBuilder = new DetectorRuleSetBuilder();
        ruleSetBuilder.addDetector(DetectorType.GRADLE, "Gradle", GradleDetectable.class, (e) -> null).build();
        ruleSetBuilder.addDetector(DetectorType.NPM, "Npm", NpmCliDetectable.class, (e) -> null).build();
        final DetectorRuleSet ruleSet = ruleSetBuilder.build();

        Assertions.assertEquals(2, ruleSet.getOrderedDetectorRules().size());
        Assertions.assertEquals("Gradle", ruleSet.getOrderedDetectorRules().get(0).getName());
        Assertions.assertEquals("Npm", ruleSet.getOrderedDetectorRules().get(1).getName());
    }

    @Test
    public void testReorderedWithYield() {
        final DetectorRuleSetBuilder ruleSetBuilder = new DetectorRuleSetBuilder();
        final DetectorRule gradle = ruleSetBuilder.addDetector(DetectorType.GRADLE, "Gradle", GradleDetectable.class, (e) -> null).build();
        final DetectorRule npm = ruleSetBuilder.addDetector(DetectorType.NPM, "Npm", NpmCliDetectable.class, (e) -> null).build();
        ruleSetBuilder.yield(gradle).to(npm);
        final DetectorRuleSet ruleSet = ruleSetBuilder.build();

        Assertions.assertEquals(2, ruleSet.getOrderedDetectorRules().size());
        Assertions.assertEquals("Npm", ruleSet.getOrderedDetectorRules().get(0).getName());
        Assertions.assertEquals("Gradle", ruleSet.getOrderedDetectorRules().get(1).getName());
    }

    @Test
    public void testReorderedWithFallback() {
        final DetectorRuleSetBuilder ruleSetBuilder = new DetectorRuleSetBuilder();
        final DetectorRule gradle = ruleSetBuilder.addDetector(DetectorType.GRADLE, "Gradle", GradleDetectable.class, (e) -> null).build();
        final DetectorRule npm = ruleSetBuilder.addDetector(DetectorType.NPM, "Npm", NpmCliDetectable.class, (e) -> null).build();
        ruleSetBuilder.fallback(npm).to(gradle);
        final DetectorRuleSet ruleSet = ruleSetBuilder.build();

        Assertions.assertEquals(2, ruleSet.getOrderedDetectorRules().size());
        Assertions.assertEquals("Npm", ruleSet.getOrderedDetectorRules().get(0).getName());
        Assertions.assertEquals("Gradle", ruleSet.getOrderedDetectorRules().get(1).getName());
    }

    @Test
    public void testMultipleFallbacksThrows() {
        final DetectorRuleSetBuilder ruleSetBuilder = new DetectorRuleSetBuilder();
        final DetectorRule gradle = ruleSetBuilder.addDetector(DetectorType.GRADLE, "Gradle", GradleDetectable.class, (e) -> null).build();
        final DetectorRule maven = ruleSetBuilder.addDetector(DetectorType.MAVEN, "Maven", MavenPomDetectable.class, (e) -> null).build();
        final DetectorRule npm = ruleSetBuilder.addDetector(DetectorType.NPM, "Npm", NpmCliDetectable.class, (e) -> null).build();

        ruleSetBuilder.fallback(npm).to(gradle);
        ruleSetBuilder.fallback(npm).to(maven);

        Assertions.assertThrows(RuntimeException.class, ruleSetBuilder::build);
    }

    @Test
    public void testCircularYieldThrows() {
        final DetectorRuleSetBuilder ruleSetBuilder = new DetectorRuleSetBuilder();
        final DetectorRule gradle = ruleSetBuilder.addDetector(DetectorType.GRADLE, "Gradle", GradleDetectable.class, (e) -> null).build();
        final DetectorRule npm = ruleSetBuilder.addDetector(DetectorType.NPM, "Npm", NpmCliDetectable.class, (e) -> null).build();

        ruleSetBuilder.yield(gradle).to(npm);
        ruleSetBuilder.yield(npm).to(gradle);

        Assertions.assertThrows(RuntimeException.class, ruleSetBuilder::build);
    }
}
