package com.synopsys.integration.detect.util;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.detect.type.OperatingSystemType;

public class TildeInPathResolverTest {
    @Test
    public void testResolvingTilde() throws IllegalArgumentException, IllegalAccessException {
        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin", OperatingSystemType.LINUX);

        final Optional<String> resolved = resolver.resolveTildeInValue("~/Documents/source/funtional/detect");

        Assert.assertEquals("/Users/ekerwin/Documents/source/funtional/detect", resolved.get());
    }

    @Test
    public void testResolvingTildeInWindows() throws IllegalArgumentException, IllegalAccessException {
        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin", OperatingSystemType.WINDOWS);

        final Optional<String> resolved = resolver.resolveTildeInValue("~/Documents/source/funtional/detect");

        Assert.assertFalse(resolved.isPresent());
    }

    @Test
    public void testResolvingTildeInTheMiddleOfAPath() throws IllegalArgumentException, IllegalAccessException {
        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin", OperatingSystemType.LINUX);

        final Optional<String> resolved = resolver.resolveTildeInValue("/Documents/~source/~/funtional/detect");

        Assert.assertFalse(resolved.isPresent());
    }
}
