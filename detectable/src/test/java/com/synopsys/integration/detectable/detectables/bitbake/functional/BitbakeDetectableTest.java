package com.synopsys.integration.detectable.detectables.bitbake.functional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectableOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.executable.ExecutableOutput;

public class BitbakeDetectableTest extends DetectableFunctionalTest {
    private static final Logger logger = LoggerFactory.getLogger(BitbakeDetectableTest.class);

    public BitbakeDetectableTest() throws IOException {
        super("bitbake");
    }

    @Override
    protected void setup() throws IOException {
        addFile("oe-init-build-env");

        ExecutableOutput bitbakeGOutput = createStandardOutput(
            ""
        );
        addExecutableOutput(bitbakeGOutput, "bash", "-c", "source " + getSourceDirectory().toFile().getCanonicalPath() + File.separator + "oe-init-build-env; " + "bitbake " + "-g " + "core-image-minimal");

        addFile(Paths.get("task-depends.dot"),
            "digraph depends {",
            "\"acl.do_build\" [label = \"acl do_build\\n:2.2.52-r0\\n/home/bit/poky/meta/recipes-support/attr/acl_2.2.52.bb\"]",
            "\"acl.do_build\" -> \"acl.do_package_qa\"",
            "\"acl.do_package\" -> \"attr.do_packagedata\"",
            "\"attr.do_build\" [label = \"attr do_build\\n:2.4.47-r0\\n/home/bit/poky/meta/recipes-support/attr/attr_2.4.47.bb\"]",
            "\"attr.do_build\" -> \"base-files.do_package_write_rpm\"",
            "\"attr.do_build\" -> \"base-passwd.do_package_write_rpm\"",
            "\"base-files.do_build\" [label = \"base-files do_build\\n:3.0.14-r89\\n/home/bit/poky/meta/recipes-core/base-files/base-files_3.0.14.bb\"]",
            "\"base-passwd.do_build\" [label = \"base-passwd do_build\\n:3.5.29-r0\\n/home/bit/poky/meta/recipes-core/base-passwd/base-passwd_3.5.29.bb\"]",
            "}"
        );

        ExecutableOutput bitbakeShowRecipesOutput = createStandardOutput(
            "=== Available recipes: ===",
            "acl:",
            "  meta                 2.2.52",
            "attr:",
            "  meta                 2.4.47",
            "base-files:",
            "  meta                 3.0.14",
            "base-passwd:",
            "  meta                 3.5.29"
        );
        addExecutableOutput(bitbakeShowRecipesOutput, "bash", "-c", "source " + getSourceDirectory().toFile().getCanonicalPath() + File.separator + "oe-init-build-env; " + "bitbake-layers show-recipes");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createBitbakeDetectable(
            detectableEnvironment,
            new BitbakeDetectableOptions("oe-init-build-env", new ArrayList<>(), Collections.singletonList("core-image-minimal"), 0),
            () -> ExecutableTarget.forCommand("bash")
        );
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.YOCTO, extraction.getCodeLocations().get(0).getDependencyGraph());

        graphAssert.hasRootSize(4);

        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        ExternalId aclExternalId = externalIdFactory.createYoctoExternalId("meta", "acl", "2.2.52-r0");
        ExternalId attrExternalId = externalIdFactory.createYoctoExternalId("meta", "attr", "2.4.47-r0");
        ExternalId baseFilesExternalId = externalIdFactory.createYoctoExternalId("meta", "base-files", "3.0.14-r89");
        ExternalId basePasswdExternalId = externalIdFactory.createYoctoExternalId("meta", "base-passwd", "3.5.29-r0");

        graphAssert.hasRootDependency(aclExternalId);
        graphAssert.hasRootDependency(attrExternalId);
        graphAssert.hasRootDependency(baseFilesExternalId);
        graphAssert.hasRootDependency(basePasswdExternalId);
        graphAssert.hasParentChildRelationship(aclExternalId, attrExternalId);
        graphAssert.hasParentChildRelationship(attrExternalId, baseFilesExternalId);
        graphAssert.hasParentChildRelationship(attrExternalId, basePasswdExternalId);
    }
}
