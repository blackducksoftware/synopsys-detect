/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectables.clang.linux.LinuxDistro;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManager;

public class ForgeChooser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LinuxDistro linuxDistro;
    private final ForgeGenerator forgeGenerator;

    public ForgeChooser(ForgeGenerator forgeGenerator, LinuxDistro linuxDistro) {
        this.linuxDistro = linuxDistro;
        this.forgeGenerator = forgeGenerator;
    }

    public List<Forge> determineForges(ClangPackageManager currentPackageManager) {

        Optional<String> actualLinuxDistro = linuxDistro.extractLinuxDistroNameFromEtcDir(new File("/etc"));
        if (actualLinuxDistro.isPresent()) {
            Forge preferredAliasNamespaceForge = forgeGenerator.createPreferredAliasNamespaceForge(actualLinuxDistro.get());
            return Arrays.asList(preferredAliasNamespaceForge);
        }
        List<Forge> possibleForges = currentPackageManager.getPackageManagerInfo().getPossibleForges();
        StringBuilder sb = new StringBuilder();
        possibleForges.stream().map(Forge::getName).forEach(name -> {
            sb.append(name);
            sb.append(" ");
        });
        String forgesList = sb.toString();
        logger.warn("Unable to determine the Linux distro name of the host operating system; will generate components for: {}", forgesList);
        return possibleForges;
    }
}
