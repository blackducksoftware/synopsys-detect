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

    @Test
    public void testMavenInstall() throws IntegrationException {
        final List<String> userProvidedCqueryAdditionalOptions = null;
        final List<String> expectedBazelCommandArgs = Arrays.asList("cquery", "--noimplicit_deps", "kind(j.*import, deps(/:testTarget))", "--output", "build");

        doTest(expectedBazelCommandArgs, userProvidedCqueryAdditionalOptions);
    }

    @Test
    public void testMavenInstallCqueryAdditionalOptions() throws IntegrationException {
        final List<String> userProvidedCqueryAdditionalOptions = Arrays.asList("--option1=a", "--option2=b");
        final List<String> expectedBazelCommandArgs = Arrays.asList("cquery", "--noimplicit_deps", "--option1=a", "--option2=b", "kind(j.*import, deps(/:testTarget))", "--output", "build");

        doTest(expectedBazelCommandArgs, userProvidedCqueryAdditionalOptions);
    }

    private void doTest(final List<String> expectedBazelCommandArgs, final List<String> userProvidedCqueryAdditionalOptions) throws IntegrationException {
        final BazelCommandExecutor bazelCommandExecutor = Mockito.mock(BazelCommandExecutor.class);
        Mockito.when(bazelCommandExecutor.executeToString(expectedBazelCommandArgs)).thenReturn(Optional.of(cqueryOutput));
        final BazelVariableSubstitutor bazelVariableSubstitutor = new BazelVariableSubstitutor("/:testTarget", userProvidedCqueryAdditionalOptions);

        final Pipelines pipelines = new Pipelines(bazelCommandExecutor, bazelVariableSubstitutor);
        System.out.printf("# maven_install steps: %d\n", pipelines.get(WorkspaceRule.MAVEN_INSTALL).size());
        List<String> input = new ArrayList<>();
        for (final StepExecutor stepExecutor : pipelines.get(WorkspaceRule.MAVEN_INSTALL)) {
            input = stepExecutor.process(input);
        }
        assertEquals(8, input.size());
        assertEquals("com.google.guava:guava:27.0-jre", input.get(0));
        assertEquals("com.google.code.findbugs:jsr305:3.0.2", input.get(7));
    }
}
