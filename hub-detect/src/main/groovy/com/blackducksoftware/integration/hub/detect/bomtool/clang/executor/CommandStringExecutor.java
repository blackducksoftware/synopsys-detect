/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.bomtool.clang.executor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

@Component
public class CommandStringExecutor {
    private static final Logger logger = LoggerFactory.getLogger(CommandStringExecutor.class);

    @Autowired
    ExecutableRunner runner;

    public String execute(final File workingDir, Map<String, String> environmentVariables, final String cmd) throws ExecutableRunnerException, IntegrationException {
        logger.debug(String.format("Executing %s in %s", cmd, workingDir));

        // TODO: this is stupid. In some cases caller could easily pass in the parts
        // TODO: For compiler command: string might make sense
        // Or: might be a better detect class to use
        final String[] cmdParts = cmd.split("\\s+");
        final List<String> argsOrig = Arrays.asList(cmdParts);
        final List<String> args = new ArrayList<>(argsOrig.size());
        args.addAll(argsOrig);
        args.remove(0);
        //////////////////////

        // TODO this can probably be simplified; see how docker bomtool does it
        final String newPath = "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin";
        if (environmentVariables == null) {
            environmentVariables = new HashMap<>();
        }
        String path = environmentVariables.get("PATH");
        path = path == null ? newPath : String.format("%s:%s", path, newPath);
        environmentVariables.put("PATH", path);
        logger.debug(String.format("Env: %s", environmentVariables));
        final Executable executor = new Executable(workingDir, environmentVariables, cmdParts[0], args);
        final ExecutableOutput out = runner.executeQuietly(executor);
        final List<String> stderrList = out.getErrorOutputAsList();
        final List<String> stdout = out.getStandardOutputAsList();
        final String stderrString = StringUtils.join(stderrList, '\n');
        final String stdoutString = StringUtils.join(stdout, '\n');
        logger.trace(String.format("Command: '%s'; Output: %s; stderr: %s", cmd, stdoutString, stderrString));
        if (out.getReturnCode() != 0) {
            throw new IntegrationException(String.format("Command '%s' return code: %d; stderr: %s", cmd, out.getReturnCode(), stderrString));
        }
        return stdoutString;
    }
}
