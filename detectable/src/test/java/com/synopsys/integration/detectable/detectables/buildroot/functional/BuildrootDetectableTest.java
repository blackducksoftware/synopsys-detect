package com.synopsys.integration.detectable.detectables.buildroot.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.buildroot.BuildrootDependencyType;
import com.synopsys.integration.detectable.detectables.buildroot.BuildrootDetectableOptions;
import com.synopsys.integration.detectable.detectables.buildroot.BuildrootExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class BuildrootDetectableTest extends DetectableFunctionalTest {
    public BuildrootDetectableTest() throws IOException {
        super("buildroot");
    }

    @Override
    protected void setup() throws IOException {
        addFile(".config");
        addFile("Makefile");

        addExecutableOutput(
            createStandardOutput(
                FunctionalTestFiles.asString("/buildroot/make-show-info.json")
            ),
            new String[] {
                "make",
                "show-info"
            }
        );
    }

    @Override
    public @NotNull Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createBuildrootDetectable(
            detectableEnvironment,
            new BuildrootDetectableOptions(
                EnumListFilter.fromExcluded(BuildrootDependencyType.HOST)
            ),
            () -> ExecutableTarget.forCommand("make")
        );
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        assertEquals(1, extraction.getCodeLocations().size());

        ExternalIdFactory factory = new ExternalIdFactory();
        ExternalId busyboxId = factory.createNameVersionExternalId(BuildrootExtractor.forge, "busybox", "1.36.1");
        ExternalId gccId = factory.createNameVersionExternalId(BuildrootExtractor.forge, "gcc-final", "12.3.0");

        ExternalId glibcId = factory.createNameVersionExternalId(
            BuildrootExtractor.forge,
            "glibc",
            "2.38-44-gd37c2b20a4787463d192b32041c3406c2bd91de0"
        );
        ExternalId linuxHeadersId = factory.createNameVersionExternalId(BuildrootExtractor.forge, "linux-headers", "6.6.18");

        ExternalId libtoolId = factory.createNameVersionExternalId(BuildrootExtractor.forge, "libtool", "2.4.6");


        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(
            BuildrootExtractor.forge,
            extraction.getCodeLocations().get(0).getDependencyGraph()
        );

        graphAssert.hasDependency(busyboxId);
        graphAssert.hasDependency(gccId);
        graphAssert.hasDependency(glibcId);
        graphAssert.hasDependency(linuxHeadersId);

        graphAssert.hasParentChildRelationship(glibcId, linuxHeadersId);

        graphAssert.hasNoDependency(libtoolId);
    }
    
}
