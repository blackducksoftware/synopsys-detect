package com.synopsys.integration.detect.docs.content;

import java.util.HashMap;
import java.util.Map;

public class Terms {
    private final Map<String, String> termMap = new HashMap<>();

    public Terms() {
        termMap.put("solution_name", "Black Duck Detect");
        termMap.put("script_repo_url_bash", "https://detect.synopsys.com/detect9.sh");
        termMap.put("script_repo_url_powershell", "https://detect.blackduck.com/detect9.ps1");
        termMap.put("binary_repo_url_project", "https://repo.blackduck.com/bds-integrations-release/com/blackduck/integration/detect");
        termMap.put("binary_repo_ui_url_project", "https://repo.blackduck.com/bds-integrations-release/com/blackduck/integration/detect");
	termMap.put("binary_repo_jenkins_url_project", "https://repo.blackduck.com/bds-integrations-release/com/blackducksoftware/integration/blackduck-detect/");
	termMap.put("binary_repo_url_sigma", "https://repo.blackduck.com/sigma-release-trial/2022.6.0/");
    }

    public String put(String termKey, String replacementString) {
        return termMap.put(termKey, replacementString);
    }

    public Map<String, String> getTerms() {
        return termMap;
    }
}