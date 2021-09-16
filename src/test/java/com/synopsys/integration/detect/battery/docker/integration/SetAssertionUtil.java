package com.synopsys.integration.detect.battery.docker.integration;

import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.collections4.SetUtils;

public class SetAssertionUtil {
    public static <T> void assertSetDifferences(Set<T> actual, Set<T> expected, Consumer<T> expectedMissing, Consumer<T> extraActual) {
        Set<T> different = SetUtils.disjunction(actual, expected);
        for (T differentElement : different) {
            if (expected.contains(differentElement)) {
                expectedMissing.accept(differentElement);
            } else if (actual.contains(differentElement)) {
                extraActual.accept(differentElement);
            } else {
                throw new RuntimeException("Something went wrong comparing two sets. An element was in the disjunction but neither of the sets."); //Should not be possible - jp
            }
        }
    }
}
