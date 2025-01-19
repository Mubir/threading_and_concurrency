package org.prac.dataasync;

import java.util.concurrent.CompletableFuture;

/**
 * When you use methods like thenApply(), thenAccept(), or thenRun() on a CompletableFuture,
 * they are non-blocking. Instead of waiting for the result, these methods register a callback
 * to be executed asynchronously once the task is complete. The current thread
 * (e.g., the main thread) continues running and is not blocked.
 * <p>
 * How does this resolve the blocking issue?
 * Instead of calling get() or join(), which stops the current thread, we register callbacks
 * like thenApply() or thenAccept(). These methods attach a continuation to the CompletableFuture.
 * The main thread does not stop and can continue executing other tasks, improving responsiveness
 * and performance.
 * When the task completes in the background (on another thread), the registered callbacks
 * are executed asynchronously.
 */
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
