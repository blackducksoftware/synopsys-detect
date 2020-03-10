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
package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.Pipelines;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.BazelVariableSubstitutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutor;
import com.synopsys.integration.exception.IntegrationException;

public class PipelineTest {
    private static final List<String> STANDARD_BAZEL_COMMAND_ARGS = Arrays.asList("cquery", "--noimplicit_deps", "kind(j.*import, deps(/:testTarget))", "--output", "build");
    private static final String cqueryOutput = "# /root/.cache/bazel/_bazel_root/896e039de1e50d7e2de0b14a9acf4028/external/exclusion_testing/BUILD:41:1\n"
                                                   + "jvm_import(\n"
                                                   + "  name = \"com_google_guava_guava\",\n"
                                                   + "  tags = [\"maven_coordinates=com.google.guava:guava:27.0-jre\"], \n"
                                                   + "  jars = [\"@exclusion_testing//:v1/https/repo1.maven.org/maven2/com/google/guava/guava/27.0-jre/guava-27.0-jre.jar\"],\n"
                                                   + "  deps = [\"@exclusion_testing//:com_google_guava_listenablefuture\", \"@exclusion_testing//:com_google_code_findbugs_jsr305\", \"@exclusion_testing//:com_google_guava_failureaccess\", \"@exclusion_testing//:com_google_errorprone_error_prone_annotations\", \"@exclusion_testing//:org_checkerframework_checker_qual\"],\n"
                                                   + ")\n"
                                                   + "\n"
                                                   + "# /root/.cache/bazel/_bazel_root/896e039de1e50d7e2de0b14a9acf4028/external/maven/BUILD:103:1\n"
                                                   + "jvm_import(\n"
                                                   + "  name = \"org_hamcrest_hamcrest_core\",\n"
                                                   + "  tags = [\"maven_coordinates=org.hamcrest:hamcrest-core:2.1\"],  \n"
                                                   + "  jars = [\"@maven//:v1/https/jcenter.bintray.com/org/hamcrest/hamcrest-core/2.1/hamcrest-core-2.1.jar\"],\n"
                                                   + "  deps = [\"@maven//:org_hamcrest_hamcrest\"],\n"
                                                   + ")\n"
                                                   + "\n"
                                                   + "# /root/.cache/bazel/_bazel_root/896e039de1e50d7e2de0b14a9acf4028/external/maven/BUILD:115:1\n"
                                                   + "jvm_import(\n"
                                                   + "  name = \"org_hamcrest_hamcrest\",\n"
                                                   + "  tags = [\"maven_coordinates=org.hamcrest:hamcrest:2.1\"], \n"
                                                   + "  jars = [\"@maven//:v1/https/jcenter.bintray.com/org/hamcrest/hamcrest/2.1/hamcrest-2.1.jar\"],\n"
                                                   + "  deps = [],\n"
                                                   + ")\n"
                                                   + "\n"
                                                   + "# /root/.cache/bazel/_bazel_root/896e039de1e50d7e2de0b14a9acf4028/external/exclusion_testing/BUILD:30:1\n"
                                                   + "jvm_import(\n"
                                                   + "  name = \"com_google_guava_failureaccess\",\n"
                                                   + "  tags = [\"maven_coordinates=com.google.guava:failureaccess:1.0\"],\n"
                                                   + "  jars = [\"@exclusion_testing//:v1/https/repo1.maven.org/maven2/com/google/guava/failureaccess/1.0/failureaccess-1.0.jar\"],\n"
                                                   + "  deps = [],\n"
                                                   + ")\n"
                                                   + "\n"
                                                   + "# /root/.cache/bazel/_bazel_root/896e039de1e50d7e2de0b14a9acf4028/external/exclusion_testing/BUILD:68:1\n"
                                                   + "jvm_import(\n"
                                                   + "  name = \"org_checkerframework_checker_qual\",\n"
                                                   + "  tags = [\"maven_coordinates=org.checkerframework:checker-qual:2.5.2\"],\n"
                                                   + "  jars = [\"@exclusion_testing//:v1/https/repo1.maven.org/maven2/org/checkerframework/checker-qual/2.5.2/checker-qual-2.5.2.jar\"],\n"
                                                   + "  deps = [],\n"
                                                   + ")\n"
                                                   + "\n"
                                                   + "# /root/.cache/bazel/_bazel_root/896e039de1e50d7e2de0b14a9acf4028/external/exclusion_testing/BUILD:19:1\n"
                                                   + "jvm_import(\n"
                                                   + "  name = \"com_google_errorprone_error_prone_annotations\", \n"
                                                   + "  tags = [\"maven_coordinates=com.google.errorprone:error_prone_annotations:2.2.0\"],\n"
                                                   + "  jars = [\"@exclusion_testing//:v1/https/repo1.maven.org/maven2/com/google/errorprone/error_prone_annotations/2.2.0/error_prone_annotations-2.2.0.jar\"],\n"
                                                   + "  deps = [],\n"
                                                   + ")\n"
                                                   + "\n"
                                                   + "# /root/.cache/bazel/_bazel_root/896e039de1e50d7e2de0b14a9acf4028/external/exclusion_testing/BUILD:57:1\n"
                                                   + "jvm_import(\n"
                                                   + "  name = \"com_google_guava_listenablefuture\",\n"
                                                   + "  tags = [\"maven_coordinates=com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava\"],\n"
                                                   + "  jars = [\"@exclusion_testing//:v1/https/repo1.maven.org/maven2/com/google/guava/listenablefuture/9999.0-empty-to-avoid-conflict-with-guava/listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar\"],\n"
                                                   + "  deps = [],\n"
                                                   + ")\n"
                                                   + "# /root/.cache/bazel/_bazel_root/896e039de1e50d7e2de0b14a9acf4028/external/exclusion_testing/BUILD:8:1\n"
                                                   + "jvm_import(\n"
                                                   + "  name = \"com_google_code_findbugs_jsr305\",\n"
                                                   + "  tags = [\"maven_coordinates=com.google.code.findbugs:jsr305:3.0.2\"],\n"
                                                   + "  jars = [\"@exclusion_testing//:v1/https/repo1.maven.org/maven2/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar\"],\n"
                                                   + "  deps = [],\n"
                                                   + ")\n";

