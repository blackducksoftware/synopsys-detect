package com.blackducksoftware.integration.hub.detect.tool.bazel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class BazelExternalIdGeneratorTest {
    private static final String commonsIoXml = "<?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"no\"?> "
                                          +"<query version=\"2\"> "
                                          +"    <rule class=\"maven_jar\" location=\"/root/home/steve/examples/java-tutorial/WORKSPACE:6:1\" name=\"//external:org_apache_commons_commons_io\"> "
                                          +"        <string name=\"name\" value=\"org_apache_commons_commons_io\"/> "
                                          +"        <string name=\"artifact\" value=\"org.apache.commons:commons-io:1.3.2\"/> "
                                          +"    </rule> "
                                          +"</query>";
    private static final String guavaXml = "<?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"no\"?> "
                                               + "<query version=\"2\"> "
                                               + "    <rule class=\"maven_jar\" location=\"/root/home/steve/examples/java-tutorial/WORKSPACE:1:1\" name=\"//external:com_google_guava_guava\"> "
                                               + "        <string name=\"name\" value=\"com_google_guava_guava\"/> "
                                               + "        <string name=\"artifact\" value=\"com.google.guava:guava:18.0\"/> "
                                               + "    </rule> "
                                               + "</query>";

    @Test
    public void test() throws ExecutableRunnerException {

        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final String bazelExe = "notUsed";
        final XPathParser xPathParser = new XPathParser();
        final BazelQueryXmlOutputParser parser = new BazelQueryXmlOutputParser(xPathParser);
        final File workspaceDir = new File("notUsed");
        final String bazelTarget = "//testproject:ProjectRunner";

        BazelExternalIdGenerator generator = new BazelExternalIdGenerator(executableRunner, bazelExe, parser, workspaceDir, bazelTarget);

        BazelExternalIdExtractionSimpleRule simpleRule = new BazelExternalIdExtractionSimpleRule("@.*:jar", "maven_jar",
            "artifact", ":");
        BazelExternalIdExtractionXPathRule xPathRule = new BazelExternalIdExtractionXPathRule(simpleRule);

        // executableRunner.executeQuietly(workspaceDir, bazelExe, targetOnlyVariableSubstitutor.substitute(xPathRule.getTargetDependenciesQueryBazelCmdArguments()));
        final BazelVariableSubstitutor targetOnlyVariableSubstitutor = new BazelVariableSubstitutor(bazelTarget);
        ExecutableOutput executableOutputQueryForDependencies = new ExecutableOutput(0, "@org_apache_commons_commons_io//jar:jar\n@com_google_guava_guava//jar:jar", "");
        Mockito.when(executableRunner.executeQuietly(workspaceDir, bazelExe, targetOnlyVariableSubstitutor.substitute(xPathRule.getTargetDependenciesQueryBazelCmdArguments()))).thenReturn(executableOutputQueryForDependencies);

        // executableRunner.executeQuietly(workspaceDir, bazelExe, dependencyVariableSubstitutor.substitute(xPathRule.getDependencyDetailsXmlQueryBazelCmdArguments()));
        final BazelVariableSubstitutor dependencyVariableSubstitutorCommonsIo = new BazelVariableSubstitutor(bazelTarget, "//external:org_apache_commons_commons_io");
        final BazelVariableSubstitutor dependencyVariableSubstitutorGuava = new BazelVariableSubstitutor(bazelTarget, "//external:com_google_guava_guava");
        ExecutableOutput executableOutputQueryCommonsIo = new ExecutableOutput(0, commonsIoXml, "");
        ExecutableOutput executableOutputQueryGuava = new ExecutableOutput(0, guavaXml, "");
        Mockito.when(executableRunner.executeQuietly(workspaceDir, bazelExe, dependencyVariableSubstitutorCommonsIo.substitute(xPathRule.getDependencyDetailsXmlQueryBazelCmdArguments()))).thenReturn(executableOutputQueryCommonsIo);
        Mockito.when(executableRunner.executeQuietly(workspaceDir, bazelExe, dependencyVariableSubstitutorGuava.substitute(xPathRule.getDependencyDetailsXmlQueryBazelCmdArguments()))).thenReturn(executableOutputQueryGuava);

        List<BazelExternalId> bazelExternalIds = generator.generate(xPathRule);
        assertEquals(2, bazelExternalIds.size());
        assertEquals("org.apache.commons", bazelExternalIds.get(0).getGroup());
        assertEquals("commons-io", bazelExternalIds.get(0).getArtifact());
        assertEquals("1.3.2", bazelExternalIds.get(0).getVersion());

        assertEquals("com.google.guava", bazelExternalIds.get(1).getGroup());
        assertEquals("guava", bazelExternalIds.get(1).getArtifact());
        assertEquals("18.0", bazelExternalIds.get(1).getVersion());
    }
}
