package org.prac.dataasync;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class UseCompatibleFuture {
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
                System.out.println("Stating -- pausing thread 1");
                Thread.sleep(rand.nextInt(80, 120));
                System.out.println("Resuming thread 1");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("Q_one ", rand.nextInt(100, 200));
        };
        Supplier<Quotation> secondQ = () -> {
            try {
                System.out.println("Stating -- pausing thread 2");
                Thread.sleep(rand.nextInt(80, 120));
                System.out.println("Resuming thread 2");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("Q_two ", rand.nextInt(100, 200));
        };
        Supplier<Quotation> thirdQ = () -> {
            try {
                System.out.println("Stating -- pausing thread 3");
                Thread.sleep(rand.nextInt(80, 120));
                System.out.println("Resuming thread 3");
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
        List<Quotation> listOfQuotation = new ArrayList<>();
        for (CompletableFuture<Quotation> quotationFuture : listOfFuture) {
            // no try catch diff with .get()
            System.out.println("joining start ");
            Quotation q = quotationFuture.join();
            System.out.println("joining end ");
            listOfQuotation.add(q);
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