/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.sbt.dot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SbtCommandArgumentGenerator {

    @NotNull
    public List<String> generateSbtCmdArgs(@Nullable String sbtCommandAdditionalArgumentsString, String sbtSubCommand) {
        List<String> exeArgs = new ArrayList<>();
        if (StringUtils.isNotBlank(sbtCommandAdditionalArgumentsString)) {
            String[] additionalArgs = sbtCommandAdditionalArgumentsString.split(" +");
            exeArgs.addAll(Arrays.asList(additionalArgs));
        }
        exeArgs.add(sbtSubCommand);
        return exeArgs;
    }
}
