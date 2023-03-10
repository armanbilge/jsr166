/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

/*
 * @test
 * @bug 4486658
 * @summary multiple threads using a single lock
 * @library /lib/testlibrary/
 */

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ThreadLocalRandom;
import jdk.testlibrary.Utils;

public final class SimpleReentrantLockLoops {
    static final long LONG_DELAY_MS = Utils.adjustTimeout(10_000);
    static final ExecutorService pool = Executors.newCachedThreadPool();
    static boolean print = false;
    static int iters = 100_000;

    public static void main(String[] args) throws Exception {
        int maxThreads = 5;
        if (args.length > 0)
            maxThreads = Integer.parseInt(args[0]);

        print = true;

        int reps = 2;
        for (int i = 1; i <= maxThreads; i += (i+1) >>> 1) {
            int n = reps;
            if (reps > 1) --reps;
            while (n-- > 0) {
                System.out.print("Threads: " + i);
                new ReentrantLockLoop(i).test();
            }
        }
        pool.shutdown();
        if (! pool.awaitTermination(LONG_DELAY_MS, MILLISECONDS))
            throw new Error();
    }

    static final class ReentrantLockLoop implements Runnable {
        private int v = ThreadLocalRandom.current().nextInt();
        private volatile int result = 17;
        private final ReentrantLock lock = new ReentrantLock();
        private final LoopHelpers.BarrierTimer timer = new LoopHelpers.BarrierTimer();
        private final CyclicBarrier barrier;
        private final int nthreads;
        ReentrantLockLoop(int nthreads) {
            this.nthreads = nthreads;
            barrier = new CyclicBarrier(nthreads+1, timer);
        }

        final void test() throws Exception {
            for (int i = 0; i < nthreads; ++i)
                pool.execute(this);
            barrier.await();
            barrier.await();
            if (print) {
                long time = timer.getTime();
                long tpi = time / ((long)iters * nthreads);
                System.out.print("\t" + LoopHelpers.rightJustify(tpi) + " ns per lock");
                double secs = (double)time / 1000000000.0;
                System.out.println("\t " + secs + "s run time");
            }

            int r = result;
            if (r == 0) // avoid overoptimization
                System.out.println("useless result: " + r);
        }

        public final void run() {
            try {
                barrier.await();
                int sum = v;
                int x = 0;
                int n = iters;
                do {
                    lock.lock();
                    try {
                        if ((n & 255) == 0)
                            v = x = LoopHelpers.compute2(LoopHelpers.compute1(v));
                        else
                            v = x += ~(v - n);
                    }
                    finally {
                        lock.unlock();
                    }
                    // Once in a while, do something more expensive
                    if ((~n & 255) == 0) {
                        sum += LoopHelpers.compute1(LoopHelpers.compute2(x));
                    }
                    else
                        sum += sum ^ x;
                } while (n-- > 0);
                barrier.await();
                result += sum;
            }
            catch (Exception ex) {
                return;
            }
        }
    }

}
