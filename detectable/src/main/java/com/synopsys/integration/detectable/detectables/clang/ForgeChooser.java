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

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectables.clang.linux.LinuxDistro;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManager;

public class ForgeChooser {
    private final ForgeGenerator forgeGenerator;

    public ForgeChooser(ForgeGenerator forgeGenerator) {
        this.forgeGenerator = forgeGenerator;
    }

    public List<Forge> determineForges(ClangPackageManager currentPackageManager, LinuxDistro linuxDistro) {

        Optional<String> actualLinuxDistro = linuxDistro.extractLinuxDistroNameFromEtcDir(new File("/etc"));
        if (actualLinuxDistro.isPresent()) {
            Forge preferredAliasNamespaceForge = forgeGenerator.createPreferredAliasNamespaceForge(actualLinuxDistro.get());
            return Arrays.asList(preferredAliasNamespaceForge);
        }
        return currentPackageManager.getPackageManagerInfo().getForges();
    }
}
