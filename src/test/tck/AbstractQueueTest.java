/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 * Other contributors include Andrew Wright, Jeffrey Hayes,
 * Pat Fisher, Mike Judd.
 */

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AbstractQueueTest extends JSR166TestCase {
    public static void main(String[] args) {
        main(suite(), args);
    }
    public static Test suite() {
        return new TestSuite(AbstractQueueTest.class);
    }

    static class Succeed extends AbstractQueue<Item> {
        public boolean offer(Item x) {
            if (x == null) throw new NullPointerException();
            return true;
        }
        public Item peek() { return one; }
        public Item poll() { return one; }
        public int size() { return 0; }
        public Iterator<Item> iterator() { return null; } // not needed
    }

    static class Fail extends AbstractQueue<Item> {
        public boolean offer(Item x) {
            if (x == null) throw new NullPointerException();
            return false;
        }
        public Item peek() { return null; }
        public Item poll() { return null; }
        public int size() { return 0; }
        public Iterator<Item> iterator() { return null; } // not needed
    }

    /**
     * add returns true if offer succeeds
     */
    public void testAddS() {
        Succeed q = new Succeed();
        assertTrue(q.add(two));
    }

    /**
     * add throws IllegalStateException if offer fails
     */
    public void testAddF() {
        Fail q = new Fail();
        try {
            q.add(one);
            shouldThrow();
        } catch (IllegalStateException success) {}
    }

    /**
     * add throws NullPointerException if offer does
     */
    public void testAddNPE() {
        Succeed q = new Succeed();
        try {
            q.add(null);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * remove returns normally if poll succeeds
     */
    public void testRemoveS() {
        Succeed q = new Succeed();
        assertSame(one, q.remove());
    }

    /**
     * remove throws NSEE if poll returns null
     */
    public void testRemoveF() {
        Fail q = new Fail();
        try {
            q.remove();
            shouldThrow();
        } catch (NoSuchElementException success) {}
    }

    /**
     * element returns normally if peek succeeds
     */
    public void testElementS() {
        Succeed q = new Succeed();
        assertSame(one, q.element());
    }

    /**
     * element throws NSEE if peek returns null
     */
    public void testElementF() {
        Fail q = new Fail();
        try {
            q.element();
            shouldThrow();
        } catch (NoSuchElementException success) {}
    }

    /**
     * addAll(null) throws NullPointerException
     */
    public void testAddAll1() {
        Succeed q = new Succeed();
        try {
            q.addAll(null);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * addAll(this) throws IllegalArgumentException
     */
    public void testAddAllSelf() {
        Succeed q = new Succeed();
        try {
            q.addAll(q);
            shouldThrow();
        } catch (IllegalArgumentException success) {}
    }

    /**
     * addAll of a collection with null elements throws NullPointerException
     */
    public void testAddAll2() {
        Succeed q = new Succeed();
        Item[] items = new Item[SIZE];
        try {
            q.addAll(Arrays.asList(items));
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * addAll of a collection with any null elements throws NPE after
     * possibly adding some elements
     */
    public void testAddAll3() {
        Succeed q = new Succeed();
        Item[] items = new Item[SIZE];
        for (int i = 0; i < SIZE - 1; ++i)
            items[i] = itemFor(i);
        try {
            q.addAll(Arrays.asList(items));
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * addAll throws IllegalStateException if an add fails
     */
    public void testAddAll4() {
        Fail q = new Fail();
        Item[] items = seqItems(SIZE);
        try {
            q.addAll(Arrays.asList(items));
            shouldThrow();
        } catch (IllegalStateException success) {}
    }

}
