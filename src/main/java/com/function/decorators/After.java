package com.function.decorators;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.synchronizedList;

/**
 * Create a function which is executed after it is called n times,
 * mostly useful for functions with side-effects.(i,e) consumers or runnables.
 */
public final class After {

    public static <T> Consumer<T> after(int count, Consumer<T> cn) {
        checkArgument((count > 0), "count should be greater than zero");
        checkNotNull(cn);

        AtomicInteger runCount = new AtomicInteger(count);

        return t -> {
            if (runCount.get() < 1) {
                cn.accept(t);
            } else {
                runCount.getAndDecrement();
            }
        };
    }

    public static Runnable after(int count, Runnable r) {
        checkArgument((count > 0), "count should be greater than zero");
        checkNotNull(r);

        AtomicInteger runCount = new AtomicInteger(count);

        return () -> {
            if (runCount.get() < 1) {
                r.run();
            } else {
                runCount.getAndDecrement();
            }
        };
    }

    public static <T> Supplier<T> after(int count, Supplier<T> sup) {
        checkArgument((count > 0), "count should be greater than zero");
        checkNotNull(sup);

        List<T> val = synchronizedList(new ArrayList<>(1));
        AtomicInteger runCount = new AtomicInteger(count);

        return () -> {
            if (runCount.get() < 1) {
                return sup.get();
            } else {
                runCount.getAndDecrement();
                return val.get(0);
            }
        };

    }

    //unit test
    public static void main(String[] args) {

        Consumer<String> print = System.out::println;

        Consumer<String> printWithSkipTwice = after(2, print);

        printWithSkipTwice.accept("hello"); //not printed
        printWithSkipTwice.accept("Hey man."); //not printed
        printWithSkipTwice.accept("I didn't hear ya!"); //printed
    }

}
