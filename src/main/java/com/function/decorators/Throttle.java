package com.function.decorators;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.synchronizedList;

/**
 * Regulate the rate at which an function is executed.
 */
public final class Throttle {


    public static <T> Consumer<T> throttle(Consumer<T> con, Duration interval) {
        checkNotNull(con);
        checkNotNull(interval);
        checkArgument(!interval.isNegative());
        List<Long> lastExecuted = synchronizedList(new ArrayList<>(1));
        lastExecuted.add(null);
        long intervalInSecs = interval.getSeconds();

        return t -> {
            long now = Instant.now().getEpochSecond();
            Long last = lastExecuted.get(0);
            if (last == null) lastExecuted.add(0, now);
            if ((now - last) >= intervalInSecs) {
                con.accept(t);
                lastExecuted.add(0, now);
            }
        };
    }

    public static Runnable throttle(Runnable r, Duration interval) {
        checkNotNull(r);
        checkNotNull(interval);
        checkArgument(!interval.isNegative());
        List<Long> lastExecuted = synchronizedList(new ArrayList<>(1));
        lastExecuted.add(null);
        long intervalInSecs = interval.getSeconds();

        return () -> {
            long now = Instant.now().getEpochSecond();
            Long last = lastExecuted.get(0);
            if (last == null) lastExecuted.add(0, now);
            if ((now - last) >= intervalInSecs) {
                r.run();
                lastExecuted.add(0, now);
            }
        };
    }


    public static void main(String[] args) throws InterruptedException {
        List<Long> lastExecuted = synchronizedList(new ArrayList<>(1));
        lastExecuted.add(null);

        Duration _1000millis = Duration.ofMillis(1000);

        Consumer<String> print = System.out::println;

        Consumer<String> throttledPrint = throttle(print, _1000millis);


        for (int i = 0; i < 10; i++) {
            System.out.println("times : " + i);
            Thread.sleep(500L);
            throttledPrint.accept("function executed.");
        }

    }

}
