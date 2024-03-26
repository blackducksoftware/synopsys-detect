package com.synopsys.integration.detectable.detectables.buildroot.unit.model;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.buildroot.model.Parser;
import com.synopsys.integration.detectable.detectables.buildroot.model.ShowInfoComponent;

public class ParserTest {

    private static String sourceJson = "{\n" + //
            "    \"busybox\": {\n" + //
            "        \"type\": \"target\",\n" + //
            "        \"name\": \"busybox\",\n" + //
            "        \"virtual\": false,\n" + //
            "        \"version\": \"1.36.1\",\n" + //
            "        \"licenses\": \"GPL-2.0, bzip2-1.0.4\",\n" + //
            "        \"license_files\": [\n" + //
            "            \"LICENSE\",\n" + //
            "            \"archival/libarchive/bz/LICENSE\"\n" + //
            "        ],\n" + //
            "        \"redistributable\": true,\n" + //
            "        \"dl_dir\": \"busybox\",\n" + //
            "        \"downloads\": [\n" + //
            "            {\n" + //
            "                \"source\": \"busybox-1.36.1.tar.bz2\",\n" + //
            "                \"uris\": [\n" + //
            "                    \"https+https://www.busybox.net/downloads\",\n" + //
            "                    \"https|urlencode+https://sources.buildroot.net/busybox\",\n" + //
            "                    \"https|urlencode+https://sources.buildroot.net\"\n" + //
            "                ]\n" + //
            "            }\n" + //
            "        ],\n" + //
            "        \"stamp_dir\": \"output/build/busybox-1.36.1\",\n" + //
            "        \"source_dir\": \"output/build/busybox-1.36.1\",\n" + //
            "        \"build_dir\": \"output/build/busybox-1.36.1/\",\n" + //
            "        \"install_target\": true,\n" + //
            "        \"install_staging\": false,\n" + //
            "        \"install_images\": false,\n" + //
            "        \"dependencies\": [\n" + //
            "            \"host-skeleton\",\n" + //
            "            \"host-tar\",\n" + //
            "            \"skeleton\",\n" + //
            "            \"toolchain\"\n" + //
            "        ],\n" + //
            "        \"reverse_dependencies\": [],\n" + //
            "        \"cpe-id\": \"cpe:2.3:a:busybox:busybox:1.36.1:*:*:*:*:*:*:*\",\n" + //
            "        \"ignore_cves\": [\n" + //
            "            \"CVE-2022-28391\"\n" + //
            "        ]\n" + //
            "    }}";

    @Test
    public void testParse() {
        Map<String, ShowInfoComponent> nameComponentMap = new Parser().parse(sourceJson);

        assertEquals(1, nameComponentMap.size());
        
        ShowInfoComponent busyBox = nameComponentMap.get("busybox");

        assertNotNull(busyBox);

        assertEquals("busybox", busyBox.getName());
        assertEquals("target", busyBox.getType());
        assertEquals("1.36.1", busyBox.getVersion());
        assertEquals(4, busyBox.getDependencies().size());
        assertEquals(0, busyBox.getReverseDependencies().size());
    }
}
