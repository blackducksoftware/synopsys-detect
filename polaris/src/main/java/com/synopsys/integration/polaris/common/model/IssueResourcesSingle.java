/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jayway.jsonpath.JsonPath;
import com.synopsys.integration.polaris.common.api.PolarisResourcesSingle;
import com.synopsys.integration.polaris.common.api.query.model.issue.IssueV0Resource;

public class IssueResourcesSingle extends PolarisResourcesSingle<IssueV0Resource> {
    public String getSourcePath() {
        final List<String> pathPieces = JsonPath.read(getJson(), "$.included[?(@.type == 'path')].attributes.path[*]");
        return StringUtils.join(pathPieces, "/");
    }

}
