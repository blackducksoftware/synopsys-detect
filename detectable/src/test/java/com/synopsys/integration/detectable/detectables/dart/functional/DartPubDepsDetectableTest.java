package com.synopsys.integration.detectable.detectables.dart.functional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.DartResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.FlutterResolver;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.dart.pubdep.DartPubDepsDetectableOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.executable.ExecutableOutput;

public class DartPubDepsDetectableTest extends DetectableFunctionalTest {
    public DartPubDepsDetectableTest() throws IOException {
        super("dart");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("pubspec.lock"));

        addFile(Paths.get("pubspec.yaml"));

        ExecutableOutput executableOutput = createStandardOutput(
            "http_parser 3.1.5-dev",
            "|-- pedantic 1.9.2",
            "|   '-- meta...",
            "|-- source_span 1.7.0",
            "|   |-- meta 1.2.3",
            "|   |-- path 1.7.0"
        );
        addExecutableOutput(executableOutput, new File("dart").getAbsolutePath(), "pub", "deps");
    }

    @Override
    @NotNull
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        class DartResolverTest implements DartResolver {
            @Override
            public ExecutableTarget resolveDart() throws DetectableException {
                return ExecutableTarget.forFile(new File("dart"));
            }
        }

        class FlutterResolverTest implements FlutterResolver {
            @Override
            public ExecutableTarget resolveFlutter() throws DetectableException {
                return ExecutableTarget.forFile(new File("flutter"));
            }
        }

        DartPubDepsDetectableOptions dartPubDepsDetectableOptions = new DartPubDepsDetectableOptions(EnumListFilter.excludeNone());
        return detectableFactory.createDartPubDepDetectable(detectableEnvironment, dartPubDepsDetectableOptions, new DartResolverTest(), new FlutterResolverTest());
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertNotEquals(0, extraction.getCodeLocations().size(), "A code location should have been generated.");

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.DART, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("pedantic", "1.9.2");
        graphAssert.hasRootDependency("source_span", "1.7.0");
        graphAssert.hasParentChildRelationship("source_span", "1.7.0", "meta", "1.2.3");
        graphAssert.hasParentChildRelationship("source_span", "1.7.0", "path", "1.7.0");
    }
}
