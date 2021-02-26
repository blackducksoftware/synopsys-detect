/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration.help.json;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.tool.detector.DetectDetectableFactory;
import com.synopsys.integration.detect.tool.detector.DetectorRuleFactory;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class HelpJsonManager {
    private final Gson gson;

    public HelpJsonManager(Gson gson) {
        this.gson = gson;
    }

    public void createHelpJsonDocument(String fileName) throws IllegalAccessException {
        List<HelpJsonDetector> buildDetectors = createHelpJsonDetectorList(false);
        List<HelpJsonDetector> buildlessDetectors = createHelpJsonDetectorList(true);

        HelpJsonWriter helpJsonWriter = new HelpJsonWriter(gson);
        helpJsonWriter.writeGsonDocument(fileName, DetectProperties.allProperties().getProperties(), buildDetectors, buildlessDetectors);
    }

    private List<HelpJsonDetector> createHelpJsonDetectorList(boolean buildless) {
        DetectorRuleFactory ruleFactory = new DetectorRuleFactory();
        // TODO: Is there a better way to build a fake set of rules?
        DetectDetectableFactory mockFactory = new DetectDetectableFactory(null, null, null, null, null, null, null);
        DetectorRuleSet ruleSet = ruleFactory.createRules(mockFactory, buildless);
        return ruleSet.getOrderedDetectorRules()
                   .stream()
                   .map(detectorRule -> convertDetectorRule(detectorRule, ruleSet))
                   .collect(Collectors.toList());
    }

    private HelpJsonDetector convertDetectorRule(DetectorRule rule, DetectorRuleSet ruleSet) {
        HelpJsonDetector helpData = new HelpJsonDetector();
        helpData.setDetectorName(rule.getName());
        helpData.setDetectorDescriptiveName(rule.getDescriptiveName());
        helpData.setDetectorType(rule.getDetectorType().toString());
        helpData.setMaxDepth(rule.getMaxDepth());
        helpData.setNestable(rule.isNestable());
        helpData.setNestInvisible(rule.isNestInvisible());
        helpData.setYieldsTo(ruleSet.getYieldsTo(rule).stream().map(DetectorRule::getDescriptiveName).collect(Collectors.toList()));
        helpData.setFallbackTo(ruleSet.getFallbackFrom(rule).map(DetectorRule::getDescriptiveName).orElse(""));

        //Attempt to create the detectable.
        //Not currently possible. Need a full DetectableConfiguration to be able to make Detectables.
        Class<Detectable> detectableClass = rule.getDetectableClass();
        Optional<DetectableInfo> infoSearch = Arrays.stream(detectableClass.getAnnotations())
                                                  .filter(annotation -> annotation instanceof DetectableInfo)
                                                  .map(annotation -> (DetectableInfo) annotation)
                                                  .findFirst();

        if (infoSearch.isPresent()) {
            DetectableInfo info = infoSearch.get();
            helpData.setDetectableLanguage(info.language());
            helpData.setDetectableRequirementsMarkdown(info.requirementsMarkdown());
            helpData.setDetectableForge(info.forge());
        }

        return helpData;
    }
}
