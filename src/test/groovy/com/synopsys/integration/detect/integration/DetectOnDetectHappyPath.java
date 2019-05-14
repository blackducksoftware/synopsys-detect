package com.synopsys.integration.detect.integration;

import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.api.generated.view.VersionBomComponentView;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.Application;
import com.synopsys.integration.exception.IntegrationException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.file.Files.lines;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
public class DetectOnDetectHappyPath extends BlackDuckIntegrationTest {
    /**
     synopsys-detect/detectable/com.synopsys.integration/detectable/5.5.0-SNAPSHOT gradle/bom
     synopsys-detect/detector/com.synopsys.integration/detector/5.5.0-SNAPSHOT gradle/bom
     synopsys-detect/detect-configuration/com.synopsys.integration/detect-configuration/5.5.0-SNAPSHOT gradle/bom
     synopsys-detect/com.synopsys.integration/synopsys-detect/5.5.0-SNAPSHOT gradle/bom
     */
    public static final String SIGNATURE_SCAN_CODE_LOCATION = "synopsys-detect/synopsys-detect-junit/happy-path scan";
    public static final String DETECTABLE_CODE_LOCATION = "synopsys-detect/detectable/com.synopsys.integration/detectable/%s gradle/bom";
    public static final String SYNOPSYS_DETECT_CODE_LOCATION = "synopsys-detect/com.synopsys.integration/synopsys-detect/%s gradle/bom";
    public static final String DETECT_CONFIGURATION_CODE_LOCATION = "synopsys-detect/detect-configuration/com.synopsys.integration/detect-configuration/%s gradle/bom";
    public static final String DETECTOR_CODE_LOCATION = "synopsys-detect/detector/com.synopsys.integration/detector/%s gradle/bom";

    private ProjectView projectToDelete = null;
    private List<CodeLocationView> codeLocationsToDelete = null;

    @AfterEach
    public void deleteBlackDuckItems() {
        if (null != projectToDelete) {
            try {
                blackDuckService.delete(projectToDelete);
            } catch (IntegrationException e) {
                e.printStackTrace();
            }
        }

        if (null != codeLocationsToDelete) {
            for (CodeLocationView toDelete : codeLocationsToDelete) {
                try {
                    blackDuckService.delete(toDelete);
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testBomCreatedInBlackDuck() throws Exception {
        Path buildGradle = Paths.get("build.gradle");
        Optional<String> versionLine = lines(buildGradle).map(String::trim).filter(line -> line.startsWith("version")).findFirst();
        String version = StringUtils.substringBetween(versionLine.get(), "'");

        List<String> codeLocationNamesToCheck = new ArrayList<>();
        codeLocationNamesToCheck.add(SIGNATURE_SCAN_CODE_LOCATION);
        codeLocationNamesToCheck.add(String.format(DETECTABLE_CODE_LOCATION, version));
        codeLocationNamesToCheck.add(String.format(SYNOPSYS_DETECT_CODE_LOCATION, version));
        codeLocationNamesToCheck.add(String.format(DETECT_CONFIGURATION_CODE_LOCATION, version));
        codeLocationNamesToCheck.add(String.format(DETECTOR_CODE_LOCATION, version));

        for (String codeLocationName : codeLocationNamesToCheck) {
            Optional<CodeLocationView> codeLocationView = codeLocationService.getCodeLocationByName(codeLocationName);
            if (codeLocationView.isPresent()) {
                blackDuckService.delete(codeLocationView.get());
            }
        }

        String projectName = "synopsys-detect-junit";
        String projectVersionName = "happy-path";
        ProjectVersionWrapper projectVersionWrapper = assertProjectVersionReady(projectName, projectVersionName);
        projectToDelete = projectVersionWrapper.getProjectView();

        List<String> detectArgs = getInitialArgs(projectName, projectVersionName);
        detectArgs.add("--detect.wait.for.results=true");
        Application.main(detectArgs.toArray(new String[detectArgs.size()]));

        codeLocationsToDelete = blackDuckService.getAllResponses(projectVersionWrapper.getProjectVersionView(), ProjectVersionView.CODELOCATIONS_LINK_RESPONSE);
        Set<String> createdCodeLocationNames = codeLocationsToDelete.stream().map(CodeLocationView::getName).collect(Collectors.toSet());
        createdCodeLocationNames.stream().forEach(System.out::println);
        codeLocationNamesToCheck.stream().forEach(System.out::println);

        assertEquals(codeLocationNamesToCheck.size(), createdCodeLocationNames.size());
        for (String name : codeLocationNamesToCheck) {
            assertTrue(createdCodeLocationNames.contains(name));
        }

        List<VersionBomComponentView> bomComponents = projectBomService.getComponentsForProjectVersion(projectVersionWrapper.getProjectVersionView());
        Optional<VersionBomComponentView> blackDuckCommonComponent = bomComponents.stream().filter(versionBomComponentView -> "blackduck-common".equals(versionBomComponentView.getComponentName())).findFirst();
        assertTrue(blackDuckCommonComponent.isPresent());
    }

}
