package com.yaki;

import java.util.concurrent.ThreadLocalRandom;

public class Part3 {
    public static void main(String[] args) throws InterruptedException {
        final long TOTAL_POINTS = 100_000_000;
        int[] threadCounts = {1, 2, 4, 8, 16, 32};

        long baselineTime = 0;

        System.out.printf("%-10s | %-12s | %-20s | %-10s%n", "Threads", "Runtime(ms)", "Speedup", "Efficiency");
        System.out.println("-----------------------------------------------------------------------");

        for (int t : threadCounts) {
            long startTime = System.currentTimeMillis();
            long totalHits = runMonteCarlo(TOTAL_POINTS, t);
            long endTime = System.currentTimeMillis();
            long runtime = endTime - startTime;

            if (t == 1) {
                baselineTime = runtime;
            }

            double speedup = (double) baselineTime / runtime;
            double efficiency = (speedup / t) * 100;

            System.out.printf("%-10d | %-12d | %-20.2fx | %-10.2f%%%n", t, runtime, speedup, efficiency);
        }
    }

    private static long runMonteCarlo(long totalPoints, int numThreads) throws InterruptedException {
        Thread[] threads = new Thread[numThreads];
        long[] partialHits = new long[numThreads];
        long pointsPerThread = totalPoints / numThreads;

        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                long localHits = 0;
                ThreadLocalRandom random = ThreadLocalRandom.current();

                for (long j = 0; j < pointsPerThread; j++) {
                    double x = random.nextDouble();
                    double y = random.nextDouble();

                    if (x * x + y * y <= 1.0) {
                        localHits++;
                    }
                }
                partialHits[threadIndex] = localHits;
            });
            threads[i].start();
        }

        long totalHits = 0;
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
            totalHits += partialHits[i];
        }

        return totalHits;
    }
}