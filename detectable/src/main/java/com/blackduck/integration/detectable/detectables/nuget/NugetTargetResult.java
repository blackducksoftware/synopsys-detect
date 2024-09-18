package com.blackduck.integration.detectable.detectables.nuget;

import java.util.List;

import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.util.NameVersion;

public class NugetTargetResult {
    List<CodeLocation> codeLocations;
    NameVersion nameVersion;
}
