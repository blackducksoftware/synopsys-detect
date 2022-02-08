package com.synopsys.integration.detect.workflow.codelocation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class CreateBdioCodeLocationsFromDetectCodeLocationsOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CodeLocationNameManager codeLocationNameManager;
    private final DirectoryManager directoryManager;

    public CreateBdioCodeLocationsFromDetectCodeLocationsOperation(CodeLocationNameManager codeLocationNameManager, DirectoryManager directoryManager) {
        this.codeLocationNameManager = codeLocationNameManager;
        this.directoryManager = directoryManager;
    }

    public BdioCodeLocationResult transformDetectCodeLocations(List<DetectCodeLocation> detectCodeLocations, String prefix, String suffix, NameVersion projectNameVersion) {
        List<DetectCodeLocation> validDetectCodeLocations = findValidCodeLocations(detectCodeLocations);
        Map<DetectCodeLocation, String> codeLocationsAndNames = createCodeLocationNameMap(validDetectCodeLocations, directoryManager.getSourceDirectory(), projectNameVersion, prefix, suffix);

        Map<String, List<DetectCodeLocation>> codeLocationsByName = separateCodeLocationsByName(codeLocationsAndNames);

        List<BdioCodeLocation> bdioCodeLocations = createBdioCodeLocations(codeLocationsByName);

        return new BdioCodeLocationResult(bdioCodeLocations, codeLocationsAndNames);
    }

    private Map<DetectCodeLocation, String> createCodeLocationNameMap(List<DetectCodeLocation> codeLocations, File detectSourcePath, NameVersion projectNameVersion, String prefix,
        String suffix) {
        Map<DetectCodeLocation, String> nameMap = new HashMap<>();
        for (DetectCodeLocation detectCodeLocation : codeLocations) {
            String codeLocationName = codeLocationNameManager.createCodeLocationName(detectCodeLocation, detectSourcePath, projectNameVersion.getName(), projectNameVersion.getVersion(), prefix, suffix);
            nameMap.put(detectCodeLocation, codeLocationName);
        }
        return nameMap;
    }

    private List<DetectCodeLocation> findValidCodeLocations(List<DetectCodeLocation> detectCodeLocations) {
        List<DetectCodeLocation> validCodeLocations = new ArrayList<>();
        for (DetectCodeLocation detectCodeLocation : detectCodeLocations) {
            if (detectCodeLocation.getDependencyGraph() == null) {
                logger.warn(String.format("Dependency graph is null for code location %s", detectCodeLocation.getSourcePath()));
                continue;
            }
            if (detectCodeLocation.getDependencyGraph().getRootDependencies().isEmpty()) {
                logger.debug(String.format("Could not find any dependencies for code location %s", detectCodeLocation.getSourcePath()));
            }
            validCodeLocations.add(detectCodeLocation);
        }
        return validCodeLocations;
    }

    private Map<String, List<DetectCodeLocation>> separateCodeLocationsByName(Map<DetectCodeLocation, String> detectCodeLocationNameMap) {
        Map<String, List<DetectCodeLocation>> codeLocationNameMap = new HashMap<>();
        for (Map.Entry<DetectCodeLocation, String> detectCodeLocationEntry : detectCodeLocationNameMap.entrySet()) {
            String codeLocationName = detectCodeLocationEntry.getValue();
            codeLocationNameMap.computeIfAbsent(codeLocationName, key -> new ArrayList<>());
            codeLocationNameMap.get(codeLocationName).add(detectCodeLocationEntry.getKey());
        }
        return codeLocationNameMap;
    }

    private List<BdioCodeLocation> createBdioCodeLocations(Map<String, List<DetectCodeLocation>> codeLocationsByName) {
        List<BdioCodeLocation> bdioCodeLocations = new ArrayList<>();
        for (Map.Entry<String, List<DetectCodeLocation>> codeLocationEntry : codeLocationsByName.entrySet()) {
            String codeLocationName = codeLocationEntry.getKey();
            List<DetectCodeLocation> codeLocations = codeLocationEntry.getValue();
            List<BdioCodeLocation> transformedBdioCodeLocations = transformDetectCodeLocationsIntoBdioCodeLocations(codeLocations, codeLocationName);
            bdioCodeLocations.addAll(transformedBdioCodeLocations);
        }

        return bdioCodeLocations;
    }

    private List<BdioCodeLocation> transformDetectCodeLocationsIntoBdioCodeLocations(List<DetectCodeLocation> codeLocations, String codeLocationName) {
        List<BdioCodeLocation> bdioCodeLocations;
        IntegrationEscapeUtil integrationEscapeUtil = new IntegrationEscapeUtil();

        if (codeLocations.size() > 1) {
            bdioCodeLocations = new ArrayList<>();
            for (int i = 0; i < codeLocations.size(); i++) {
                DetectCodeLocation codeLocation = codeLocations.get(i);
                String newCodeLocationName = String.format("%s %d", codeLocationName, i);
                BdioCodeLocation bdioCodeLocation = new BdioCodeLocation(codeLocation, newCodeLocationName, createBdioName(newCodeLocationName, integrationEscapeUtil));
                bdioCodeLocations.add(bdioCodeLocation);

            }
        } else if (codeLocations.size() == 1) {
            DetectCodeLocation codeLocation = codeLocations.get(0);
            BdioCodeLocation bdioCodeLocation = new BdioCodeLocation(codeLocation, codeLocationName, createBdioName(codeLocationName, integrationEscapeUtil));
            bdioCodeLocations = Collections.singletonList(bdioCodeLocation);
        } else {
            logger.error("Created a code location name but no code locations.");
            bdioCodeLocations = new ArrayList<>();
        }

        return bdioCodeLocations;
    }

    private String createBdioName(String codeLocationName, IntegrationEscapeUtil integrationEscapeUtil) {
        String filenameRaw = StringUtils.replaceEach(codeLocationName, new String[] { "/", "\\", " " }, new String[] { "_", "_", "_" });
        return integrationEscapeUtil.replaceWithUnderscore(filenameRaw);
    }
}
