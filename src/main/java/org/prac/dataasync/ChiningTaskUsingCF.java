package org.prac.dataasync;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

public class ChiningTaskUsingCF {

    record Quotation(String name, int duration) {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        Random rand = new Random();
        // callable can be replaced by supplier only difference is supplier can not handle exception
        // so need to use it in try-catch
        Supplier<Quotation> firstQ = () -> {
            try {
                //System.out.println("Stating -- pausing thread 1");
                Thread.sleep(rand.nextInt(80, 120));
                //  System.out.println("Resuming thread 1");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("Q_one ", rand.nextInt(100, 200));
        };
        Supplier<Quotation> secondQ = () -> {
            try {
                // System.out.println("Stating -- pausing thread 2");
                Thread.sleep(rand.nextInt(80, 120));
                //  System.out.println("Resuming thread 2");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("Q_two ", rand.nextInt(100, 200));
        };
        Supplier<Quotation> thirdQ = () -> {
            try {
                //System.out.println("Stating -- pausing thread 3");
                Thread.sleep(rand.nextInt(80, 120));
                // System.out.println("Resuming thread 3");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("Q_three ", rand.nextInt(100, 200));
        };

        var taskList = List.of(firstQ, secondQ, thirdQ);
        Instant start = Instant.now();

        List<CompletableFuture<Quotation>> listOfFuture = new ArrayList<>();
        for (Supplier<Quotation> currentSupplier : taskList) {
            CompletableFuture<Quotation> future = CompletableFuture.supplyAsync(currentSupplier);
            listOfFuture.add(future);
        }


        Collection<Quotation> listOfQuotation = new ConcurrentLinkedDeque<>();
        List<CompletableFuture<Void>> voids = new ArrayList<>();
        for (CompletableFuture<Quotation> quotationFuture : listOfFuture) {

            quotationFuture.thenAccept(System.out::println);
            CompletableFuture<Void> addToListAndEnsureFinish = quotationFuture
                    .thenAccept(q -> listOfQuotation.add(q));
            voids.add(addToListAndEnsureFinish);
            /**
             *  if we do like this we can't make sure that quation is added to the
             *  `listOfQuotation`
             *
             *  CompletableFuture<Void> addToListAndEnsureFinish =
             *  quotationFuture.thenAccept(System.out::println);
             *  voids.add(addToListAndEnsureFinish);
             *  quotationFuture.thenAccept(q -> listOfQuotation.add(q));
             *
             * Because:
             * we are making sure that voids(execution of future by join on next for loop¬)
             * are finished but not this `add to list` is finished.
             * */
        }
        for (CompletableFuture<Void> finishIt : voids) {
            finishIt.join(); // we need to finish the starting task before exiting main thread
            // this will avoid using sleep(X) to pause main thread as sleep(X) is a bad-pattern.
        }
        Quotation fastestQ = listOfQuotation.stream()
                .min(Comparator.comparing(Quotation::duration))
                .orElseThrow();
        Instant end = Instant.now();

        Duration duration = Duration.between(start, end);
        System.out.println("Best quotation [SYNC ] = " + fastestQ +
                " (" + duration.toMillis() + "ms)");
    }
}
