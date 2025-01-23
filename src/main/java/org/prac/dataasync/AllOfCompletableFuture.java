package org.prac.dataasync;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AllOfCompletableFuture {

    record Quotation(String name, int duration) {
    }

    public static void main(String[] args) {
        Random rand = new Random();
        List<Supplier<Quotation>> tasks = mySupplier(rand);
        List<CompletableFuture<Quotation>> cFutures = new ArrayList<>();

        for (Supplier<Quotation> task : tasks) {
            CompletableFuture<Quotation> currFuture = CompletableFuture.supplyAsync(task);
            cFutures.add(currFuture);
        }

        CompletableFuture<Void> allCpFuture
                = CompletableFuture.allOf(cFutures.toArray(CompletableFuture[]::new));

        Quotation bestQ = allCpFuture.thenApply(
                v -> cFutures.stream()
                        .map(CompletableFuture::join)
                        .min(Comparator.comparing(quotation -> quotation.duration))
                        .orElseThrow()
        ).join();
        System.out.println("Ichiban:: " + bestQ);
    }

    public static List<Supplier<Quotation>> mySupplier(Random rand) {
        Supplier<Quotation> firstQ = () -> {
            try {
                Thread.sleep(rand.nextInt(80, 120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("Q_one ", rand.nextInt(100, 200));
        };
        Supplier<Quotation> secondQ = () -> {
            try {
                Thread.sleep(rand.nextInt(80, 120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("Q_two ", rand.nextInt(100, 200));
        };
        Supplier<Quotation> thirdQ = () -> {
            try {
                Thread.sleep(rand.nextInt(80, 120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("Q_three ", rand.nextInt(100, 200));
        };

        var taskList = List.of(firstQ, secondQ, thirdQ);
        return taskList;
    }
}
