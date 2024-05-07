package com.synopsys.integration.detectable.detectables.setuptools.buildless;

import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;

@DetectableInfo(name = "Setuptools", language = "Python", forge = "Pypi", accuracy = DetectableAccuracyType.LOW, requirementsMarkdown = "A pyproject.toml file.")
public class SetupToolsBuildlessDetectable {

}
