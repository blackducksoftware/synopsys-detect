package com.synopsys.integration.detectable.detectables.go.gomod.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListAllData;
import com.synopsys.integration.detectable.detectables.go.gomod.model.ReplaceData;

public class GoModDependencyManager {
    private static final String INCOMPATIBLE_SUFFIX = "+incompatible";
    private static final String SHA1_REGEX = "[a-fA-F0-9]{40}";
    private static final Pattern GIT_VERSION_PATTERN = Pattern.compile(String.format(".*(%s).*", SHA1_REGEX));

    private final ExternalIdFactory externalIdFactory;

    private final Map<String, Dependency> modulesAsDependencies;

    public GoModDependencyManager(List<GoListAllData> allModules, ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
        modulesAsDependencies = convertModulesToDependencies(allModules);
    }

    private Map<String, Dependency> convertModulesToDependencies(List<GoListAllData> allModules) {
        Map<String, Dependency> dependencies = new HashMap<>();

        for (GoListAllData module : allModules) {
            String name = Optional.ofNullable(module.getReplace())
                .map(ReplaceData::getPath)
                .orElse(module.getPath());
            String version = Optional.ofNullable(module.getReplace())
                .map(ReplaceData::getVersion)
                .orElse(module.getVersion());
            if (version != null) {
                version = handleGitHash(version);
                version = removeIncompatibleSuffix(version);
            }
            dependencies.put(module.getPath(), convertToDependency(name, version));
        }

        return dependencies;
    }

    private Dependency convertToDependency(String moduleName, @Nullable String moduleVersion) {
        return new Dependency(moduleName, moduleVersion, externalIdFactory.createNameVersionExternalId(Forge.GOLANG, moduleName, moduleVersion));
    }

    public Dependency getDependencyForModule(String moduleName) {
        return modulesAsDependencies.getOrDefault(moduleName, convertToDependency(moduleName, null));
    }

    // When a version contains a commit hash, the KB only accepts the git hash, so we must strip out the rest.
    private String handleGitHash(String version) {
        Matcher matcher = GIT_VERSION_PATTERN.matcher(version);
        if (matcher.matches()) {
            return StringUtils.trim(matcher.group(1));
        }
        return version;
    }

    // https://golang.org/ref/mod#incompatible-versions
    private String removeIncompatibleSuffix(String version) {
        if (version.endsWith(INCOMPATIBLE_SUFFIX)) {
            // Trim incompatible suffix so that KB can match component
            version = version.substring(0, version.length() - INCOMPATIBLE_SUFFIX.length());
        }
        return version;
    }
}
