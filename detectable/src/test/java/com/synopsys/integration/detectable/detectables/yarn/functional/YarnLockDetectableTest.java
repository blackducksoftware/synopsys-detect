package com.synopsys.integration.detectable.detectables.yarn.functional;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.yarn.YarnDependencyType;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class YarnLockDetectableTest extends DetectableFunctionalTest {

    public YarnLockDetectableTest() throws IOException {
        super("yarn");
    }

    @Override
    protected void setup() throws IOException {
        addFile(
            Paths.get("yarn.lock"),
            "async@2.5.0:",
            "   version \"2.5.0\"",
            "   dependencies:",
            "     lodash \"4.17.4\"",
            "",
            "lodash@npm:4.17.4",
            "   version \"4.17.4\""
        );

        addFile(
            Paths.get("package.json"),
            "{",
            "   \"name\": \"babel\",",
            "   \"version\": \"1.2.3\",",
            "   \"private\": true,",
            "   \"license\": \"MIT\",",
            "   \"dependencies\": { ",
            "       \"async\": \"2.5.0\",",
            "       \"lodash\": \"4.17.4\"",
            "   }",
            "}"
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createYarnLockDetectable(
            detectableEnvironment,
            new YarnLockOptions(EnumListFilter.fromExcluded(YarnDependencyType.NON_PRODUCTION), new ArrayList<>(0), new ArrayList<>(0))
        );
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size());
        CodeLocation codeLocation = extraction.getCodeLocations().get(0);

        Assertions.assertEquals("babel", extraction.getProjectName());
        Assertions.assertEquals("1.2.3", extraction.getProjectVersion());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, codeLocation.getDependencyGraph());
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("async", "2.5.0");
        graphAssert.hasRootDependency("lodash", "4.17.4");
        graphAssert.hasParentChildRelationship("async", "2.5.0", "lodash", "4.17.4");
    }
}
