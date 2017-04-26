/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.util;

import java.util.Map;

public class Command {
    private final String executableName;

    private final String[] args;

    public Command(final String executableName, final String... args) {
        this.executableName = executableName;
        this.args = args;
    }

    public String getExecutableName(final Map<String, String> alternativeExecutable) {
        if (alternativeExecutable != null && alternativeExecutable.containsKey(executableName)) {
            return alternativeExecutable.get(executableName);
        }
        return executableName;
    }

    public String[] getArgs() {
        return args;
    }

}
