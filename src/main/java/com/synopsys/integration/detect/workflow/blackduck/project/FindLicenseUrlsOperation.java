package com.synopsys.integration.detect.workflow.blackduck.project;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.service.dataservice.LicenseService;
import com.synopsys.integration.exception.IntegrationException;

public class FindLicenseUrlsOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LicenseService licenseService;

    public FindLicenseUrlsOperation(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    public List<String> findLicenseUrls(List<String> licenseNames) throws IntegrationException {
        List<String> licenseUrls = new LinkedList<>();
        for (String licenseName : licenseNames) {
            licenseUrls.add(licenseService.getLicenseUrlByLicenseName(licenseName).string());
            logger.debug("Found url for License: {}", licenseName);
        }
        return licenseUrls;
    }
}