    private static final String cqueryOutputMixed = "java_import(\n"
                                                        + "  name = \"sso-adminsdk\",\n"
                                                        + "  tags = [\"__SOME_OTHER_TAG__\"],\n"
                                                        + "  generator_name = \"sso-adminsdk\",\n"
                                                        + "  generator_function = \"java_import\",\n"
                                                        + "  generator_location = \"/home/dail/test/b-cls-bazel/2e7559aa3afc160ade8ae4cb99d56da9/external/rd_platform_services/BUILD.bazel:238\",\n"
                                                        + "  jars = [\"@rd_platform_services//:lin64/jars/sso-adminsdk.jar\"],\n"
                                                        + ")\n"
                                                        + "\n"
                                                        + "# /home/dail/test/b-cls-bazel/2e7559aa3afc160ade8ae4cb99d56da9/external/thing_cis/BUILD.bazel:20:5\n"
                                                        + "java_import(\n"
                                                        + "  name = \"thing-common-client\",\n"
                                                        + "  tags = [\"__SOME_OTHER_TAG__\", \"maven_coordinates=com.company.thing:thing-common-client:2.100.0\"],\n"
                                                        + "  generator_name = \"thing-common-client\",\n"
                                                        + "  generator_function = \"java_import\",\n"
                                                        + "  generator_location = \"/home/dail/test/b-cls-bazel/2e7559aa3afc160ade8ae4cb99d56da9/external/thing_cis/BUILD.bazel:20\",\n"
                                                        + "  jars = [\"@thing_cis//:java-toolkit/runtime/thing-common-client-2.100.0.jar\"],\n"
                                                        + "  srcjar = \"@thing_cis//:java-toolkit/runtime/thing-common-client-2.100.0-sources.jar\",\n"
                                                        + ")\n"
                                                        + "\n"
                                                        + "# /home/dail/test/b-cls-bazel/2e7559aa3afc160ade8ae4cb99d56da9/external/cls_maven_stubs/BUILD:41:1\n"
                                                        + "jvm_import(\n"
                                                        + "  name = \"javax_servlet_javax_servlet_api\",\n"
                                                        + "  tags = [\"maven_coordinates=javax.servlet:javax.servlet-api:3.0.1\"],\n"
                                                        + "  jars = [\"@cls_maven_stubs//:v1/https/build-artifactory.eng.company.com/artifactory/maven/javax/servlet/javax.servlet-api/3.0.1/javax.servlet-api-3.0.1.jar\"],\n"
                                                        + "  srcjar = \"@cls_maven_stubs//:v1/https/build-artifactory.eng.company.com/artifactory/maven/javax/servlet/javax.servlet-api/3.0.1/javax.servlet-api-3.0.1-sources.jar\",\n"
                                                        + "  deps = [],\n"
                                                        + "  neverlink = True,\n"
                                                        + ")";

