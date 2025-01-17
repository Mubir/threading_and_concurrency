package org.prac.dataasync;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorServiceTasks {
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
        var executor = Executors.newFixedThreadPool(4);
        Instant start = Instant.now();
        List<Future<Quotation>> listOfFuture = new ArrayList<>();
        for (Callable<Quotation> task : taskList) {
            Future<Quotation> future = executor.submit(task);
            listOfFuture.add(future);
        }

        List<Quotation> listOfQuotation = new ArrayList<>();
        for (Future<Quotation> futureOfQuotation : listOfFuture) {
            Quotation quotation = openFuture(futureOfQuotation);
            listOfQuotation.add(quotation);
        }

        Quotation fastestQ = listOfQuotation.stream()
                .min(Comparator.comparing(Quotation::duration))
                .orElseThrow();
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Best quotation [SYNC ] = " + fastestQ +
                " (" + duration.toMillis() + "ms)");

        executor.shutdown();
    }

    private static Quotation openFuture(Future<Quotation> future) {
        try {
            return future.get(); // get need to be handled and its blocking.
            // This blocks the current thread
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
