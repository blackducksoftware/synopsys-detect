package com.synopsys.integration.detect.workflow.blackduck.project.licenseurls;

import java.util.List;

import com.synopsys.integration.blackduck.api.generated.view.LicenseView;

public class SuggestedLicenses {
    private final List<LicenseView> suggestedLicenseNames;

    public SuggestedLicenses(List<LicenseView> suggestedLicenseNames) {
        this.suggestedLicenseNames = suggestedLicenseNames;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        suggestedLicenseNames.stream()
            .map(LicenseView::getName)
            .forEach(name ->
                stringBuilder.append("\n").append(name)
            );
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}

