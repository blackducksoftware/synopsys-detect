package com.synopsys.integration.detectable.detectables.clang.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.clang.linux.LinuxDistro;

public class LinuxDistroTest {

    @Test
    void testNotFound() throws IOException {
        File emptyDir = Files.createTempDirectory("linuxDistroTest").toFile();
        LinuxDistro linuxDistro = new LinuxDistro();
        Optional<String> extractedLinuxDistroName = linuxDistro.extractLinuxDistroNameFromEtcDir(emptyDir);

        assertFalse(extractedLinuxDistroName.isPresent());
    }

    @Test
    void testUbuntu() throws IOException {
        File etcDir = Files.createTempDirectory("linuxDistroTest").toFile();
        File osReleaseFile = new File(etcDir, "os-release");
        FileUtils.write(osReleaseFile, "ID=ubuntu", StandardCharsets.UTF_8);
        LinuxDistro linuxDistro = new LinuxDistro();
        Optional<String> extractedLinuxDistroName = linuxDistro.extractLinuxDistroNameFromEtcDir(etcDir);

        assertTrue(extractedLinuxDistroName.isPresent());
        assertEquals("ubuntu", extractedLinuxDistroName.get());
    }

    @Test
    void testFedora() throws IOException {
        File etcDir = Files.createTempDirectory("linuxDistroTest").toFile();
        File osReleaseFile = new File(etcDir, "redhat-release");
        FileUtils.write(osReleaseFile, "Fedora-something", StandardCharsets.UTF_8);
        LinuxDistro linuxDistro = new LinuxDistro();
        Optional<String> extractedLinuxDistroName = linuxDistro.extractLinuxDistroNameFromEtcDir(etcDir);

        assertTrue(extractedLinuxDistroName.isPresent());
        assertEquals("fedora", extractedLinuxDistroName.get());
    }
}
