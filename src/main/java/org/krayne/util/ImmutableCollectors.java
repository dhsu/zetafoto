package org.krayne.util;

import com.google.common.collect.ImmutableList;

import java.util.stream.Collector;

public final class ImmutableCollectors {
    private ImmutableCollectors() {}

    /**
     * A stream {@link Collector} to immutable list.
     *
     * @param <T> list item type
     * @return immutable list collector
     */
    public static <T> Collector<T, ImmutableList.Builder<T>, ImmutableList<T>> toList() {
        return Collector.of(
                ImmutableList::builder,
                ImmutableList.Builder::add,
                (l, r) -> l.addAll(r.build()),
                ImmutableList.Builder::build);
    }
}