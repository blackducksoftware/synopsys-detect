package com.synopsys.integration.detectable.detectables.clang.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectables.clang.ForgeChooser;
import com.synopsys.integration.detectable.detectables.clang.LinuxDistroToForgeMapper;
import com.synopsys.integration.detectable.detectables.clang.linux.LinuxDistro;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManager;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;

public class ForgeChooserTest {

    @Test
    void testDistroIdentified() {
        LinuxDistroToForgeMapper forgeGenerator = Mockito.mock(LinuxDistroToForgeMapper.class);
        LinuxDistro linuxDistro = Mockito.mock(LinuxDistro.class);
        ForgeChooser forgeChooser = new ForgeChooser(forgeGenerator, linuxDistro);
        ClangPackageManager currentPackageManager = Mockito.mock(ClangPackageManager.class);

        Mockito.when(linuxDistro.extractLinuxDistroNameFromEtcDir()).thenReturn(Optional.of("ubuntu"));
        Forge createdForge = Mockito.mock(Forge.class);
        Mockito.when(forgeGenerator.createPreferredAliasNamespaceForge("ubuntu")).thenReturn(createdForge);
        List<Forge> chosenForges = forgeChooser.determineForges(currentPackageManager);

        assertEquals(1, chosenForges.size());
        assertEquals(createdForge, chosenForges.get(0));
    }

    @Test
    void testDistroNotIdentified() {
        LinuxDistroToForgeMapper forgeGenerator = Mockito.mock(LinuxDistroToForgeMapper.class);
        LinuxDistro linuxDistro = Mockito.mock(LinuxDistro.class);
        ForgeChooser forgeChooser = new ForgeChooser(forgeGenerator, linuxDistro);
        ClangPackageManager currentPackageManager = Mockito.mock(ClangPackageManager.class);

        Mockito.when(linuxDistro.extractLinuxDistroNameFromEtcDir()).thenReturn(Optional.empty());
        ClangPackageManagerInfo packageManagerInfo = Mockito.mock(ClangPackageManagerInfo.class);
        Mockito.when(currentPackageManager.getPackageManagerInfo()).thenReturn(packageManagerInfo);
        Forge possibleForge1 = Mockito.mock(Forge.class);
        Mockito.when(possibleForge1.getName()).thenReturn("possibleForge1");
        Forge possibleForge2 = Mockito.mock(Forge.class);
        Mockito.when(possibleForge2.getName()).thenReturn("possibleForge2");
        List<Forge> possibleForges = Arrays.asList(possibleForge1, possibleForge2);
        Mockito.when(packageManagerInfo.getPossibleForges()).thenReturn(possibleForges);
        List<Forge> chosenForges = forgeChooser.determineForges(currentPackageManager);

        assertEquals(2, chosenForges.size());
        assertEquals(possibleForge1, chosenForges.get(0));
        assertEquals(possibleForge2, chosenForges.get(1));
    }
}
