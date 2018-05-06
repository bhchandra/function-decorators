package com.function.decorators;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.synchronizedList;

/**
 * Regulate the rate at which an function is executed.
 */
public final class Throttle {


    public static <T> Consumer<T> throttle(Consumer<T> con, long interval, TimeUnit unit) {
        checkNotNull(con, "consumer");
        checkNotNull(unit, "timeUnit");
        checkArgument(interval >= 0, "interval should be greater than or equal than zero");

        List<Long> lastExecuted = synchronizedList(new ArrayList<>(1));
        lastExecuted.add(null);
        long intervalNanos = unit.toNanos(interval);

        return t -> {
            long now = System.nanoTime();
            Long last = lastExecuted.get(0);
            if (last == null) {
                last = now;
                lastExecuted.add(0, now);
            }
            if ((now - last) >= intervalNanos) {
                con.accept(t);
                lastExecuted.add(0, now);
            }
        };
    }

    public static Runnable throttle(Runnable r, long interval, TimeUnit unit) {
        checkNotNull(r, "runnable");
        checkNotNull(unit, "timeUnit");
        checkArgument(interval >= 0, "interval should be greater than or equal than zero");

        List<Long> lastExecuted = synchronizedList(new ArrayList<>(1));
        lastExecuted.add(null);
        long intervalNanos = unit.toNanos(interval);

        return () -> {
            long now = System.nanoTime();
            Long last = lastExecuted.get(0);
            if (last == null) {
                last = now;
                lastExecuted.add(0, now);
            }
            if ((now - last) >= intervalNanos) {
                r.run();
                lastExecuted.add(0, now);
            }
        };
    }

    //unit test
    public static void main(String[] args) throws InterruptedException {

        Duration _1s = Duration.ofSeconds(1);

        Consumer<String> print = System.out::println;

        Consumer<String> throttledPrint = throttle(print, _1s.toMillis(), TimeUnit.MILLISECONDS);


        for (int i = 0; i < 10; i++) {
            System.out.println("times : " + i);
            Thread.sleep(500L);
            throttledPrint.accept("function executed.");
        }

    }

}
