package io.github.anjoismysign.aesthetic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Utility for processing collections of items with rate-limiting strategies.
 *
 * <p>This class provides two methods for handling rate-limited execution:
 * - {@link #processWithBatchLimit} limits the number of items processed in a given time window.
 * - {@link #processWithFixedDelay} processes items with a fixed delay between each request.
 */
public enum RateLimitedProcessor {
    INSTANCE;

    /**
     * Automatically chooses the appropriate processing strategy based on the number of items.
     * Uses batch limiting if item count is less than or equal to the maxRequestsPerCycle,
     * otherwise uses fixed delay processing.
     *
     * @param maxRequestsPerCycle Maximum number of requests per cycle
     * @param cycleTimeMillis     Time duration of a full cycle in milliseconds
     * @param items               Collection of items to process
     * @param itemProcessor       Callback to handle each item
     * @param progressCallback    Optional callback to report percentage progress
     * @param onComplete          Optional callback to execute upon completion
     * @param <T>                 Type of items in the collection
     * @return A CompletableFuture that completes when processing finishes
     */
    public <T> CompletableFuture<Void> processAuto(
            int maxRequestsPerCycle,
            long cycleTimeMillis,
            @NotNull Collection<T> items,
            @NotNull Consumer<T> itemProcessor,
            @Nullable Consumer<Integer> progressCallback,
            @Nullable Runnable onComplete) {

        int totalToProcess = items.size();

        if (totalToProcess <= maxRequestsPerCycle) {
            return processWithBatchLimit(
                    maxRequestsPerCycle,
                    cycleTimeMillis,
                    items,
                    itemProcessor,
                    progressCallback,
                    onComplete
            );
        }
        return processWithFixedDelay(
                maxRequestsPerCycle,
                cycleTimeMillis,
                items,
                itemProcessor,
                progressCallback,
                onComplete
        );
    }

    /**
     * Processes a collection of items with a limit on the number of requests per time cycle.
     * After reaching the maximum request count, it waits for the cycle duration before continuing.
     *
     * @param maxRequestsPerCycle Maximum number of requests before pausing
     * @param cycleTimeMillis     Duration of the cycle in milliseconds
     * @param items               Collection of items to process
     * @param itemProcessor       Callback to process each individual item
     * @param progressCallback    Callback to report progress as a percentage (0-100), can be null
     * @param onComplete          Callback to invoke after processing is complete, can be null
     * @param <T>                 Type of the items being processed
     * @return A CompletableFuture that completes when all items have been processed
     */
    public <T> CompletableFuture<Void> processWithBatchLimit(
            int maxRequestsPerCycle,
            long cycleTimeMillis,
            @NotNull Collection<T> items,
            @NotNull Consumer<T> itemProcessor,
            @Nullable Consumer<Integer> progressCallback,
            @Nullable Runnable onComplete) {

        return CompletableFuture.runAsync(() -> {
            int total = items.size();
            int requestCount = 0;
            int processedCount = 0;

            for (T item : items) {
                if (requestCount >= maxRequestsPerCycle) {
                    sleepSafely(cycleTimeMillis);
                    requestCount = 0;
                }

                itemProcessor.accept(item);
                requestCount++;
                processedCount++;

                int progress = (int) ((processedCount / (double) total) * 100);
                if (progressCallback != null)
                    progressCallback.accept(progress);
            }

            if (onComplete != null)
                onComplete.run();
        });
    }

    /**
     * Processes a collection of items by introducing a fixed delay between each item.
     * This method distributes the processing evenly over time.
     *
     * @param maxRequestsPerCycle Number of requests expected per cycle
     * @param cycleTimeMillis     Total duration of the cycle in milliseconds
     * @param items               Collection of items to process
     * @param itemProcessor       Callback to process each individual item
     * @param progressCallback    Callback to report progress as a percentage (0-100), can be null
     * @param onComplete          Callback to invoke after processing is complete, can be null
     * @param <T>                 Type of the items being processed
     * @return A CompletableFuture that completes when all items have been processed
     */
    public <T> CompletableFuture<Void> processWithFixedDelay(
            int maxRequestsPerCycle,
            long cycleTimeMillis,
            @NotNull Collection<T> items,
            @NotNull Consumer<T> itemProcessor,
            @Nullable Consumer<Integer> progressCallback,
            @Nullable Runnable onComplete) {
        int total = items.size();

        return CompletableFuture.runAsync(() -> {
            long delayPerRequest = cycleTimeMillis / maxRequestsPerCycle;
            int processedCount = 0;

            for (T item : items) {
                itemProcessor.accept(item);
                sleepSafely(delayPerRequest);
                processedCount++;

                int progress = (int) ((processedCount / (double) total) * 100);
                if (progressCallback != null)
                    progressCallback.accept(progress);
            }

            if (onComplete != null)
                onComplete.run();
        });
    }

    /**
     * Utility method to sleep without requiring exception handling in the main logic.
     *
     * @param milliseconds Time to sleep in milliseconds
     */
    private void sleepSafely(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted during sleep", exception);
        }
    }
}
