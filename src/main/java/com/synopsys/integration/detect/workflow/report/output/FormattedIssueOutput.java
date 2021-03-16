/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report.output;

import java.util.List;

public class FormattedIssueOutput {
    public final String type;
    public final List<String> messages;

    public FormattedIssueOutput(final String type, final List<String> messages) {
        this.type = type;
        this.messages = messages;
    }
}
