package com.synopsys.integration.detectable.detectables.bazel.functional.bazel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.bazel.BazelExtractor;
import com.synopsys.integration.detectable.detectables.bazel.BazelWorkspace;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bazel.BazelCodeLocationBuilder;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.WorkspaceRuleChooser;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.Pipelines;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.BazelVariableSubstitutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutor;
import com.synopsys.integration.exception.IntegrationException;

public class BazelExtractorTest {

    @Test
    public void testMavenJar() throws ExecutableRunnerException, IntegrationException {
        final BazelCommandExecutor bazelCommandExecutor = Mockito.mock(BazelCommandExecutor.class);
        final BazelVariableSubstitutor bazelVariableSubstitutor = Mockito.mock(BazelVariableSubstitutor.class);
        final Pipelines pipelines = new Pipelines(bazelCommandExecutor, bazelVariableSubstitutor);
        final List<StepExecutor> steps = pipelines.get(WorkspaceRule.MAVEN_JAR);

        final String commonsIoXml = "<?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                                        + "<query version=\"2\">\n"
                                        + "    <rule class=\"maven_jar\" location=\"/root/home/steve/examples/java-tutorial/WORKSPACE:6:1\" name=\"//external:org_apache_commons_commons_io\">\n"
                                        + "        <string name=\"name\" value=\"org_apache_commons_commons_io\"/>\n"
                                        + "        <string name=\"artifact\" value=\"org.apache.commons:commons-io:1.3.2\"/>\n"
                                        + "    </rule>\n"
                                        + "</query>";

        final String guavaXml = "<?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                                    + "<query version=\"2\">\n"
                                    + "    <rule class=\"maven_jar\" location=\"/root/home/steve/examples/java-tutorial/WORKSPACE:1:1\" name=\"//external:com_google_guava_guava\">\n"
                                    + "        <string name=\"name\" value=\"com_google_guava_guava\"/>\n"
                                    + "        <string name=\"artifact\" value=\"com.google.guava:guava:18.0\"/>\n"
                                    + "    </rule>\n"
                                    + "</query>";

        final File workspaceDir = new File(".");
        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final BazelCodeLocationBuilder codeLocationBuilder = new BazelCodeLocationBuilder(externalIdFactory);
        final BazelWorkspace bazelWorkspace = Mockito.mock(BazelWorkspace.class);
        Mockito.when(bazelWorkspace.getDependencyRule()).thenReturn(WorkspaceRule.MAVEN_JAR);
        final WorkspaceRuleChooser workspaceRuleChooser = Mockito.mock(WorkspaceRuleChooser.class);
        Mockito.when(workspaceRuleChooser.choose(Mockito.eq(WorkspaceRule.MAVEN_JAR), Mockito.isNull())).thenReturn(WorkspaceRule.MAVEN_JAR);
        final BazelExtractor bazelExtractor = new BazelExtractor(executableRunner, codeLocationBuilder, workspaceRuleChooser);
        final File bazelExe = new File("/usr/bin/bazel");

        // bazel query 'filter("@.*:jar", deps(//:ProjectRunner))'
        final List<String> bazelArgsGetDependencies = new ArrayList<>();
        bazelArgsGetDependencies.add("query");
        bazelArgsGetDependencies.add("filter('@.*:jar', deps(//:ProjectRunner))");
        final ExecutableOutput bazelCmdExecutableOutputGetDependencies = Mockito.mock(ExecutableOutput.class);
        Mockito.when(bazelCmdExecutableOutputGetDependencies.getReturnCode()).thenReturn(0);
        Mockito.when(bazelCmdExecutableOutputGetDependencies.getStandardOutput()).thenReturn("@org_apache_commons_commons_io//jar:jar\n@com_google_guava_guava//jar:jar");
        Mockito.when(executableRunner.execute(workspaceDir, bazelExe, bazelArgsGetDependencies)).thenReturn(bazelCmdExecutableOutputGetDependencies);

        // bazel query 'kind(maven_jar, //external:org_apache_commons_commons_io)' --output xml
        final List<String> bazelArgsGetDependencyDetailsCommonsIo = new ArrayList<>();
        bazelArgsGetDependencyDetailsCommonsIo.add("query");
        bazelArgsGetDependencyDetailsCommonsIo.add("kind(maven_jar, //external:org_apache_commons_commons_io)");
        bazelArgsGetDependencyDetailsCommonsIo.add("--output");
        bazelArgsGetDependencyDetailsCommonsIo.add("xml");
        final ExecutableOutput bazelCmdExecutableOutputGetDependencyDetailsCommonsIo = Mockito.mock(ExecutableOutput.class);
        Mockito.when(bazelCmdExecutableOutputGetDependencyDetailsCommonsIo.getReturnCode()).thenReturn(0);
        Mockito.when(bazelCmdExecutableOutputGetDependencyDetailsCommonsIo.getStandardOutput()).thenReturn(commonsIoXml);
        Mockito.when(executableRunner.execute(workspaceDir, bazelExe, bazelArgsGetDependencyDetailsCommonsIo)).thenReturn(bazelCmdExecutableOutputGetDependencyDetailsCommonsIo);

        // bazel query 'kind(maven_jar, //external:com_google_guava_guava)' --output xml
        final List<String> bazelArgsGetDependencyDetailsGuava = new ArrayList<>();
        bazelArgsGetDependencyDetailsGuava.add("query");
        bazelArgsGetDependencyDetailsGuava.add("kind(maven_jar, //external:com_google_guava_guava)");
        bazelArgsGetDependencyDetailsGuava.add("--output");
        bazelArgsGetDependencyDetailsGuava.add("xml");
        final ExecutableOutput bazelCmdExecutableOutputGetDependencyDetailsGuava = Mockito.mock(ExecutableOutput.class);
        Mockito.when(bazelCmdExecutableOutputGetDependencyDetailsGuava.getReturnCode()).thenReturn(0);
        Mockito.when(bazelCmdExecutableOutputGetDependencyDetailsGuava.getStandardOutput()).thenReturn(guavaXml);
        Mockito.when(executableRunner.execute(workspaceDir, bazelExe, bazelArgsGetDependencyDetailsGuava)).thenReturn(bazelCmdExecutableOutputGetDependencyDetailsGuava);

        final Extraction result = bazelExtractor.extract(bazelExe, workspaceDir, bazelWorkspace, "//:ProjectRunner", null);

        assertEquals(1, result.getCodeLocations().size());
        final Set<Dependency> dependencies = result.getCodeLocations().get(0).getDependencyGraph().getRootDependencies();
        assertEquals(2, dependencies.size());
        boolean foundCommonsIo = false;
        boolean foundGuava = false;
        for (final Dependency dep : dependencies) {
            System.out.printf("externalId: %s\n", dep.externalId);
            if ("commons-io".equals(dep.externalId.name)) {
                foundCommonsIo = true;
            }
            if ("guava".equals(dep.externalId.name)) {
                foundGuava = true;
            }
        }
        assertTrue(foundCommonsIo);
        assertTrue(foundGuava);
    }


