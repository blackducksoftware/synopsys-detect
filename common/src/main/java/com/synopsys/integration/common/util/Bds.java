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
    Stream<T> stream;

    public Bds(Stream<T> stream) {
        this.stream = stream;
    }

    public <U extends Comparable<? super U>> Bds<T> sortedBy(Function<? super T, ? extends U> keyExtractor) {
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

    public Bds<T> filter(Predicate<? super T> predicate) {
        return new Bds<>(stream.filter(predicate));
    }

    public Optional<T> firstFiltered(Predicate<? super T> predicate) {
        return stream.filter(predicate).findFirst();
    }

    public Bds<T> filterNot(Predicate<? super T> predicate) {
        return new Bds<>(stream.filter(predicate.negate()));
    }

    public <R> Bds<R> map(Function<? super T, ? extends R> mapper) {
        return new Bds<>(stream.map(mapper));
    }

    public <R> Bds<R> flatMapStream(Function<? super T, ? extends Stream<? extends R>> mapper) {
        return new Bds<>(stream.flatMap(mapper));
    }

    public <R> Bds<R> flatMap(Function<? super T, ? extends Collection<? extends R>> mapper) {
        Function<? super T, ? extends Stream<? extends R>> streamMapper = value -> mapper.apply(value).stream();
        return new Bds<>(stream.flatMap(streamMapper));
    }

    public <K, V> BdsPair<K, V> flatMapToPairs(Function<? super T, ? extends Stream<? extends Pair<K, V>>> mapper) {
        return new BdsPair<>(stream.flatMap(mapper));
    }

    public void forEach(Consumer<T> consumer) {
        stream.forEach(consumer);
    }

    public Optional<T> minBy(Comparator<? super T> comparator) {
        return stream.min(comparator);
    }

    public <R> Map<R, List<T>> groupBy(Function<? super T, ? extends R> classifier) {
        return stream.collect(Collectors.groupingBy(classifier));
    }

    public <K, U> Map<K, U> toMap(
        Function<? super T, ? extends K> keyMapper,
        Function<? super T, ? extends U> valueMapper
    ) {
        return stream.collect(Collectors.toMap(keyMapper, valueMapper));
    }

    @SafeVarargs
    public static <K, U> Map<K, U> mapOf(Pair<K, U>... elements) {
        return mapOfEntries(elements);
    }

    @SafeVarargs
    public static <K, U> Map<K, U> mapOfEntries(Map.Entry<K, U>... elements) {
        return Arrays.stream(elements).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K, U> Bds<Map.Entry<K, U>> of(Map<K, U> collection) {
        return new Bds<>(collection.entrySet().stream());
    }

    public static <T> Bds<T> of(Collection<T> collection) {
        return new Bds<>(collection.stream());
    }

    public static <T> Bds<T> of(Optional<T> optional) {
        return optional.map(Bds::of).orElseGet(Bds::of);
    }

    @SafeVarargs
    public static <T> Bds<T> of(T... elements) {
        return new Bds<>(Arrays.stream(elements));
    }

    public static <T> Bds<T> of(Iterable<T> iterable) {
        return new Bds<>(StreamSupport.stream(iterable.spliterator(), false));
    }

    public static <T> List<T> listOf(Iterable<T> iterable) {
        List<T> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }

    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        return Arrays.asList(elements);
    }

    @SafeVarargs
    public static <T> Set<T> setOf(T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }
}
