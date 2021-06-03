/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.battery.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;

import freemarker.template.TemplateException;

//This executable types text from a set of files when executed.
public abstract class TypingExecutableCreator extends BatteryExecutableCreator {

    @Override
    public File createExecutable(final int id, final BatteryExecutableInfo executableInfo, final AtomicInteger commandCount) throws IOException, TemplateException {

        //The data file tracks the current invocation count for this exe. It types a different command each invocation.
        //For example GIT types 'url' first, then 'branch' second. This data file contains 0 at first, then 1 after the first run.
        final File dataFile = new File(executableInfo.getMockDirectory(), "exe-" + id + ".dat");
        FileUtils.writeStringToFile(dataFile, "0", Charset.defaultCharset());

        final Map<String, Object> model = new HashMap<>();
        model.put("dataFile", dataFile.getCanonicalPath());
        model.put("files", Lists.newArrayList(getFilePaths(executableInfo, commandCount)));
        final File commandFile;
        if (SystemUtils.IS_OS_WINDOWS) {
            commandFile = new File(executableInfo.getMockDirectory(), "exe-" + id + ".bat");
            BatteryFiles.processTemplate("/typing-exe.ftl", commandFile, model, BatteryFiles.UTIL_RESOURCE_PREFIX);
        } else {
            commandFile = new File(executableInfo.getMockDirectory(), "sh-" + id + ".sh");
            BatteryFiles.processTemplate("/typing-sh.ftl", commandFile, model, BatteryFiles.UTIL_RESOURCE_PREFIX);
            Assertions.assertTrue(commandFile.setExecutable(true));
        }

        return commandFile;
    }

    public abstract List<String> getFilePaths(BatteryExecutableInfo executableInfo, AtomicInteger commandCount) throws IOException, TemplateException;
}
