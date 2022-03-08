package com.synopsys.integration.detectable.detectables.conda.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.conda.model.CondaListElement;
import com.synopsys.integration.detectable.detectables.conda.parser.CondaDependencyCreator;

public class CondaDependencyCreatorTest {
    @Test
    public void testCorrectVersionAndForge() {
        CondaDependencyCreator dependencyCreator = new CondaDependencyCreator(new ExternalIdFactory());

        String platform = "platform";

        CondaListElement pypiComponent = new CondaListElement();
        pypiComponent.version = "1.0.0";
        pypiComponent.name = "numpy";
        pypiComponent.channel = "pypi";
        pypiComponent.buildString = "build";

        String pypiComponentVersion = pypiComponent.version;
        Forge pypiComponentForge = Forge.PYPI;

        Dependency pypiDependency = dependencyCreator.createFromCondaListElement(pypiComponent, platform);
        Assertions.assertEquals(pypiComponentVersion, pypiDependency.getVersion());
        Assertions.assertEquals(pypiComponentForge, pypiDependency.getExternalId().getForge());

        CondaListElement condaComponent = new CondaListElement();
        condaComponent.version = "2.0.0";
        condaComponent.name = "test";
        condaComponent.channel = "defaults";
        condaComponent.buildString = "BUILD";

        String condaComponentVersion = String.format("%s-%s-%s", condaComponent.version, condaComponent.buildString, platform);
        Forge condaComponentForge = Forge.ANACONDA;

        Dependency condaDependency = dependencyCreator.createFromCondaListElement(condaComponent, platform);
        Assertions.assertEquals(condaComponentVersion, condaDependency.getVersion());
        Assertions.assertEquals(condaComponentForge, condaDependency.getExternalId().getForge());
    }
}
