/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline.stepexecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutorParseEachXml;
import com.synopsys.integration.exception.IntegrationException;

public class StepExecutorParseEachXmlTest {

    private static final String COMMONS_IO_XML = "<?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                                    + "<query version=\"2\">\n"
                                    + "    <rule class=\"maven_jar\" location=\"/root/home/steve/examples/java-tutorial/WORKSPACE:6:1\" name=\"//external:org_apache_commons_commons_io\">\n"
                                    + "        <string name=\"name\" value=\"org_apache_commons_commons_io\"/>\n"
                                    + "        <string name=\"artifact\" value=\"org.apache.commons:commons-io:1.3.2\"/>\n"
                                    + "    </rule>\n"
                                    + "</query>";

    private static final String GUAVA_XML = "<?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                                + "<query version=\"2\">\n"
                                + "    <rule class=\"maven_jar\" location=\"/root/home/steve/examples/java-tutorial/WORKSPACE:1:1\" name=\"//external:com_google_guava_guava\">\n"
                                + "        <string name=\"name\" value=\"com_google_guava_guava\"/>\n"
                                + "        <string name=\"artifact\" value=\"com.google.guava:guava:18.0\"/>\n"
                                + "    </rule>\n"
                                + "</query>";

    @Test
    public void test() throws IntegrationException {
        final StepExecutor stepExecutor = new StepExecutorParseEachXml("/query/rule[@class='maven_jar']/string[@name='artifact']", "value");
        final List<String> input = Arrays.asList(COMMONS_IO_XML, GUAVA_XML);

        final List<String> results = stepExecutor.process(input);

        assertEquals(2, results.size());
        assertEquals("org.apache.commons:commons-io:1.3.2", results.get(0));
        assertEquals("com.google.guava:guava:18.0", results.get(1));
    }
}
