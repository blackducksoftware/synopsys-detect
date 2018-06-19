/**
 * detect-configuration
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
package com.blackducksoftware.integration.hub.detect.interactive;

import java.io.PrintStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.help.DetectOptionManager;
import com.blackducksoftware.integration.hub.detect.interactive.mode.DefaultInteractiveMode;
import com.blackducksoftware.integration.hub.detect.interactive.mode.InteractiveMode;
import com.blackducksoftware.integration.hub.detect.interactive.reader.InteractiveReader;

@Component
public class InteractiveManager {
    private final Logger logger = LoggerFactory.getLogger(InteractiveManager.class);

    @Autowired
    DetectOptionManager detectOptionManager;

    @Autowired
    List<InteractiveMode> interactiveModes;

    @Autowired
    DefaultInteractiveMode defaultInteractiveMode;

    public void interact(final InteractiveReader interactiveReader, final PrintStream printStream) {
        final InteractiveMode interactiveMode = defaultInteractiveMode;

        interactiveMode.init(printStream, interactiveReader);

        interactiveMode.println("");
        interactiveMode.println("Interactive flag found.");
        interactiveMode.println("Starting default interactive mode.");
        interactiveMode.println("");

        try {
            interactiveMode.interact();
            final List<InteractiveOption> interactiveOptions = interactiveMode.getInteractiveOptions();
            detectOptionManager.applyInteractiveOptions(interactiveOptions);
        } catch (final Exception e) {
            logger.error(e.toString());
            logger.error("Interactive mode failed. Please retry interactive mode or remove '-i' and '--interactive' from your options.");
            throw new RuntimeException(e);
        }
    }

}
