package com.blackduck.integration.configuration.util;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.blackduck.integration.common.util.Bdo;

// TODO: Finish testing this class.
class BdoTest {

    @Test
    void or() {
        Optional<Integer> emptyOptional = Optional.empty();
        Optional<Integer> nonEmptyOptional = Optional.of(2);
        Bdo<Integer> bdo = Bdo.of(emptyOptional).or(nonEmptyOptional).or(1);

        Assertions.assertEquals(new Integer(2), bdo.get());
    }
}