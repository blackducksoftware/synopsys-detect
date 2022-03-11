package com.synopsys.integration.detectable.detectables.bazel.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.FinalStepTransformColonSeparatedGavsToMaven;

public class FinalStepTransformColonSeparatedGavsToMavenTest {

    @Test
    void testThreePart() throws DetectableException {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        FinalStepTransformColonSeparatedGavsToMaven step = new FinalStepTransformColonSeparatedGavsToMaven(externalIdFactory);
        List<Dependency> deps = step.finish(Arrays.asList("xml-apis:xml-apis:1.4.01"));
        assertEquals(1, deps.size());
        String[] pieces = deps.get(0).getExternalId().getExternalIdPieces();
        assertEquals(3, pieces.length);
        assertEquals("xml-apis", pieces[0]);
        assertEquals("xml-apis", pieces[1]);
        assertEquals("1.4.01", pieces[2]);
    }

    @Test
    void testFourPart() throws DetectableException {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        FinalStepTransformColonSeparatedGavsToMaven step = new FinalStepTransformColonSeparatedGavsToMaven(externalIdFactory);
        List<Dependency> deps = step.finish(Arrays.asList("org.bouncycastle:bcutil-jdk15on:jar:1.70"));
        assertEquals(1, deps.size());
        String[] pieces = deps.get(0).getExternalId().getExternalIdPieces();
        assertEquals(3, pieces.length);
        assertEquals("org.bouncycastle", pieces[0]);
        assertEquals("bcutil-jdk15on", pieces[1]);
        assertEquals("1.70", pieces[2]);
    }

    @Test
    void testFivePart() throws DetectableException {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        FinalStepTransformColonSeparatedGavsToMaven step = new FinalStepTransformColonSeparatedGavsToMaven(externalIdFactory);
        List<Dependency> deps = step.finish(Arrays.asList("io.netty:netty-transport-native-epoll:jar:linux-x86_64:4.1.58.Final"));
        assertEquals(1, deps.size());
        String[] pieces = deps.get(0).getExternalId().getExternalIdPieces();
        assertEquals(3, pieces.length);
        assertEquals("io.netty", pieces[0]);
        assertEquals("netty-transport-native-epoll", pieces[1]);
        assertEquals("4.1.58.Final", pieces[2]);
    }
}
