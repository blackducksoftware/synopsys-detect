package com.synopsys.integration.detect.configuration.help.json;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.help.json.model.HelpJsonDetectable;
import com.synopsys.integration.detect.configuration.help.json.model.HelpJsonDetectorEntryPoint;
import com.synopsys.integration.detect.configuration.help.json.model.HelpJsonDetectorRule;
import com.synopsys.integration.detect.configuration.help.json.model.HelpJsonSearchRule;
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

        HelpJsonWriter helpJsonWriter = new HelpJsonWriter(gson);
        helpJsonWriter.writeGsonDocument(fileName, DetectProperties.allProperties().getProperties(), detectors);
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

        List<HelpJsonDetectorEntryPoint> entryPoints = detector.getEntryPoints().stream()
            .map(this::convertEntryPoint)
            .collect(Collectors.toList());
        helpData.setEntryPoints(entryPoints);

        return helpData;
    }

    private HelpJsonDetectorEntryPoint convertEntryPoint(EntryPoint entryPoint) {
        HelpJsonDetectorEntryPoint entryPointData = new HelpJsonDetectorEntryPoint();
        entryPointData.setName(entryPoint.getPrimary().getName());

        List<HelpJsonDetectable> detectables = entryPoint.allDetectables().stream()
            .map(this::convertDetectable)
            .collect(Collectors.toList());

        entryPointData.setDetectables(detectables);

        HelpJsonSearchRule searchRule = new HelpJsonSearchRule();
        searchRule.setMaxDepth(entryPoint.getSearchRule().getMaxDepth());
        searchRule.setNestable(entryPoint.getSearchRule().isNestable());
        searchRule.setYieldsTo(entryPoint.getSearchRule().getYieldsTo().stream().map(Object::toString).collect(Collectors.toList()));

        searchRule.setNotNestableBeneath(entryPoint.getSearchRule().getNotNestableBeneath().stream().map(Object::toString).collect(Collectors.toList()));
        searchRule.setNotNestableBeneathDetectables(entryPoint.getSearchRule().getNotNestableBeneathDetectables().stream().map(Object::toString).collect(Collectors.toList()));

        entryPointData.setSearchRule(searchRule);

        return entryPointData;
    }

    private HelpJsonDetectable convertDetectable(DetectableDefinition detectableDefinition) {
        HelpJsonDetectable detectableData = new HelpJsonDetectable();
        detectableData.setDetectableName(detectableDefinition.getName());
        detectableData.setDetectableLanguage(detectableDefinition.getLanguage());
        detectableData.setDetectableRequirementsMarkdown(detectableDefinition.getRequirementsMarkdown());
        detectableData.setDetectableForge(detectableDefinition.getForge());
        detectableData.setDetectableAccuracy(detectableDefinition.getAccuracyType().name());
        return detectableData;
    }

}
