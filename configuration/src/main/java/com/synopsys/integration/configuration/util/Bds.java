/**
 * configuration
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.configuration.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Bds<T> {
    private Stream<T> stream;

    public Bds(final Stream<T> stream) {
        this.stream = stream;
    }

    public <U extends Comparable<? super U>> Bds<T> sortedBy(
        Function<? super T, ? extends U> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        stream = stream.sorted(Comparator.comparing(keyExtractor));
        return this;
    }

    public List<T> toList() {
        return stream.collect(Collectors.toList());
    }

    public Set<T> toSet() {
        return stream.collect(Collectors.toSet());
    }

    public Bds<T> filterNotNull() {
        return new Bds<T>(stream.filter(it -> it != null));
    }

    public <R> Bds<R> map(Function<? super T, ? extends R> mapper) {
        return new Bds<>(stream.map(mapper));
    }

    public <K, U> Map<K, U> toMap(Function<? super T, ? extends K> keyMapper,
        Function<? super T, ? extends U> valueMapper) {
        return stream.collect(Collectors.toMap(keyMapper, valueMapper));
    }

    public static <K, U> Bds<Map.Entry<K, U>> of(Map<K, U> collection) {
        return new Bds<>(collection.entrySet().stream());
    }

    public static <T> Bds<T> of(Collection<T> collection) {
        return new Bds<>(collection.stream());
    }

    public static <T> Bds<T> of(Iterable<T> iterable) {
        return new Bds<>(StreamSupport.stream(iterable.spliterator(), false));
    }

    public static <T> List<T> listOf(Iterable<T> iterable) {
        List<T> result = new ArrayList<T>();
        iterable.forEach(result::add);
        return result;
    }
}
