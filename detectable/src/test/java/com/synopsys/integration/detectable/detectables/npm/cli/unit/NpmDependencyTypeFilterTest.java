package com.synopsys.integration.detectable.detectables.npm.cli.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.npm.cli.parse.NpmDependencyTypeFilter;

class NpmDependencyTypeFilterTest {
    private final static String NEITHER = "test";
    private final static String DEV = "test-dev";
    private final static String PEER = "test-peer";

    @Test
    void includeNeitherTest() {
        NpmDependencyTypeFilter filter = createFilter(false, false);
        assertTrue(filter.shouldInclude(NEITHER, true));
        assertTrue(filter.shouldInclude(NEITHER, false));
        assertTrue(filter.shouldInclude(DEV, false));
        assertFalse(filter.shouldInclude(DEV, true));
        assertTrue(filter.shouldInclude(PEER, false));
        assertFalse(filter.shouldInclude(PEER, true));
    }

    @Test
    void includeDevTest() {
        NpmDependencyTypeFilter filter = createFilter(true, false);
        assertTrue(filter.shouldInclude(NEITHER, true));
        assertTrue(filter.shouldInclude(NEITHER, false));
        assertTrue(filter.shouldInclude(DEV, true));
        assertTrue(filter.shouldInclude(DEV, false));
        assertFalse(filter.shouldInclude(PEER, true));
        assertTrue(filter.shouldInclude(PEER, false));

    }

    @Test
    void includePeerTest() {
        NpmDependencyTypeFilter filter = createFilter(false, true);
        assertTrue(filter.shouldInclude(NEITHER, true));
        assertTrue(filter.shouldInclude(NEITHER, false));
        assertFalse(filter.shouldInclude(DEV, true));
        assertTrue(filter.shouldInclude(DEV, false));
        assertTrue(filter.shouldInclude(PEER, true));
        assertTrue(filter.shouldInclude(PEER, false));
    }

    @Test
    void includeBothTest() {
        NpmDependencyTypeFilter filter = createFilter(true, true);
        assertTrue(filter.shouldInclude(NEITHER, true));
        assertTrue(filter.shouldInclude(NEITHER, false));
        assertTrue(filter.shouldInclude(DEV, true));
        assertTrue(filter.shouldInclude(DEV, false));
        assertTrue(filter.shouldInclude(PEER, true));
        assertTrue(filter.shouldInclude(PEER, false));
    }

    private NpmDependencyTypeFilter createFilter(boolean includeDevDependencies, boolean includePeerDependencies) {
        Set<String> devDependencies = Collections.singleton(DEV);
        Set<String> peerDependencies = Collections.singleton(PEER);
        return new NpmDependencyTypeFilter(devDependencies, peerDependencies, includeDevDependencies, includePeerDependencies);
    }

}
