package com.yaki;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class Part2 {
    static AtomicLong totalHits = new AtomicLong(0);

    public static void main(String[] args) throws InterruptedException {
        final long TOTAL_POINTS = 50_000_000;
        final int THREADS = 4;
        final long pointsPerThread = TOTAL_POINTS / THREADS;

        long startTimeMulti = System.currentTimeMillis();
        Thread[] threads = new Thread[THREADS];

        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();

                for (long j = 0; j < pointsPerThread; j++) {
                    double x = random.nextDouble();
                    double y = random.nextDouble();

                    if (x * x + y * y <= 1.0) {
                        totalHits.incrementAndGet();
                    }
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }
        long endTimeMulti = System.currentTimeMillis();
        double piMulti = 4.0 * totalHits.get() / TOTAL_POINTS;

        long startTimeSingle = System.currentTimeMillis();
        long singleHits = 0;
        ThreadLocalRandom randomSingle = ThreadLocalRandom.current();

        for (long j = 0; j < TOTAL_POINTS; j++) {
            double x = randomSingle.nextDouble();
            double y = randomSingle.nextDouble();

            if (x * x + y * y <= 1.0) {
                singleHits++;
            }
        }
        long endTimeSingle = System.currentTimeMillis();
        double piSingle = 4.0 * singleHits / TOTAL_POINTS;

        System.out.println("--- Multi-threaded (AtomicLong) ---");
        System.out.println("Approximated Pi: " + piMulti);
        System.out.println("Execution Time: " + (endTimeMulti - startTimeMulti) + " ms\n");

        System.out.println("--- Single-threaded (Plain for-loop) ---");
        System.out.println("Approximated Pi: " + piSingle);
        System.out.println("Execution Time: " + (endTimeSingle - startTimeSingle) + " ms");
    }
}