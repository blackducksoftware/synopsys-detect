package com.synopsys.integration.detector.rule.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DetectorRuleSetBuilder {
    private final List<DetectorRuleBuilder> builders = new ArrayList<>();
    private final Object factory;

    public DetectorRuleSetBuilder(Object factory) {this.factory = factory;}

    public DetectorRuleBuilder addDetector(DetectorType detectorType, DetectorRuleBuilderDelegate creator) {
        DetectorRuleBuilder builder = new DetectorRuleBuilder(detectorType, new DetectableLookup(factory));
        creator.build(builder);
        builders.add(builder);
        return builder;
    }

    public <T extends Detectable> DetectorRuleBuilder addDetector(DetectorType detectorType, Class<T> detectableClass) {
        DetectorRuleBuilder builder = new DetectorRuleBuilder(detectorType, new DetectableLookup(factory));
        builder.entryPoint(detectableClass);
        builders.add(builder);
        return builder;
    }

    public DetectorRuleSet build() {
        List<DetectorRule> rules = builders.stream()
            .map(DetectorRuleBuilder::build)
            .collect(Collectors.toList());

        return new DetectorRuleSet(rules);
    }
}
