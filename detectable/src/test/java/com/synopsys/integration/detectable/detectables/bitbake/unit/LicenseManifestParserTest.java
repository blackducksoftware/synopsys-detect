package com.synopsys.integration.detectable.detectables.bitbake.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bitbake.parse.LicenseManifestParser;
import com.synopsys.integration.exception.IntegrationException;

public class LicenseManifestParserTest {

    @Test
    void test() throws IntegrationException {
        LicenseManifestParser parser = new LicenseManifestParser();
        List<String> lines = Arrays.asList(
            "PACKAGE NAME: adwaita-icon-theme",
            "PACKAGE VERSION: 3.34.3",
            "RECIPE NAME: adwaita-icon-theme",
            "LICENSE: LGPL-3.0 | CC-BY-SA-3.0",
            "",
            "PACKAGE NAME: adwaita-icon-theme-symbolic",
            "PACKAGE VERSION: 3.34.3",
            "RECIPE NAME: adwaita-icon-theme",
            "LICENSE: LGPL-3.0 | CC-BY-SA-3.0",
            "",
            "PACKAGE NAME: alsa-conf",
            "PACKAGE VERSION: 1.2.5.1",
            "RECIPE NAME: alsa-lib",
            "LICENSE: LGPLv2.1 & GPLv2+");

        Map<String, String> imageRecipes = parser.collectImageRecipes(lines);

        assertEquals(2, imageRecipes.size());
        assertTrue(imageRecipes.get("adwaita-icon-theme").equals("3.34.3"));
        assertTrue(imageRecipes.get("alsa-lib").equals("1.2.5.1"));
    }
}
