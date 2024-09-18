package com.blackduck.integration.detectable.detectables.cpan.functional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.functional.DetectableFunctionalTest;
import com.blackduck.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.executable.ExecutableOutput;

public class CpanCliDetectableTest extends DetectableFunctionalTest {
    public CpanCliDetectableTest() throws IOException {
        super("cpan");
    }

    @Override
    protected void setup() throws IOException { //TODO: The difference between LIST and SHOWDEPS is not clear from this test.
        addFile("Makefile.PL");

        ExecutableOutput cpanListOutput = createStandardOutput(
            "ExtUtils::MakeMaker\t7.24",
            "perl\t5.1",
            "Test::More\t1.3"
        );
        Map<String, String> envVars = new HashMap<>();
        envVars.put("PERL_MM_USE_DEFAULT", "true");
        addExecutableOutput(getSourceDirectory(), cpanListOutput, envVars, "cpan", "-l");

        ExecutableOutput cpanmShowDepsOutput = createStandardOutput(
            "--> Working on .",
            "Configuring App-cpanminus-1.7043 ... OK",
            "ExtUtils::MakeMaker~6.58",
            "Test::More",
            "perl~5.008001",
            "ExtUtils::MakeMaker"
        );
        addExecutableOutput(getSourceDirectory(), cpanmShowDepsOutput, "cpanm", "--showdeps", ".");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createCpanCliDetectable(detectableEnvironment, () -> ExecutableTarget.forCommand("cpan"), () -> ExecutableTarget.forCommand("cpanm"));
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.CPAN, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(3);
        graphAssert.hasRootDependency("Test-More", "1.3");
        graphAssert.hasRootDependency("ExtUtils-MakeMaker", "7.24");
        graphAssert.hasRootDependency("perl", "5.1");

    }
}
