package com.synopsys.integration.detectable.detectables.clang;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectables.clang.linux.LinuxDistro;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManager;

public class ForgeChooser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LinuxDistro linuxDistro;
    private final LinuxDistroToForgeMapper forgeGenerator;

    public ForgeChooser(LinuxDistroToForgeMapper forgeGenerator, LinuxDistro linuxDistro) {
        this.linuxDistro = linuxDistro;
        this.forgeGenerator = forgeGenerator;
    }

    public List<Forge> determineForges(ClangPackageManager currentPackageManager) {

        Optional<String> actualLinuxDistro = linuxDistro.extractLinuxDistroNameFromEtcDir();
        if (actualLinuxDistro.isPresent()) {
            Forge preferredAliasNamespaceForge = forgeGenerator.createPreferredAliasNamespaceForge(actualLinuxDistro.get());
            return Collections.singletonList(preferredAliasNamespaceForge);
        }
        List<Forge> possibleForges = currentPackageManager.getPackageManagerInfo().getPossibleForges();
        String forgesList = generateNamesString(possibleForges);
        logger.warn("Unable to determine the Linux distro name of the host operating system; will generate components for: {}", forgesList);
        return possibleForges;
    }

    @NotNull
    private String generateNamesString(List<Forge> forges) {
        return forges.stream()
            .map(Forge::getName)
            .collect(Collectors.joining(" "));
    }
}
