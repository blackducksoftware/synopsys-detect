package com.synopsys.integration.detectable.detectables.pip.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.pip.model.PipParseResult;
import com.synopsys.integration.detectable.detectables.pip.parser.PipenvGraphParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

@UnitTest
public class PipEnvGraphBareTest {

    @Test
    void testPackageTreatedAsProject() {
        ExternalIdFactory factory = new ExternalIdFactory();
        PipenvGraphParser parser = new PipenvGraphParser(factory);
        List<String> pipFreeze = FunctionalTestFiles.asListOfStrings("/pip/pipenv_pip_freeze.txt");
        List<String> pipGraph = FunctionalTestFiles.asListOfStrings("/pip/pipenv_graph_bare.txt");

        //The project name and version needs to match the file.
        String projectName = "pipenv-virtualenv-test";
        String projectVersion = "1.0.0";

        PipParseResult parseResult = parser.parse(projectName, projectVersion, pipFreeze, pipGraph);

        Assert.assertFalse(parseResult.getCodeLocation().getDependencyGraph().hasDependency(factory.createNameVersionExternalId(Forge.PYPI, projectName, projectVersion)));
        Assert.assertEquals(5, parseResult.getCodeLocation().getDependencyGraph().getRootDependencies().size()); //Should have found 5, not 3 (it should treat project as dependency).

    }
}
