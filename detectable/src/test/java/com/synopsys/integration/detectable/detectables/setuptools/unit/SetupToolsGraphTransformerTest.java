package com.synopsys.integration.detectable.detectables.setuptools.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.setuptools.parse.SetupToolsParsedResult;
import com.synopsys.integration.detectable.detectables.setuptools.transform.SetupToolsGraphTransformer;
import com.synopsys.integration.detectable.python.util.PythonDependency;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class SetupToolsGraphTransformerTest {
    @Test
    public void testNoPipTransform() throws ExecutableRunnerException {
        // Create a list of PythonDependency objects
        List<PythonDependency> dependencies = new ArrayList<>();
        dependencies.add(new PythonDependency("requests", "2.25.1"));
        dependencies.add(new PythonDependency("numpy", "1.21.0"));

        // Create a SetupToolsParsedResult object
        SetupToolsParsedResult parsedResult = new SetupToolsParsedResult("setuptools", "1.0.0", dependencies);

        // Create an instance of SetupToolsGraphTransformer
        SetupToolsGraphTransformer graphTransformer = new SetupToolsGraphTransformer(null, new ExternalIdFactory(), null);

        // Call the transform method
        DependencyGraph dependencyGraph = graphTransformer.transform(null, parsedResult);

        assertNotNull(dependencyGraph);
        
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, dependencyGraph);

        graphAssert.hasRootDependency("requests", "2.25.1");
        graphAssert.hasRootDependency("numpy", "1.21.0");
        
        assertEquals(2, dependencyGraph.getRootDependencies().size());
    }
}
