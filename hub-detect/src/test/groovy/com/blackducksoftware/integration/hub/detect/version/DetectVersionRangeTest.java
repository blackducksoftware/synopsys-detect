package com.blackducksoftware.integration.hub.detect.version;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.text.html.Option;

import org.junit.Test;

public class DetectVersionRangeTest {

    @Test
    public void testMajor() {
        asserBestChosen("4.*", 4, 9, 2, "4.9.2", "4.3.7", "3.13.13");
    }

    @Test
    public void testMinor() {
        asserBestChosen("4.2.*", 4, 2, 3, "4.9.2", "4.3.7", "4.1.3", "4.2.2", "4.2.3");
    }

    @Test
    public void testWild() {
        asserBestChosen("*", 5, 0, 0, "4.9.2", "5.0.0", "1.23.16");
    }

    private void asserBestChosen(String range, int major, int minor, int patch, String... versions){
        DetectVersionRange detectVersionRange = DetectVersionRange.fromString("4.*");

        List<DetectVersion> detectVersions = Arrays.asList(versions).stream().map(it -> DetectVersion.fromString(it)).collect(Collectors.toList());

        Optional<DetectVersion> best = detectVersionRange.bestMatch(detectVersions);
        assertEquals(4, best.get().getMajorVersion());
        assertEquals(9, best.get().getMinorVersion());
        assertEquals(2, best.get().getPatchVersion());
    }
}
