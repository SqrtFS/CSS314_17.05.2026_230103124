package com.yaki;

import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class ForkJoinLab1 {
    public static void main(String[] args) {
        int targetThreads = 8;
        System.out.println("Master thread starting. Spawning thread team of size: " + targetThreads);

        long startTime = System.nanoTime();

        ForkJoinPool customPool = new ForkJoinPool(targetThreads);
        try {
            customPool.submit(() -> {
                IntStream.range(0, targetThreads).parallel().forEach(idx -> {
                    long osTid = Thread.currentThread().getId();
                    String threadName = Thread.currentThread().getName();
                    boolean isMaster = idx == 0;


                    double dummySum = 0;
                    for (int i = 0; i < 10_000_000; i++) {
                        dummySum += Math.sqrt(i);
                    }

                    System.out.printf("[%s] Logical Rank: %d | Worker Thread: %s (OS ID: %d) | Result: %.2f%n",
                            isMaster ? "Master" : "Worker", idx, threadName, osTid, dummySum);
                });
            }).join();
        } finally {
            customPool.shutdown();
        }

        long endTime = System.nanoTime();

        double durationMs = (endTime - startTime) / 1_000_000.0;

        System.out.println("==========================================");
        System.out.printf("Threads (P): %d | Execution Time: %.3f ms%n", targetThreads, durationMs);
        System.out.println("==========================================");
        System.out.println("Parallel region closed. Execution returned to master thread.");
    }
}