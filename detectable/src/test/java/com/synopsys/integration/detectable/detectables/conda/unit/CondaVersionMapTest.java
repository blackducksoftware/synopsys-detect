package com.synopsys.integration.detectable.detectables.conda.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.conda.model.CondaListElement;
import com.synopsys.integration.detectable.detectables.conda.parser.CondaVersionMap;

public class CondaVersionMapTest {
    //TODO- delete this test once one getVersion method is chosen for CondaVersionMap
    @Test
    public void testIfElseVersusMapMethods() {
        CondaVersionMap versionMap = new CondaVersionMap();

        String platform = "platform";

        CondaListElement pypiComponent = new CondaListElement();
        pypiComponent.version = "1.0.0";
        pypiComponent.name = "numpy";
        pypiComponent.channel = "pypi";
        pypiComponent.buildString = "build";

        String pypiComponentVersion = pypiComponent.version;
        Assertions.assertEquals(pypiComponentVersion, versionMap.getVersionIfElse(pypiComponent, platform));
        Assertions.assertEquals(pypiComponentVersion, versionMap.getVersionMap(pypiComponent, platform));

        CondaListElement condaComponent = new CondaListElement();
        condaComponent.version = "2.0.0";
        condaComponent.name = "test";
        condaComponent.channel = "defaults";
        condaComponent.buildString = "BUILD";

        String condaComponentVersion = String.format("%s-%s-%s", condaComponent.version, condaComponent.buildString, platform);
        Assertions.assertEquals(condaComponentVersion, versionMap.getVersionIfElse(condaComponent, platform));
        Assertions.assertEquals(condaComponentVersion, versionMap.getVersionMap(condaComponent, platform));

    }
}
