/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */
/*
 * @test
 * @bug 6785442
 * @summary Benchmark that tries to GC-tenure head, followed by
 * many add/remove operations.
 * @run main GCRetention just-testing
 */

import static java.util.concurrent.TimeUnit.SECONDS;

import java.lang.ref.WeakReference;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Map;

public class GCRetention {
    boolean benchmarkMode;
    int count;

    /** No guarantees, but effective in practice. */
    static void forceFullGc() {
        CountDownLatch finalizeDone = new CountDownLatch(1);
        WeakReference<?> ref = new WeakReference<Object>(new Object() {
            protected void finalize() { finalizeDone.countDown(); }});
        try {
            for (int i = 0; i < 10; i++) {
                System.gc();
                if (finalizeDone.await(1L, SECONDS) && ref.get() == null) {
                    System.runFinalization(); // try to pick up stragglers
                    return;
                }
            }
        } catch (InterruptedException unexpected) {
            throw new AssertionError("unexpected InterruptedException");
        }
        throw new AssertionError("failed to do a \"full\" gc");
    }

    final Map<String,String> results = new ConcurrentHashMap<>();

    Collection<Queue<Boolean>> queues() {
        List<Queue<Boolean>> queues = new ArrayList<>();
        queues.add(new ConcurrentLinkedDeque<Boolean>());
        queues.add(new ConcurrentLinkedQueue<Boolean>());
        queues.add(new ArrayBlockingQueue<Boolean>(count, false));
        queues.add(new ArrayBlockingQueue<Boolean>(count, true));
        queues.add(new LinkedBlockingQueue<Boolean>());
        queues.add(new LinkedBlockingDeque<Boolean>());
        queues.add(new PriorityBlockingQueue<Boolean>());
        queues.add(new PriorityQueue<Boolean>());
        queues.add(new LinkedList<Boolean>());
        queues.add(new LinkedTransferQueue<Boolean>());

        // Following additional implementations are available from:
        // http://gee.cs.oswego.edu/dl/concurrency-interest/index.html
        // queues.add(new SynchronizedLinkedListQueue<Boolean>());

        // Avoid "first fast, second slow" benchmark effect.
        Collections.shuffle(queues);
        return queues;
    }

    void prettyPrintResults() {
        List<String> classNames = new ArrayList<>(results.keySet());
        Collections.sort(classNames);
        int maxClassNameLength = 0;
        int maxNanosLength = 0;
        for (String name : classNames) {
            if (maxClassNameLength < name.length())
                maxClassNameLength = name.length();
            if (maxNanosLength < results.get(name).length())
                maxNanosLength = results.get(name).length();
        }
        String format = String.format("%%%ds %%%ds nanos/item%%n",
                                      maxClassNameLength, maxNanosLength);
        for (String name : classNames)
            System.out.printf(format, name, results.get(name));
    }

    void test(String[] args) {
        benchmarkMode = ! (args.length > 0 && args[0].equals("just-testing"));
        count = benchmarkMode ? 1024 * 1024 : 30;

        // Warmup
        for (Queue<Boolean> queue : queues())
            test(queue);
        results.clear();
        for (Queue<Boolean> queue : queues())
            test(queue);

        prettyPrintResults();
    }

    void test(Queue<Boolean> q) {
        long t0 = System.nanoTime();
        for (int i = 0; i < count; i++)
            check(q.add(Boolean.TRUE));
        if (benchmarkMode) forceFullGc();
        // forceFullGc();
        Boolean x;
        while ((x = q.poll()) != null)
            equal(x, Boolean.TRUE);
        check(q.isEmpty());

        for (int i = 0; i < 10 * count; i++) {
            for (int k = 0; k < 3; k++)
                check(q.add(Boolean.TRUE));
            for (int k = 0; k < 3; k++)
                if (q.poll() != Boolean.TRUE)
                    fail();
        }
        check(q.isEmpty());

        String className = q.getClass().getSimpleName();
        long elapsed = System.nanoTime() - t0;
        int nanos = (int) ((double) elapsed / (10 * 3 * count));
        results.put(className, String.valueOf(nanos));
    }

    //--------------------- Infrastructure ---------------------------
    volatile int passed = 0, failed = 0;
    void pass() {passed++;}
    void fail() {failed++; Thread.dumpStack();}
    void fail(String msg) {System.err.println(msg); fail();}
    void unexpected(Throwable t) {failed++; t.printStackTrace();}
    void check(boolean cond) {if (cond) pass(); else fail();}
    void equal(Object x, Object y) {
        if (x == null ? y == null : x.equals(y)) pass();
        else fail(x + " not equal to " + y);}
    public static void main(String[] args) throws Throwable {
        new GCRetention().instanceMain(args);}
    public void instanceMain(String[] args) throws Throwable {
        try {test(args);} catch (Throwable t) {unexpected(t);}
        System.out.printf("%nPassed = %d, failed = %d%n%n", passed, failed);
        if (failed > 0) throw new AssertionError("Some tests failed");}
}