/*
 * common
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.tuple.Pair;

//Black Duck Stream
// TODO: Test this class.
public class Bds<T> {
    private Stream<T> stream;

    public Bds(final Stream<T> stream) {
        this.stream = stream;
    }

    public <U extends Comparable<? super U>> Bds<T> sortedBy(
        final Function<? super T, ? extends U> keyExtractor) {
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
        return new Bds<>(stream.filter(Objects::nonNull));
    }

    public Bds<T> filter(final Predicate<? super T> predicate) {
        return new Bds<>(stream.filter(predicate));
    }

    public Optional<T> firstFiltered(final Predicate<? super T> predicate) {
        return stream.filter(predicate).findFirst();
    }

    public Bds<T> filterNot(final Predicate<? super T> predicate) {
        return new Bds<>(stream.filter(predicate.negate()));
    }

    public <R> Bds<R> map(final Function<? super T, ? extends R> mapper) {
        return new Bds<>(stream.map(mapper));
    }

    public <R> Bds<R> flatMapStream(final Function<? super T, ? extends Stream<? extends R>> mapper) {
        return new Bds<>(stream.flatMap(mapper));
    }

    public <R> Bds<R> flatMap(final Function<? super T, ? extends Collection<? extends R>> mapper) {
        final Function<? super T, ? extends Stream<? extends R>> streamMapper = value -> mapper.apply(value).stream();
        return new Bds<>(stream.flatMap(streamMapper));
    }

    public void forEach(final Consumer<T> consumer) {
        stream.forEach(consumer);
    }

    public Optional<T> minBy(final Comparator<? super T> comparator) {
        return stream.min(comparator);
    }

    public <R> Map<R, List<T>> groupBy(final Function<? super T, ? extends R> classifier) {
        return stream.collect(Collectors.groupingBy(classifier));
    }

    public <K, U> Map<K, U> toMap(final Function<? super T, ? extends K> keyMapper,
        final Function<? super T, ? extends U> valueMapper) {
        return stream.collect(Collectors.toMap(keyMapper, valueMapper));
    }

    @SafeVarargs
    public static <K, U> Map<K, U> mapOf(final Pair<K, U>... elements) {
        return mapOfEntries(elements);
    }

    @SafeVarargs
    public static <K, U> Map<K, U> mapOfEntries(final Map.Entry<K, U>... elements) {
        return Arrays.stream(elements).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K, U> Bds<Map.Entry<K, U>> of(final Map<K, U> collection) {
        return new Bds<>(collection.entrySet().stream());
    }

    public static <T> Bds<T> of(final Collection<T> collection) {
        return new Bds<>(collection.stream());
    }

    public static <T> Bds<T> of(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") final Optional<T> optional) {
        return optional.map(Bds::of).orElseGet(Bds::of);
    }

    @SafeVarargs
    public static <T> Bds<T> of(final T... elements) {
        return new Bds<>(Arrays.stream(elements));
    }

    public static <T> Bds<T> of(final Iterable<T> iterable) {
        return new Bds<>(StreamSupport.stream(iterable.spliterator(), false));
    }

    public static <T> List<T> listOf(final Iterable<T> iterable) {
        final List<T> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }

    @SafeVarargs
    public static <T> List<T> listOf(final T... elements) {
        return Arrays.asList(elements);
    }

    @SafeVarargs
    public static <T> Set<T> setOf(final T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }
}
