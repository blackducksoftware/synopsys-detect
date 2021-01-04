/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectable.executable;

import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class ExecutableFailedException extends Exception {
    private static final long serialVersionUID = -4117278710469900787L;
    private final int returnCode;
    private final String executableDescription;
    private final ExecutableRunnerException executableException;

    public ExecutableFailedException(Executable executable, ExecutableRunnerException executableException) {
        super("An exception occurred running an executable.", executableException);
        this.executableException = executableException;
        this.returnCode = 0;
        this.executableDescription = executable.getExecutableDescription();
    }
    
    public ExecutableFailedException(Executable executable, ExecutableOutput executableOutput) {
        super("An executable returned a non-zero exit code: " + executableOutput.getReturnCode());
        this.returnCode = executableOutput.getReturnCode();
        this.executableDescription = executable.getExecutableDescription();
        executableException = null;
    }

    public boolean hasReturnCode() {
        return returnCode != 0;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public String getExecutableDescription() {
        return executableDescription;
    }

    public ExecutableRunnerException getExecutableException() {
        return executableException;
    }
}
