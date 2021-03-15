/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.executable.Executable;

public class ExecutableUtils {
    public static Executable createFromTarget(File directory, ExecutableTarget target, String... commands) {
        return Executable.create(directory, target.toCommand(), Arrays.asList(commands)); //TODO Add similar create to library and replace.
    }

    public static Executable createFromTarget(File directory, ExecutableTarget target, List<String> commands) {
        return Executable.create(directory, target.toCommand(), commands); //TODO Add similar create to library and replace.
    }

    public static Executable createFromTarget(File directory, Map<String, String> environmentVariables, ExecutableTarget target, List<String> commands) {
        return Executable.create(directory, environmentVariables, target.toCommand(), commands); //TODO Add similar create to library and replace.
    }
}
