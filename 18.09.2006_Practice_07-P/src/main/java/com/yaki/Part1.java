package com.yaki;

import java.util.concurrent.ThreadLocalRandom;

public class Part1 {
    static long totalHits = 0;

    public static void main(String[] args) throws InterruptedException {
        final long TOTAL_POINTS = 50_000_000;
        final int THREADS = 4;
        final long pointsPerThread = TOTAL_POINTS / THREADS;

        Thread[] threads = new Thread[THREADS];

        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(() -> {

                ThreadLocalRandom random = ThreadLocalRandom.current();

                for (long j = 0; j < pointsPerThread; j++) {
                    double x = random.nextDouble();
                    double y = random.nextDouble();


                    if (x * x + y * y <= 1.0) {
                        totalHits++;
                    }
                }
            });
            threads[i].start();
        }


        for (Thread t : threads) {
            t.join();
        }


        double pi = 4.0 * totalHits / TOTAL_POINTS;

        System.out.println("Total Hits: " + totalHits);
        System.out.println("Approximated Pi: " + pi);
    }
}

