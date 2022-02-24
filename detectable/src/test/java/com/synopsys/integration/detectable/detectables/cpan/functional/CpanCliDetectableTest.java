package com.synopsys.integration.detectable.detectables.cpan.functional;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
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
        addExecutableOutput(getOutputDirectory(), cpanListOutput, "cpan", "-l");

        ExecutableOutput cpanmShowDepsOutput = createStandardOutput(
            "--> Working on .",
            "Configuring App-cpanminus-1.7043 ... OK",
            "ExtUtils::MakeMaker~6.58",
            "Test::More",
            "perl~5.008001",
            "ExtUtils::MakeMaker"
        );
        addExecutableOutput(getOutputDirectory(), cpanmShowDepsOutput, "cpanm", "--showdeps", ".");
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
