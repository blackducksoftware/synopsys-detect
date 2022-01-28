package com.synopsys.integration.detect.workflow.blackduck.project;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.view.LicenseView;
import com.synopsys.integration.blackduck.service.dataservice.LicenseService;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;

public class FindLicenseUrlsOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LicenseService licenseService;

    public FindLicenseUrlsOperation(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    public List<String> findLicenseUrls(List<String> licenseNames) throws DetectUserFriendlyException, IntegrationException {
        List<String> licenseUrls = new LinkedList<>();
        for (String licenseName : licenseNames) {
            Optional<String> licenseUrl = licenseService.getLicenseUrlByLicenseName(licenseName)
                .map(HttpUrl::string);
            if (licenseUrl.isPresent()) {
                logger.debug("Found url for License: {}", licenseName);
                licenseUrls.add(licenseUrl.get());
            } else {
                List<LicenseView> suggestedLicenses = licenseService.searchLicensesByName(licenseName);
                logger.error(String.format("Could not find url for license with name %s.  License names are case sensitive, please verify the name of your license in Black Duck in the License Management section. "
                    + "Here are some suggested licenses based on the name you provided:", licenseName));
                suggestedLicenses.stream()
                    .map(LicenseView::getName)
                    .map(name -> "\t" + name)
                    .forEach(logger::error);

                throw new DetectUserFriendlyException(String.format("Detect was unable to find a url for license %s", licenseName), ExitCodeType.FAILURE_GENERAL_ERROR);
            }
        }
        return licenseUrls;
    }
}
