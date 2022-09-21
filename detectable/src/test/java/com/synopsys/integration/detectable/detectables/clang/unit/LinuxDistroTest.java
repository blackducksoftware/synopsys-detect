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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.clang.linux.LinuxDistro;

public class LinuxDistroTest {
    private File testDirectory;

    @BeforeEach
    public void init() throws IOException {
        testDirectory = Files.createTempDirectory("linuxDistroTest").toFile();
    }

    @AfterEach
    public void cleanUp() throws IOException {
        FileUtils.forceDelete(testDirectory);
    }

    @Test
    void testNotFound() {
        LinuxDistro linuxDistro = new LinuxDistro();
        Optional<String> extractedLinuxDistroName = linuxDistro.extractLinuxDistroNameFromEtcDir(testDirectory);

        assertFalse(extractedLinuxDistroName.isPresent());
    }

    @Test
    void testUbuntu() throws IOException {
        File osReleaseFile = new File(testDirectory, "os-release");

        osReleaseFile.deleteOnExit();

        FileUtils.write(osReleaseFile, "ID=ubuntu", StandardCharsets.UTF_8);
        LinuxDistro linuxDistro = new LinuxDistro();
        Optional<String> extractedLinuxDistroName = linuxDistro.extractLinuxDistroNameFromEtcDir(testDirectory);

        assertTrue(extractedLinuxDistroName.isPresent());
        assertEquals("ubuntu", extractedLinuxDistroName.get());
    }

    @Test
    void testFedora() throws IOException {
        File osReleaseFile = new File(testDirectory, "redhat-release");

        osReleaseFile.deleteOnExit();

        FileUtils.write(osReleaseFile, "Fedora-something", StandardCharsets.UTF_8);
        LinuxDistro linuxDistro = new LinuxDistro();
        Optional<String> extractedLinuxDistroName = linuxDistro.extractLinuxDistroNameFromEtcDir(testDirectory);

        assertTrue(extractedLinuxDistroName.isPresent());
        assertEquals("fedora", extractedLinuxDistroName.get());
    }
}
