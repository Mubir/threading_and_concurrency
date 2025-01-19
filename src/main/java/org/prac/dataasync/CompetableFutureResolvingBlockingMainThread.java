package org.prac.dataasync;

import java.util.concurrent.CompletableFuture;

public class CompetableFutureResolvingBlockingMainThread {
    public static void main(String[] args) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000); // Simulate long task
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return " Main_future  --> Result";
        });

        // Non-blocking - Register a callback to process the result when it's ready
        future.thenApply(result -> {
            System.out.println("Processing result: " + result);
            return result.toUpperCase();
        }).thenAccept(finalResult -> {
            System.out.println("Final Result: " + finalResult);
        });

        // Main thread continues running
        System.out.println("Main thread is free to do other things!");

        // Sleep to allow async task to complete
        try {
            Thread.sleep(6000); // Just to wait for all tasks to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
