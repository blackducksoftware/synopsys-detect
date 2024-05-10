package com.synopsys.integration.detectable.detectables.poetry.functional;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.poetry.PoetryOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class PoetryDetectableExcludeDevTest  extends DetectableFunctionalTest {

    PoetryDetectableExcludeDevTest() throws IOException {
        super("poetry_exclude_dev");
    }

    @Override
    protected void setup() throws IOException {
        addFile(
            Paths.get("pyproject.toml"),
            "[tool.poetry]\n" + //
            "name = \"poetry-demo\"\n" + //
            "version = \"0.1.0\"\n" + //
            "description = \"\"\n" + //
            "authors = [\"Some Author\"]\n" + //
            "\n" + //
            "[tool.poetry.dependencies]\n" + //
            "python = \"^3.11\"\n" + //
            "pendulum = \"^3.0.0\"\n" + //
            "\n" + //
            "[tool.poetry.group.dev.dependencies]\n" + //
            "boost-histogram = \"^1.4.0\"\n" + //
            "\n" + //
            "[build-system]\n" + //
            "requires = [\"poetry-core\"]\n" + //
            "build-backend = \"poetry.core.masonry.api\""
        );

        addFile(
            Paths.get("poetry.lock"),
            "[[package]]\n" + //
            "name = \"boost-histogram\"\n" + //
            "version = \"1.4.1\"\n" + //
            "description = \"The Boost::Histogram Python wrapper.\"\n" + //
            "optional = false\n" + //
            "python-versions = \">=3.7\"\n" + //
            "\n" + //
            "[package.dependencies]\n" + //
            "numpy = \"*\"\n" + //
            "\n" + //
            "[[package]]\n" + //
            "name = \"numpy\"\n" + //
            "version = \"1.26.4\"\n" + //
            "description = \"Fundamental package for array computing in Python\"\n" + //
            "optional = false\n" + //
            "python-versions = \">=3.9\"\n" + //
            "\n" + //
            "[[package]]\n" + //
            "name = \"pendulum\"\n" + //
            "version = \"3.0.0\"\n" + //
            "description = \"Python datetimes made easy\"\n" + //
            "optional = false\n" + //
            "python-versions = \">=3.8\"\n" + //
            "\n" + //
            "[package.dependencies]\n" + //
            "python-dateutil = \">=2.6\"\n" + //
            "tzdata = \">=2020.1\"\n" + //
            "\n" + //
            "[[package]]\n" + //
            "name = \"python-dateutil\"\n" + //
            "version = \"2.9.0.post0\"\n" + //
            "description = \"Extensions to the standard Python datetime module\"\n" + //
            "optional = false\n" + //
            "python-versions = \"!=3.0.*,!=3.1.*,!=3.2.*,>=2.7\"\n" + //
            "\n" + //
            "[package.dependencies]\n" + //
            "six = \">=1.5\"\n" + //
            "\n" + //
            "[[package]]\n" + //
            "name = \"six\"\n" + //
            "version = \"1.16.0\"\n" + //
            "description = \"Python 2 and 3 compatibility utilities\"\n" + //
            "optional = false\n" + //
            "python-versions = \">=2.7, !=3.0.*, !=3.1.*, !=3.2.*\"\n" + //
            "\n" + //
            "[[package]]\n" + //
            "name = \"tzdata\"\n" + //
            "version = \"2024.1\"\n" + //
            "description = \"Provider of IANA time zone data\"\n" + //
            "optional = false\n" + //
            "python-versions = \">=2\""
        );
    }

    @Override
    public @NotNull Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createPoetryDetectable(detectableEnvironment, new PoetryOptions(Collections.singletonList("dev")));
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        List<CodeLocation> codeLocations = extraction.getCodeLocations();

        Assertions.assertEquals(1, codeLocations.size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, codeLocations.get(0).getDependencyGraph());

        graphAssert.hasRootSize(1);
        graphAssert.hasRootDependency("pendulum", "3.0.0");
        graphAssert.hasParentChildRelationship("pendulum", "3.0.0", "python-dateutil", "2.9.0.post0");
        graphAssert.hasParentChildRelationship("pendulum", "3.0.0", "tzdata", "2024.1");
        graphAssert.hasParentChildRelationship("python-dateutil", "2.9.0.post0", "six", "1.16.0");
    }
}
