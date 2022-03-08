package com.synopsys.integration.detectable.detectables.go.unit;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModCliExtractor;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModCommandRunner;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModDependencyType;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoGraphParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoListParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoModWhyParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoVersionParser;
import com.synopsys.integration.detectable.detectables.go.gomod.process.GoModGraphGenerator;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

@Disabled("Questionable value. Should be tested via Detectable tests. Having issues with Mockito. JM-01/2022")
public class GoModCliExtractorTest {

    // These tests weren't updated to use the new json format for go the first call of go list, yet somehow still passing?
    // With the removal of -u, Mockito kept returning null for that first go list call instead of ExecutableOutput.
    // I think this test is redundant with the existence of GoModDetectableMinusWhyTest
    // Leaving it here for now to review one day. JM-01/2022

    @Test
    public void handleMultipleReplacementsForOneComponentTest() throws ExecutableRunnerException, ExecutableFailedException, DetectableException {
        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);
        File directory = new File("");
        ExecutableTarget goExe = ExecutableTarget.forFile(new File(""));
        Answer<ExecutableOutput> executableAnswer = new Answer<ExecutableOutput>() {
            String[] goListArgs = { "list", "-m", "-json" };
            String[] goListJsonArgs = { "list", "-m", "-json", "all" };
            String[] goModGraphArgs = { "mod", "graph" };

            @Override
            public ExecutableOutput answer(InvocationOnMock invocation) {
                Executable executable = invocation.getArgument(0, Executable.class);
                List<String> commandLine = executable.getCommandWithArguments();
                ExecutableOutput result = null;
                if (commandLine.containsAll(Arrays.asList(goListJsonArgs))) {
                    result = goListJsonOutput();
                } else if (commandLine.containsAll(Arrays.asList(goListArgs))) {
                    result = goListOutput();
                } else if (commandLine.containsAll(Arrays.asList(goModGraphArgs))) {
                    result = goModGraphOutput();
                } else {
                    result = new ExecutableOutput(0, "", "");
                }
                return result;
            }
        };

        GoModCliExtractor goModCliExtractor = buildGoModCliExtractor(executableRunner, executableAnswer);

        boolean wasSuccessful = true;
        Extraction extraction = goModCliExtractor.extract(directory, goExe);
        if (extraction.getError() instanceof ArrayIndexOutOfBoundsException) {
            wasSuccessful = false;
        }

        Assertions.assertTrue(wasSuccessful);
    }

    @Test
    public void handleGoModWhyExceptionTest() throws ExecutableRunnerException, ExecutableFailedException, DetectableException {
        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);
        File directory = new File("");
        ExecutableTarget goExe = ExecutableTarget.forFile(new File(""));
        Answer<ExecutableOutput> executableAnswer = new Answer<ExecutableOutput>() {
            String[] goListArgs = { "list", "-m", "-json" };
            String[] goListJsonArgs = { "list", "-m", "-json", "all" };
            String[] goModGraphArgs = { "mod", "graph" };
            String[] goModWhyArgs = { "mod", "why", "-m", "all" };

            @Override
            public ExecutableOutput answer(InvocationOnMock invocation) throws Throwable {
                Executable executable = invocation.getArgument(0, Executable.class);
                List<String> commandLine = executable.getCommandWithArguments();
                ExecutableOutput result = null;
                if (commandLine.containsAll(Arrays.asList(goListJsonArgs))) {
                    result = goListJsonOutput();
                } else if (commandLine.containsAll(Arrays.asList(goListArgs))) {
                    result = goListOutput();
                } else if (commandLine.containsAll(Arrays.asList(goModGraphArgs))) {
                    result = goModGraphOutput();
                } else if (commandLine.containsAll(Arrays.asList(goModWhyArgs))) {
                    throw new ExecutableRunnerException(new DetectableException("Unit Test Go Mod Why error"));
                } else {
                    result = new ExecutableOutput(0, "", "");
                }
                return result;
            }
        };

        GoModCliExtractor goModCliExtractor = buildGoModCliExtractor(executableRunner, executableAnswer);
        boolean wasSuccessful = true;
        Extraction extraction = goModCliExtractor.extract(directory, goExe);
        if (extraction.getError() instanceof ArrayIndexOutOfBoundsException) {
            wasSuccessful = false;
        }

        Assertions.assertTrue(wasSuccessful);
    }

    private GoModCliExtractor buildGoModCliExtractor(DetectableExecutableRunner executableRunner, Answer<ExecutableOutput> executableAnswer) throws ExecutableRunnerException {
        Mockito.doAnswer(executableAnswer).when(executableRunner).execute(Mockito.any(Executable.class));

        GoModWhyParser goModWhyParser = new GoModWhyParser();
        GoVersionParser goVersionParser = new GoVersionParser();
        GoModCommandRunner goModCommandRunner = new GoModCommandRunner(executableRunner);
        GoModGraphGenerator goModGraphGenerator = new GoModGraphGenerator(new ExternalIdFactory());
        GoListParser goListParser = new GoListParser(new GsonBuilder().create());
        GoGraphParser goGraphParser = new GoGraphParser();
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        return new GoModCliExtractor(
            goModCommandRunner,
            goListParser,
            goGraphParser,
            goModWhyParser,
            goVersionParser,
            goModGraphGenerator,
            externalIdFactory,
            GoModDependencyType.UNUSED
        );
    }

    private ExecutableOutput goListOutput() {
        String standardOutput = String.join("\n", Arrays.asList(
            "{",
            "\t\"Path\": \"git.daimler.com/c445/t1\",",
            "\t\"Main\": true,",
            "}"
        ));
        return new ExecutableOutput(0, standardOutput, "");
    }

    private ExecutableOutput goListJsonOutput() {
        String standardOutput = String.join("\n", Arrays.asList(
            "{",
            "\t\"Path\": \"github.com/codegangsta/negroni\",",
            "\t\"Version\": \"v1.0.0\",",
            "\t\"Replace\": {",
            "\t\t\"Path\": \"github.com/codegangsta/negroni\",",
            "\t\t\"Version\": \"v2.0.0\"",
            "\t}",
            "}",
            "",
            "{",
            "\t\"Path\": \"github.com/sirupsen/logrus\",",
            "\t\"Version\": \"v1.1.1\",",
            "\t\"Replace\": {",
            "\t\t\"Path\": \"github.com/sirupsen/logrus\",",
            "\t\t\"Version\": \"v2.0.0\"",
            "\t}",
            "}"
        ));
        return new ExecutableOutput(0, standardOutput, "");
    }

    private ExecutableOutput goModGraphOutput() {
        String standardOutput = String.join("\n", Arrays.asList(
            "github.com/codegangsta/negroni@v1.0.0 github.com/sirupsen/logrus@v1.1.1"
        ));
        return new ExecutableOutput(0, standardOutput, "");
    }
}
