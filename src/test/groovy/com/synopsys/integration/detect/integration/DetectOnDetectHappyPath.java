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
    public static final String SIGNATURE_SCAN_CODE_LOCATION_SUFFIX = "/synopsys-detect-junit/happy-path scan";
    public static final String DETECTABLE_CODE_LOCATION_SUFFIX = "/detectable/com.synopsys.integration/detectable/%s gradle/bom";
    public static final String SYNOPSYS_DETECT_CODE_LOCATION_SUFFIX = "/com.synopsys.integration/synopsys-detect/%s gradle/bom";
    public static final String DETECT_CONFIGURATION_CODE_LOCATION_SUFFIX = "/detect-configuration/com.synopsys.integration/detect-configuration/%s gradle/bom";
    public static final String DETECTOR_CODE_LOCATION_SUFFIX = "/detector/com.synopsys.integration/detector/%s gradle/bom";

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

        List<String> codeLocationNameSuffixesToCheck = new ArrayList<>();
        codeLocationNameSuffixesToCheck.add(SIGNATURE_SCAN_CODE_LOCATION_SUFFIX);
        codeLocationNameSuffixesToCheck.add(String.format(DETECTABLE_CODE_LOCATION_SUFFIX, version));
        codeLocationNameSuffixesToCheck.add(String.format(SYNOPSYS_DETECT_CODE_LOCATION_SUFFIX, version));
        codeLocationNameSuffixesToCheck.add(String.format(DETECT_CONFIGURATION_CODE_LOCATION_SUFFIX, version));
        codeLocationNameSuffixesToCheck.add(String.format(DETECTOR_CODE_LOCATION_SUFFIX, version));

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
        codeLocationNameSuffixesToCheck.stream().forEach(System.out::println);

        assertEquals(codeLocationNameSuffixesToCheck.size(), createdCodeLocationNames.size());
        int matches = 0;
        for (String suffix : codeLocationNameSuffixesToCheck) {
            for (String codeLocationName : createdCodeLocationNames) {
                if (codeLocationName.endsWith(suffix)) {
                    matches++;
                }
            }
        }
        assertEquals(codeLocationNameSuffixesToCheck.size(), matches);

        List<VersionBomComponentView> bomComponents = projectBomService.getComponentsForProjectVersion(projectVersionWrapper.getProjectVersionView());
        Optional<VersionBomComponentView> blackDuckCommonComponent = bomComponents.stream().filter(versionBomComponentView -> "blackduck-common".equals(versionBomComponentView.getComponentName())).findFirst();
        assertTrue(blackDuckCommonComponent.isPresent());
    }

}
