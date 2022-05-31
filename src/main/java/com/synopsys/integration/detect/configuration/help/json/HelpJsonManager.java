package com.synopsys.integration.detect.configuration.help.json;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.tool.detector.DetectorRuleFactory;
import com.synopsys.integration.detect.tool.detector.factory.DetectDetectableFactory;
import com.synopsys.integration.detector.rule.DetectableDefinition;
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
