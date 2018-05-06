package com.function.decorators;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.synchronizedList;

/**
 * Makes a function execute only once.
 */
public final class Once {

    public static <T> Supplier<T> once(Supplier<T> sup) {
        checkNotNull(sup);

        List<T> val = synchronizedList(new ArrayList<>(1));
        AtomicInteger oneTime = new AtomicInteger(0);

        Supplier<T> onlyOnce = () -> {
            if (oneTime.get() < 1) {
                val.add(sup.get());
                oneTime.getAndIncrement();
            }
            return val.get(0);
        };
        return onlyOnce;
    }

    public static <T, R> Function<T, R> once(Function<T, R> func) {
        checkNotNull(func);

        List<R> val = synchronizedList(new ArrayList<>(1));
        AtomicInteger oneTime = new AtomicInteger(0);

        Function<T, R> onlyOnce = t -> {
            if (oneTime.get() < 1) {
                val.add(func.apply(t));
                oneTime.getAndIncrement();
            }
            return val.get(0);
        };
        return onlyOnce;
    }

    public static <T, R> Function<T, R> onceToFunc(Supplier<R> sup) {
        checkNotNull(sup);
        return t -> once(sup).get();
    }

    public static <T> Consumer<T> once(Consumer<T> cn) {
        AtomicInteger oneTime = new AtomicInteger(0);
        Consumer<T> onlyOnce = t -> {
            if (oneTime.get() < 1) {
                cn.accept(t);
                oneTime.getAndIncrement();
            }
        };
        return onlyOnce;
    }
}
