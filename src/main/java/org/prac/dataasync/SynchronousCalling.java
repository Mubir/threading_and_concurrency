package org.prac.dataasync;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class SynchronousCalling {
    record Quotation(String name, int duration) {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        Random rand = new Random();

        Callable<Quotation> firstQ = () -> {
            Thread.sleep(rand.nextInt(80, 120));
            return new Quotation("Q_one ", rand.nextInt(100, 200));
        };
        Callable<Quotation> secondQ = () -> {
            Thread.sleep(rand.nextInt(80, 120));
            return new Quotation("Q_two ", rand.nextInt(100, 200));
        };
        Callable<Quotation> thirdQ = () -> {
            Thread.sleep(rand.nextInt(80, 120));
            return new Quotation("Q_three ", rand.nextInt(100, 200));
        };

        var taskList = List.of(firstQ, secondQ, thirdQ);
        Instant start = Instant.now();
        Quotation fastestQ = taskList
                .stream()
                .map(SynchronousCalling::fetchQuotation)
                //.map(quotationCallable -> fetchQuotation(quotationCallable))
                //.min(Comparator.comparing(quotation -> quotation.duration))
                .min(Comparator.comparing(Quotation::duration))
                .orElseThrow();
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Best quotation [SYNC ] = " + fastestQ +
                " (" + duration.toMillis() + "ms)");
    }

    public static Quotation fetchQuotation(Callable<Quotation> task) {
        try {
            return task.call(); // .call() or .run() runs the callable or runnable in the same thread.
            // does not create new thread.so it's synchronous.
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
