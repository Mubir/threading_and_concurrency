package org.prac.dataasync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Future.get() or join():
 * Blocking: Stops the current thread until the task completes.
 * The main thread is paused and cannot do anything else.
 */
public class FutureBlockingMainThread {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Submit a task that takes 5 seconds to complete
        Future<String> future = executor.submit(() -> {
            Thread.sleep(5000);
            return "Result";
        });

        // Call get() in the main thread
        System.out.println("Waiting for result...");
        String result = future.get(); // BLOCKS here until the task finishes
        System.out.println("Result: " + result);

        executor.shutdown();
    }
}
