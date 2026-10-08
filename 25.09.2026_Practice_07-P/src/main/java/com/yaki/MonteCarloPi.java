package com.yaki;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class MonteCarloPi {

    private static final long TOTAL_POINTS_PART1 = 50_000_000L;
    private static final long TOTAL_POINTS_PART3 = 100_000_000L;

    // --- Part 1: Race Condition ---
    private static volatile long sharedTotalHits = 0;

    // --- Part 2: Atomic/Synchronized ---
    private static final AtomicLong atomicTotalHits = new AtomicLong(0);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== PART 1: The Phantom Bug (Data Race) ===");
        runPart1();

        System.out.println("\n=== PART 2: The Synchronization Trap ===");
        runPart2();

        System.out.println("\n=== PART 3: OpenMP-Style Reduction Benchmarks ===");
        runPart3();
    }

    private static void runPart1() throws InterruptedException {
        for (int run = 1; run <= 5; run++) {
            sharedTotalHits = 0;
            int numThreads = 4;
            long pointsPerThread = TOTAL_POINTS_PART1 / numThreads;
            Thread[] threads = new Thread[numThreads];

            for (int i = 0; i < numThreads; i++) {
                threads[i] = new Thread(() -> {
                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    for (long j = 0; j < pointsPerThread; j++) {
                        double x = random.nextDouble();
                        double y = random.nextDouble();
                        if (x * x + y * y <= 1.0) {
                            sharedTotalHits++; // UNSAFE DATA RACE
                        }
                    }
                });
                threads[i].start();
            }

            for (Thread t : threads) t.join();

            double pi = 4.0 * sharedTotalHits / TOTAL_POINTS_PART1;
            System.out.printf("Run %d: pi = %.5f (Hits: %d / %d)%n", run, pi, sharedTotalHits, TOTAL_POINTS_PART1);
        }
    }

    private static void runPart2() throws InterruptedException {
        int numThreads = 4;
        long pointsPerThread = TOTAL_POINTS_PART1 / numThreads;

        // Single-threaded baseline
        long startTime = System.currentTimeMillis();
        long singleHits = 0;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (long i = 0; i < TOTAL_POINTS_PART1; i++) {
            double x = random.nextDouble();
            double y = random.nextDouble();
            if (x * x + y * y <= 1.0) {
                singleHits++;
            }
        }
        long singleDuration = System.currentTimeMillis() - startTime;
        double singlePi = 4.0 * singleHits / TOTAL_POINTS_PART1;
        System.out.printf("Single Thread: pi = %.5f, Time = %d ms%n", singlePi, singleDuration);

        // Atomic multi-threaded
        atomicTotalHits.set(0);
        startTime = System.currentTimeMillis();
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                ThreadLocalRandom rand = ThreadLocalRandom.current();
                for (long j = 0; j < pointsPerThread; j++) {
                    double x = rand.nextDouble();
                    double y = rand.nextDouble();
                    if (x * x + y * y <= 1.0) {
                        atomicTotalHits.incrementAndGet(); // SYNCHRONIZED LOCK
                    }
                }
            });
            threads[i].start();
        }
        for (Thread t : threads) t.join();
        long atomicDuration = System.currentTimeMillis() - startTime;
        double atomicPi = 4.0 * atomicTotalHits.get() / TOTAL_POINTS_PART1;
        System.out.printf("Atomic 4-Threads: pi = %.5f, Time = %d ms%n", atomicPi, atomicDuration);
    }

    private static void runPart3() throws InterruptedException {
        int[] threadCounts = {1, 2, 4, 8, 16, 32};
        long t1Duration = 0;

        for (int numThreads : threadCounts) {
            long pointsPerThread = TOTAL_POINTS_PART3 / numThreads;
            long[] localHits = new long[numThreads];
            Thread[] threads = new Thread[numThreads];

            long startTime = System.currentTimeMillis();
            for (int i = 0; i < numThreads; i++) {
                final int threadId = i;
                threads[i] = new Thread(() -> {
                    long hits = 0;
                    ThreadLocalRandom rand = ThreadLocalRandom.current();
                    for (long j = 0; j < pointsPerThread; j++) {
                        double x = rand.nextDouble();
                        double y = rand.nextDouble();
                        if (x * x + y * y <= 1.0) {
                            hits++;
                        }
                    }
                    localHits[threadId] = hits; // Write only once per thread
                });
                threads[i].start();
            }

            for (Thread t : threads) t.join();

            long totalHits = 0;
            for (long h : localHits) totalHits += h;

            long duration = System.currentTimeMillis() - startTime;
            if (numThreads == 1) t1Duration = duration;

            double speedup = (double) t1Duration / duration;
            double efficiency = (speedup / numThreads) * 100;

            System.out.printf("Threads: %2d | Time: %5d ms | Speedup: %5.2fx | Efficiency: %6.1f%%%n",
                    numThreads, duration, speedup, efficiency);
        }
    }
}




//task1
def task1_worker_chunk(start, end, seed):
        """
    1. Iterate through index i from start to end (exclusive).
    2. Check if: ((i ^ seed) % 7) == 0
    3. If true, increment a local counter.
    4. Return the local counter value.
    """
local_count = 0
        for i in range(start, end):
        if ((i ^ seed) % 7) == 0:
local_count += 1

        return local_count


//task2
def solve_task2_balanced(num_items, seed, num_workers=4):
        """
    Cyclic / Interleaved load balancing distribution:
    Assign item 'i' to worker bucket: (i % num_workers).
    """
        # Create empty buckets for each worker
buckets = [[] for _ in range(num_workers)]

        # Interleave items across workers to balance quadratic work load
    for i in range(num_items):
buckets[i % num_workers].append(i)

total = 0
with ProcessPoolExecutor(max_workers=num_workers) as executor:
futures = [executor.submit(task2_worker_bucket, b, seed) for b in buckets]
        for f in futures:
total += f.result()

    return total