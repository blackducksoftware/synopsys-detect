package com.synopsys.integration.common.util;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

public class BdsPair<K, V> extends Bds<Pair<K, V>> {
    public BdsPair(Stream<Pair<K, V>> stream) {
        super(stream);
    }

    public Map<K, V> toMap() {
        return stream.collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }
}
