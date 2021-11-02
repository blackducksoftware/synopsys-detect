/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.docs.content;

import java.util.HashMap;
import java.util.Map;

public class Terms {

    private final Map<String, String> termMap = new HashMap<>();

    public Terms() {
        termMap.put("script_repo_url_bash", "https://detect.synopsys.com/detect7.sh");
        termMap.put("script_repo_url_powershell", "https://detect.synopsys.com/detect7.ps1");
        termMap.put("binary_repo_url_project", "https://sig-repo.synopsys.com/bds-integrations-release/com/synopsys/integration/synopsys-detect");
        termMap.put("binary_repo_ui_url_project", "https://sig-repo.synopsys.com/ui/repos/tree/General/bds-integrations-release/com/synopsys/integration/synopsys-detect");
    }

    public String put(String termKey, String replacementString) {
        return termMap.put(termKey, replacementString);
    }

    public Map<String, String> getTerms() {
        return termMap;
    }
}