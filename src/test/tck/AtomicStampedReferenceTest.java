/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 * Other contributors include Andrew Wright, Jeffrey Hayes,
 * Pat Fisher, Mike Judd.
 */

import java.util.concurrent.atomic.AtomicStampedReference;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AtomicStampedReferenceTest extends JSR166TestCase {
    public static void main(String[] args) {
        main(suite(), args);
    }
    public static Test suite() {
        return new TestSuite(AtomicStampedReferenceTest.class);
    }

    /**
     * constructor initializes to given reference and stamp
     */
    public void testConstructor() {
        AtomicStampedReference<Item> ai = new AtomicStampedReference<>(one, 0);
        assertSame(one, ai.getReference());
        assertEquals(0, ai.getStamp());
        AtomicStampedReference<Item> a2 = new AtomicStampedReference<>(null, 1);
        assertNull(a2.getReference());
        assertEquals(1, a2.getStamp());
    }

    /**
     * get returns the last values of reference and stamp set
     */
    public void testGetSet() {
        int[] mark = new int[1];
        AtomicStampedReference<Item> ai = new AtomicStampedReference<>(one, 0);
        assertSame(one, ai.getReference());
        assertEquals(0, ai.getStamp());
        assertSame(one, ai.get(mark));
        assertEquals(0, mark[0]);
        ai.set(two, 0);
        assertSame(two, ai.getReference());
        assertEquals(0, ai.getStamp());
        assertSame(two, ai.get(mark));
        assertEquals(0, mark[0]);
        ai.set(one, 1);
        assertSame(one, ai.getReference());
        assertEquals(1, ai.getStamp());
        assertSame(one, ai.get(mark));
        assertEquals(1, mark[0]);
    }

    /**
     * attemptStamp succeeds in single thread
     */
    public void testAttemptStamp() {
        int[] mark = new int[1];
        AtomicStampedReference<Item> ai = new AtomicStampedReference<>(one, 0);
        assertEquals(0, ai.getStamp());
        assertTrue(ai.attemptStamp(one, 1));
        assertEquals(1, ai.getStamp());
        assertSame(one, ai.get(mark));
        assertEquals(1, mark[0]);
    }

    /**
     * compareAndSet succeeds in changing values if equal to expected reference
     * and stamp else fails
     */
    public void testCompareAndSet() {
        int[] mark = new int[1];
        AtomicStampedReference<Item> ai = new AtomicStampedReference<>(one, 0);
        assertSame(one, ai.get(mark));
        assertEquals(0, ai.getStamp());
        assertEquals(0, mark[0]);

        assertTrue(ai.compareAndSet(one, two, 0, 0));
        assertSame(two, ai.get(mark));
        assertEquals(0, mark[0]);

        assertTrue(ai.compareAndSet(two, minusThree, 0, 1));
        assertSame(minusThree, ai.get(mark));
        assertEquals(1, mark[0]);

        assertFalse(ai.compareAndSet(two, minusThree, 1, 1));
        assertSame(minusThree, ai.get(mark));
        assertEquals(1, mark[0]);
    }

    /**
     * compareAndSet in one thread enables another waiting for reference value
     * to succeed
     */
    public void testCompareAndSetInMultipleThreads() throws Exception {
        final AtomicStampedReference<Item> ai = new AtomicStampedReference<>(one, 0);
        Thread t = new Thread(new CheckedRunnable() {
            public void realRun() {
                while (!ai.compareAndSet(two, three, 0, 0))
                    Thread.yield();
            }});

        t.start();
        assertTrue(ai.compareAndSet(one, two, 0, 0));
        t.join(LONG_DELAY_MS);
        assertFalse(t.isAlive());
        assertSame(three, ai.getReference());
        assertEquals(0, ai.getStamp());
    }

    /**
     * compareAndSet in one thread enables another waiting for stamp value
     * to succeed
     */
    public void testCompareAndSetInMultipleThreads2() throws Exception {
        final AtomicStampedReference<Item> ai = new AtomicStampedReference<>(one, 0);
        Thread t = new Thread(new CheckedRunnable() {
            public void realRun() {
                while (!ai.compareAndSet(one, one, 1, 2))
                    Thread.yield();
            }});

        t.start();
        assertTrue(ai.compareAndSet(one, one, 0, 1));
        t.join(LONG_DELAY_MS);
        assertFalse(t.isAlive());
        assertSame(one, ai.getReference());
        assertEquals(2, ai.getStamp());
    }

    /**
     * repeated weakCompareAndSet succeeds in changing values when equal
     * to expected
     */
    public void testWeakCompareAndSet() {
        int[] mark = new int[1];
        AtomicStampedReference<Item> ai = new AtomicStampedReference<>(one, 0);
        assertSame(one, ai.get(mark));
        assertEquals(0, ai.getStamp());
        assertEquals(0, mark[0]);

        do {} while (!ai.weakCompareAndSet(one, two, 0, 0));
        assertSame(two, ai.get(mark));
        assertEquals(0, mark[0]);

        do {} while (!ai.weakCompareAndSet(two, minusThree, 0, 1));
        assertSame(minusThree, ai.get(mark));
        assertEquals(1, mark[0]);
    }

}