    private static final String cqueryOutputMixedReversedOrder = "java_import(\n"
                                                                     + "  name = \"thing-common-client\",\n"
                                                                     + "  tags = [\"maven_coordinates=com.company.thing:thing-common-client:2.100.0\", \"__SOME_OTHER_TAG__\"],\n"
                                                                     + "  generator_name = \"thing-common-client\",\n"
                                                                     + "  generator_function = \"java_import\",\n"
                                                                     + "  generator_location = \"/home/dail/test/b-cls-bazel/2e7559aa3afc160ade8ae4cb99d56da9/external/thing_cis/BUILD.bazel:20\",\n"
                                                                     + "  jars = [\"@thing_cis//:java-toolkit/runtime/thing-common-client-2.100.0.jar\"],\n"
                                                                     + "  srcjar = \"@thing_cis//:java-toolkit/runtime/thing-common-client-2.100.0-sources.jar\",\n"
                                                                     + ")";

    @Test
    public void testMavenInstall() throws IntegrationException {
        final List<String> userProvidedCqueryAdditionalOptions = null;

        final List<String> results = doTest(STANDARD_BAZEL_COMMAND_ARGS, userProvidedCqueryAdditionalOptions, cqueryOutput);
        assertEquals(8, results.size());
        assertEquals("com.google.guava:guava:27.0-jre", results.get(0));
        assertEquals("com.google.code.findbugs:jsr305:3.0.2", results.get(7));
    }

    @Test
    public void testMavenInstallMixedTags() throws IntegrationException {
        final List<String> userProvidedCqueryAdditionalOptions = null;

        final List<String> results = doTest(STANDARD_BAZEL_COMMAND_ARGS, userProvidedCqueryAdditionalOptions, cqueryOutputMixed);
        assertEquals(2, results.size());
        assertEquals("com.company.thing:thing-common-client:2.100.0", results.get(0));
        assertEquals("javax.servlet:javax.servlet-api:3.0.1", results.get(1));
    }

    @Test
    public void testMavenInstallMixedTagsReversedOrder() throws IntegrationException {
        final List<String> userProvidedCqueryAdditionalOptions = null;

        final List<String> results = doTest(STANDARD_BAZEL_COMMAND_ARGS, userProvidedCqueryAdditionalOptions, cqueryOutputMixedReversedOrder);
        assertEquals(1, results.size());
        assertEquals("com.company.thing:thing-common-client:2.100.0", results.get(0));
    }

    @Test
    public void testMavenInstallCqueryAdditionalOptions() throws IntegrationException {
        final List<String> userProvidedCqueryAdditionalOptions = Arrays.asList("--option1=a", "--option2=b");
        final List<String> expectedBazelCommandArgs = Arrays.asList("cquery", "--noimplicit_deps", "--option1=a", "--option2=b", "kind(j.*import, deps(/:testTarget))", "--output", "build");

        List<String> results = doTest(expectedBazelCommandArgs, userProvidedCqueryAdditionalOptions, cqueryOutput);
        assertEquals(8, results.size());
        assertEquals("com.google.guava:guava:27.0-jre", results.get(0));
        assertEquals("com.google.code.findbugs:jsr305:3.0.2", results.get(7));
    }

    private List<String> doTest(final List<String> expectedBazelCommandArgs, final List<String> userProvidedCqueryAdditionalOptions, final String input) throws IntegrationException {
        final BazelCommandExecutor bazelCommandExecutor = Mockito.mock(BazelCommandExecutor.class);
        Mockito.when(bazelCommandExecutor.executeToString(expectedBazelCommandArgs)).thenReturn(Optional.of(input));
        final BazelVariableSubstitutor bazelVariableSubstitutor = new BazelVariableSubstitutor("/:testTarget", userProvidedCqueryAdditionalOptions);

        final Pipelines pipelines = new Pipelines(bazelCommandExecutor, bazelVariableSubstitutor);
        System.out.printf("# maven_install steps: %d\n", pipelines.get(WorkspaceRule.MAVEN_INSTALL).size());
        List<String> intermediateResult = new ArrayList<>();
        for (final StepExecutor stepExecutor : pipelines.get(WorkspaceRule.MAVEN_INSTALL)) {
            intermediateResult = stepExecutor.process(intermediateResult);
        }
        return intermediateResult;
    }
}