    @Test
    public void testMavenInstall() throws ExecutableRunnerException, IntegrationException {
        final BazelCommandExecutor bazelCommandExecutor = Mockito.mock(BazelCommandExecutor.class);
        final BazelVariableSubstitutor bazelVariableSubstitutor = Mockito.mock(BazelVariableSubstitutor.class);
        final Pipelines pipelines = new Pipelines(bazelCommandExecutor, bazelVariableSubstitutor);
        final List<StepExecutor> steps = pipelines.get(WorkspaceRule.MAVEN_INSTALL);
        final File workspaceDir = new File(".");
        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final BazelCodeLocationBuilder codeLocationBuilder = new BazelCodeLocationBuilder(externalIdFactory);
        final BazelWorkspace bazelWorkspace = Mockito.mock(BazelWorkspace.class);
        Mockito.when(bazelWorkspace.getDependencyRule()).thenReturn(WorkspaceRule.MAVEN_INSTALL);
        final WorkspaceRuleChooser workspaceRuleChooser = Mockito.mock(WorkspaceRuleChooser.class);
        Mockito.when(workspaceRuleChooser.choose(Mockito.eq(WorkspaceRule.MAVEN_INSTALL), Mockito.isNull())).thenReturn(WorkspaceRule.MAVEN_INSTALL);
        final BazelExtractor bazelExtractor = new BazelExtractor(executableRunner, codeLocationBuilder, workspaceRuleChooser);
        final File bazelExe = new File("/usr/bin/bazel");

        // bazel cquery --noimplicit_deps "kind(j.*import, deps(//:ProjectRunner))" --output build
        final List<String> bazelArgsGetDependencies = new ArrayList<>();
        bazelArgsGetDependencies.add("cquery");
        bazelArgsGetDependencies.add("--noimplicit_deps");
        bazelArgsGetDependencies.add("kind(j.*import, deps(//:ProjectRunner))");
        bazelArgsGetDependencies.add("--output");
        bazelArgsGetDependencies.add("build");
        final ExecutableOutput bazelCmdExecutableOutputGetDependencies = Mockito.mock(ExecutableOutput.class);
        Mockito.when(bazelCmdExecutableOutputGetDependencies.getReturnCode()).thenReturn(0);
        Mockito.when(bazelCmdExecutableOutputGetDependencies.getStandardOutput()).thenReturn(
            "jvm_import(\n  name = \"com_google_guava_failureaccess\",\n" +
                "  tags = [\"maven_coordinates=com.google.guava:failureaccess:1.0\"],\n" +
                "  tags = [\"maven_coordinates=com.google.errorprone:error_prone_annotations:2.2.0\"],");
        Mockito.when(executableRunner.execute(workspaceDir, bazelExe, bazelArgsGetDependencies)).thenReturn(bazelCmdExecutableOutputGetDependencies);

        final Extraction result = bazelExtractor.extract(bazelExe, workspaceDir, bazelWorkspace, "//:ProjectRunner", null);

        assertEquals(1, result.getCodeLocations().size());
        final Set<Dependency> dependencies = result.getCodeLocations().get(0).getDependencyGraph().getRootDependencies();
        assertEquals(2, dependencies.size());
        boolean foundFailureAccess = false;
        boolean foundErrorProneAnnotations = false;
        for (final Dependency dep : dependencies) {
            System.out.printf("externalId: %s\n", dep.externalId);
            if ("failureaccess".equals(dep.externalId.name)) {
                foundFailureAccess = true;
            }
            if ("error_prone_annotations".equals(dep.externalId.name)) {
                foundErrorProneAnnotations = true;
            }
        }
        assertTrue(foundFailureAccess);
        assertTrue(foundErrorProneAnnotations);
    }
}
