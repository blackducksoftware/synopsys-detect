package com.synopsys.integration.detect.configuration.help.json;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.help.json.model.HelpJsonDetectable;
import com.synopsys.integration.detect.configuration.help.json.model.HelpJsonDetector;
import com.synopsys.integration.detect.configuration.help.json.model.HelpJsonDetectorEntryPoint;
import com.synopsys.integration.detect.configuration.help.json.model.HelpJsonDetectorRule;
import com.synopsys.integration.detect.tool.detector.DetectorRuleFactory;
import com.synopsys.integration.detect.tool.detector.factory.DetectDetectableFactory;
import com.synopsys.integration.detector.rule.DetectableDefinition;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.detector.rule.EntryPoint;

public class HelpJsonManager {
    private final Gson gson;

    public HelpJsonManager(Gson gson) {
        this.gson = gson;
    }

    public void createHelpJsonDocument(String fileName) throws IllegalAccessException {
        List<HelpJsonDetectorRule> detectors = createHelpJsonDetectors();

        List<HelpJsonDetector> buildDetectors = createHelpJsonDetectorList(false);
        List<HelpJsonDetector> buildlessDetectors = createHelpJsonDetectorList(true);

        HelpJsonWriter helpJsonWriter = new HelpJsonWriter(gson);
        helpJsonWriter.writeGsonDocument(fileName, DetectProperties.allProperties().getProperties(), buildDetectors, buildlessDetectors, detectors);
    }

    private List<HelpJsonDetectorRule> createHelpJsonDetectors() {
        DetectorRuleFactory ruleFactory = new DetectorRuleFactory();
        DetectDetectableFactory mockFactory = new DetectDetectableFactory(null, null, null, null, null, null, null, null);
        DetectorRuleSet ruleSet = ruleFactory.createRules(mockFactory);

        return ruleSet.getDetectorRules().stream()
            .map(this::convertDetector)
            .collect(Collectors.toList());
    }

    private HelpJsonDetectorRule convertDetector(DetectorRule detector) {
        HelpJsonDetectorRule helpData = new HelpJsonDetectorRule();
        helpData.setDetectorType(detector.getDetectorType().toString());
        helpData.setMaxDepth(detector.getMaxDepth());
        helpData.setNestable(detector.isNestable());
        helpData.setNestInvisible(detector.isNestInvisible());
        helpData.setYieldsTo(detector.getYieldsTo().stream().map(Object::toString).collect(Collectors.toList()));

        List<HelpJsonDetectorEntryPoint> entryPoints = detector.getEntryPoints().stream()
            .map(this::convertEntryPoint)
            .collect(Collectors.toList());

        return helpData;
    }

    private HelpJsonDetectorEntryPoint convertEntryPoint(EntryPoint entryPoint) {
        HelpJsonDetectorEntryPoint entryPointData = new HelpJsonDetectorEntryPoint();
        entryPointData.setName(entryPoint.getPrimary().getName());

        List<HelpJsonDetectable> detectables = entryPoint.allDetectables().stream()
            .map(this::convertDetectable)
            .collect(Collectors.toList());

        entryPointData.setDetectables(detectables);

        return entryPointData;
    }

    private HelpJsonDetectable convertDetectable(DetectableDefinition detectableDefinition) {
        HelpJsonDetectable detectableData = new HelpJsonDetectable();
        detectableData.setDetectableName(detectableDefinition.getName());
        detectableData.setDetectableLanguage(detectableDefinition.getLanguage());
        detectableData.setDetectableRequirementsMarkdown(detectableDefinition.getRequirementsMarkdown());
        detectableData.setDetectableForge(detectableDefinition.getForge());
        return detectableData;
    }

    private List<HelpJsonDetector> createHelpJsonDetectorList(boolean buildless) {
        DetectorRuleFactory ruleFactory = new DetectorRuleFactory();
        // TODO: Is there a better way to build a fake set of rules?
        DetectDetectableFactory mockFactory = new DetectDetectableFactory(null, null, null, null, null, null, null, null);

        DetectorRuleSet ruleSet = ruleFactory.createRules(mockFactory);
        return ruleSet.getDetectorRules().stream()
            .flatMap(detectorRule ->
                detectorRule.getEntryPoints().stream()
                    .flatMap(entryPoint -> entryPoint.allDetectables().stream())
                    .map(detectable -> convertDetectorRule(detectorRule, detectable, ruleSet))
            ).collect(Collectors.toList());
    }

    private HelpJsonDetector convertDetectorRule(DetectorRule detector, DetectableDefinition detectable, DetectorRuleSet ruleSet) {
        HelpJsonDetector helpData = new HelpJsonDetector();
        helpData.setDetectorName(detectable.getName());
        //TODO: This may also need revamp.
        helpData.setDetectorDescriptiveName(detector.getDetectorType() + " - " + detectable.getName());

        helpData.setDetectorType(detector.getDetectorType().toString());
        helpData.setMaxDepth(detector.getMaxDepth());
        helpData.setNestable(detector.isNestable());
        helpData.setNestInvisible(detector.isNestInvisible());
        //TODO: This needs work...
        //helpData.setYieldsTo(ruleSet.getYieldsTo(rule).stream().map(DetectorRule::getDescriptiveName).collect(Collectors.toList()));

        helpData.setDetectableLanguage(detectable.getLanguage());
        helpData.setDetectableRequirementsMarkdown(detectable.getRequirementsMarkdown());
        helpData.setDetectableForge(detectable.getForge());
        return helpData;
    }
}
