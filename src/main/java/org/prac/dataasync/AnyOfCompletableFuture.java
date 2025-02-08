package org.prac.dataasync;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AnyOfCompletableFuture {

    record Weather(String server, String message) {
    }

    public static void main(String[] args) {
        Random myRand = new Random();
        List<Supplier<Weather>> tasks = mySupplier(myRand);
        List<CompletableFuture<Weather>> cPFuture = new ArrayList<>();

        for (Supplier<Weather> task : tasks) {
            CompletableFuture<Weather> future = CompletableFuture.supplyAsync(task);
            cPFuture.add(future);
        }
        // anyOf takes CF of anyType.
        CompletableFuture<Object> anyOfFuture
                = CompletableFuture.anyOf(cPFuture.toArray(CompletableFuture[]::new));

        anyOfFuture.thenAccept(System.out::println).join();
    }

    public static List<Supplier<Weather>> mySupplier(Random rand) {
        Supplier<Weather> firstQ = () -> {
            try {
                Thread.sleep(rand.nextInt(80, 120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather(" Server-one ", "Rainy");
        };
        Supplier<Weather> secondQ = () -> {
            try {
                Thread.sleep(rand.nextInt(80, 120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather(" Server-two ", "Sunny");
        };
        Supplier<Weather> thirdQ = () -> {
            try {
                Thread.sleep(rand.nextInt(80, 120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather(" Server-three ", "Cloudy");
        };

        var taskList = List.of(firstQ, secondQ, thirdQ);
        return taskList;
    }
}
