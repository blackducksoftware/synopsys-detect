package com.synopsys.integration.detectable.detectables.go.gomod.process;

import java.util.List;
import java.util.Optional;

import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListAllData;
import com.synopsys.integration.detectable.detectables.go.gomod.model.ReplaceData;

public class GoVersionManager {
    private final List<GoListAllData> allModules;

    public GoVersionManager(List<GoListAllData> allModules) {
        this.allModules = allModules;
    }

    public Optional<String> getVersionForModule(String moduleName) {
        return allModules.stream()
                   .filter(module -> moduleName.equals(module.getPath()))
                   .map(module -> Optional.ofNullable(module.getReplace())
                                      .map(ReplaceData::getVersion)
                                      .orElse(module.getVersion())
                   )
                   .findFirst();
    }
}
