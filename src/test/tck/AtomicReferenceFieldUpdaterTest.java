/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 * Other contributors include Andrew Wright, Jeffrey Hayes,
 * Pat Fisher, Mike Judd.
 */

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AtomicReferenceFieldUpdaterTest extends JSR166TestCase {
    volatile Item x = null;
    protected volatile Item protectedField;
    private volatile Item privateField;
    Object z;
    Item w;
    volatile int i;

    public static void main(String[] args) {
        main(suite(), args);
    }
    public static Test suite() {
        return new TestSuite(AtomicReferenceFieldUpdaterTest.class);
    }

    static AtomicReferenceFieldUpdater<AtomicReferenceFieldUpdaterTest, Item> updaterFor(String fieldName) {
        return AtomicReferenceFieldUpdater.newUpdater
            (AtomicReferenceFieldUpdaterTest.class, Item.class, fieldName);
    }

    /**
     * Construction with non-existent field throws RuntimeException
     */
    public void testConstructor() {
        try {
            updaterFor("y");
            shouldThrow();
        } catch (RuntimeException success) {
            assertNotNull(success.getCause());
        }
    }

    /**
     * construction with field not of given type throws ClassCastException
     */
    public void testConstructor2() {
        try {
            updaterFor("z");
            shouldThrow();
        } catch (ClassCastException success) {}
    }

    /**
     * Constructor with non-volatile field throws IllegalArgumentException
     */
    public void testConstructor3() {
        try {
            updaterFor("w");
            shouldThrow();
        } catch (IllegalArgumentException success) {}
    }

    /**
     * Constructor with non-reference field throws ClassCastException
     */
    public void testConstructor4() {
        try {
            updaterFor("i");
            shouldThrow();
        } catch (ClassCastException success) {}
    }

    /**
     * construction using private field from subclass throws RuntimeException
     */
    public void testPrivateFieldInSubclass() {
        new NonNestmates.AtomicReferenceFieldUpdaterTestSubclass()
            .checkPrivateAccess();
    }

    /**
     * construction from unrelated class; package access is allowed,
     * private access is not
     */
    public void testUnrelatedClassAccess() {
        new NonNestmates().checkPackageAccess(this);
        new NonNestmates().checkPrivateAccess(this);
    }

    /**
     * get returns the last value set or assigned
     */
    public void testGetSet() {
        AtomicReferenceFieldUpdater<AtomicReferenceFieldUpdaterTest, Item> a;
        a = updaterFor("x");
        x = one;
        assertSame(one, a.get(this));
        a.set(this, two);
        assertSame(two, a.get(this));
        a.set(this, minusThree);
        assertSame(minusThree, a.get(this));
    }

    /**
     * get returns the last value lazySet by same thread
     */
    public void testGetLazySet() {
        AtomicReferenceFieldUpdater<AtomicReferenceFieldUpdaterTest, Item> a;
        a = updaterFor("x");
        x = one;
        assertSame(one, a.get(this));
        a.lazySet(this, two);
        assertSame(two, a.get(this));
        a.lazySet(this, minusThree);
        assertSame(minusThree, a.get(this));
    }

    /**
     * compareAndSet succeeds in changing value if same as expected else fails
     */
    public void testCompareAndSet() {
        AtomicReferenceFieldUpdater<AtomicReferenceFieldUpdaterTest, Item> a;
        a = updaterFor("x");
        x = one;
        assertTrue(a.compareAndSet(this, one, two));
        assertTrue(a.compareAndSet(this, two, minusFour));
        assertSame(minusFour, a.get(this));
        assertFalse(a.compareAndSet(this, minusFive, seven));
        assertNotSame(seven, a.get(this));
        assertTrue(a.compareAndSet(this, minusFour, seven));
        assertSame(seven, a.get(this));
    }

    /**
     * compareAndSet succeeds in changing protected field value if
     * same as expected else fails
     */
    public void testCompareAndSetProtectedInSubclass() {
        new NonNestmates.AtomicReferenceFieldUpdaterTestSubclass()
            .checkCompareAndSetProtectedSub();
    }

    /**
     * compareAndSet in one thread enables another waiting for value
     * to succeed
     */
    public void testCompareAndSetInMultipleThreads() throws Exception {
        x = one;
        final AtomicReferenceFieldUpdater<AtomicReferenceFieldUpdaterTest, Item> a;
        a = updaterFor("x");

        Thread t = new Thread(new CheckedRunnable() {
            public void realRun() {
                while (!a.compareAndSet(AtomicReferenceFieldUpdaterTest.this, two, three))
                    Thread.yield();
            }});

        t.start();
        assertTrue(a.compareAndSet(this, one, two));
        t.join(LONG_DELAY_MS);
        assertFalse(t.isAlive());
        assertSame(three, a.get(this));
    }

    /**
     * repeated weakCompareAndSet succeeds in changing value when same as expected
     */
    public void testWeakCompareAndSet() {
        AtomicReferenceFieldUpdater<AtomicReferenceFieldUpdaterTest, Item> a;
        a = updaterFor("x");
        x = one;
        do {} while (!a.weakCompareAndSet(this, one, two));
        do {} while (!a.weakCompareAndSet(this, two, minusFour));
        assertSame(minusFour, a.get(this));
        do {} while (!a.weakCompareAndSet(this, minusFour, seven));
        assertSame(seven, a.get(this));
    }

    /**
     * getAndSet returns previous value and sets to given value
     */
    public void testGetAndSet() {
        AtomicReferenceFieldUpdater<AtomicReferenceFieldUpdaterTest, Item> a;
        a = updaterFor("x");
        x = one;
        assertSame(one, a.getAndSet(this, zero));
        assertSame(zero, a.getAndSet(this, minusTen));
        assertSame(minusTen, a.getAndSet(this, one));
    }

}
