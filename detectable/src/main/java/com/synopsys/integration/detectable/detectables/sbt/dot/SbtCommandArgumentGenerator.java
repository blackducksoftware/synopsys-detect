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

import com.synopsys.integration.detectable.detectables.sbt.parse.SbtResolutionCacheOptions;

public class SbtCommandArgumentGenerator {
    @Nullable
    private final List<String> sbtCommandAdditionalArguments;

    public SbtCommandArgumentGenerator(SbtResolutionCacheOptions options) {
        if (StringUtils.isNotBlank(options.getSbtCommandAdditionalArguments())) {
            String[] additionalArgs = options.getSbtCommandAdditionalArguments().split(" +");
            this.sbtCommandAdditionalArguments = Arrays.asList(additionalArgs);
        } else {
            this.sbtCommandAdditionalArguments = null;
        }
    }

    @NotNull
    public List<String> generateSbtCmdArgs(String sbtSubCommand) {
        List<String> exeArgs = new ArrayList<>();
        if (sbtCommandAdditionalArguments != null) {
            exeArgs.addAll(sbtCommandAdditionalArguments);
        }
        exeArgs.add(sbtSubCommand);
        return exeArgs;
    }
}
