/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AtomicReference9Test extends JSR166TestCase {
    public static void main(String[] args) {
        main(suite(), args);
    }
    public static Test suite() {
        return new TestSuite(AtomicReference9Test.class);
    }

    /**
     * getPlain returns the last value set
     */
    public void testGetPlainSet() {
        AtomicReference<Item> ai = new AtomicReference<>(one);
        mustEqual(one, ai.getPlain());
        ai.set(two);
        mustEqual(two, ai.getPlain());
        ai.set(minusThree);
        mustEqual(minusThree, ai.getPlain());
    }

    /**
     * getOpaque returns the last value set
     */
    public void testGetOpaqueSet() {
        AtomicReference<Item> ai = new AtomicReference<>(one);
        mustEqual(one, ai.getOpaque());
        ai.set(two);
        mustEqual(two, ai.getOpaque());
        ai.set(minusThree);
        mustEqual(minusThree, ai.getOpaque());
    }

    /**
     * getAcquire returns the last value set
     */
    public void testGetAcquireSet() {
        AtomicReference<Item> ai = new AtomicReference<>(one);
        mustEqual(one, ai.getAcquire());
        ai.set(two);
        mustEqual(two, ai.getAcquire());
        ai.set(minusThree);
        mustEqual(minusThree, ai.getAcquire());
    }

    /**
     * get returns the last value setPlain
     */
    public void testGetSetPlain() {
        AtomicReference<Item> ai = new AtomicReference<>(one);
        mustEqual(one, ai.get());
        ai.setPlain(two);
        mustEqual(two, ai.get());
        ai.setPlain(minusThree);
        mustEqual(minusThree, ai.get());
    }

    /**
     * get returns the last value setOpaque
     */
    public void testGetSetOpaque() {
        AtomicReference<Item> ai = new AtomicReference<>(one);
        mustEqual(one, ai.get());
        ai.setOpaque(two);
        mustEqual(two, ai.get());
        ai.setOpaque(minusThree);
        mustEqual(minusThree, ai.get());
    }

    /**
     * get returns the last value setRelease
     */
    public void testGetSetRelease() {
        AtomicReference<Item> ai = new AtomicReference<>(one);
        mustEqual(one, ai.get());
        ai.setRelease(two);
        mustEqual(two, ai.get());
        ai.setRelease(minusThree);
        mustEqual(minusThree, ai.get());
    }

    /**
     * compareAndExchange succeeds in changing value if equal to
     * expected else fails
     */
    public void testCompareAndExchange() {
        AtomicReference<Item> ai = new AtomicReference<>(one);
        mustEqual(one, ai.compareAndExchange(one, two));
        mustEqual(two, ai.compareAndExchange(two, minusFour));
        mustEqual(minusFour, ai.get());
        mustEqual(minusFour, ai.compareAndExchange(minusFive, seven));
        mustEqual(minusFour, ai.get());
        mustEqual(minusFour, ai.compareAndExchange(minusFour, seven));
        mustEqual(seven, ai.get());
    }

    /**
     * compareAndExchangeAcquire succeeds in changing value if equal to
     * expected else fails
     */
    public void testCompareAndExchangeAcquire() {
        AtomicReference<Item> ai = new AtomicReference<>(one);
        mustEqual(one, ai.compareAndExchangeAcquire(one, two));
        mustEqual(two, ai.compareAndExchangeAcquire(two, minusFour));
        mustEqual(minusFour, ai.get());
        mustEqual(minusFour, ai.compareAndExchangeAcquire(minusFive, seven));
        mustEqual(minusFour, ai.get());
        mustEqual(minusFour, ai.compareAndExchangeAcquire(minusFour, seven));
        mustEqual(seven, ai.get());
    }

    /**
     * compareAndExchangeRelease succeeds in changing value if equal to
     * expected else fails
     */
    public void testCompareAndExchangeRelease() {
        AtomicReference<Item> ai = new AtomicReference<>(one);
        mustEqual(one, ai.compareAndExchangeRelease(one, two));
        mustEqual(two, ai.compareAndExchangeRelease(two, minusFour));
        mustEqual(minusFour, ai.get());
        mustEqual(minusFour, ai.compareAndExchangeRelease(minusFive, seven));
        mustEqual(minusFour, ai.get());
        mustEqual(minusFour, ai.compareAndExchangeRelease(minusFour, seven));
        mustEqual(seven, ai.get());
    }

    /**
     * repeated weakCompareAndSetPlain succeeds in changing value when equal
     * to expected
     */
    public void testWeakCompareAndSetPlain() {
        AtomicReference<Item> ai = new AtomicReference<>(one);
        do {} while (!ai.weakCompareAndSetPlain(one, two));
        do {} while (!ai.weakCompareAndSetPlain(two, minusFour));
        mustEqual(minusFour, ai.get());
        do {} while (!ai.weakCompareAndSetPlain(minusFour, seven));
        mustEqual(seven, ai.get());
    }

    /**
     * repeated weakCompareAndSetVolatile succeeds in changing value when equal
     * to expected
     */
    public void testWeakCompareAndSetVolatile() {
        AtomicReference<Item> ai = new AtomicReference<>(one);
        do {} while (!ai.weakCompareAndSetVolatile(one, two));
        do {} while (!ai.weakCompareAndSetVolatile(two, minusFour));
        mustEqual(minusFour, ai.get());
        do {} while (!ai.weakCompareAndSetVolatile(minusFour, seven));
        mustEqual(seven, ai.get());
    }

    /**
     * repeated weakCompareAndSetAcquire succeeds in changing value when equal
     * to expected
     */
    public void testWeakCompareAndSetAcquire() {
        AtomicReference<Item> ai = new AtomicReference<>(one);
        do {} while (!ai.weakCompareAndSetAcquire(one, two));
        do {} while (!ai.weakCompareAndSetAcquire(two, minusFour));
        mustEqual(minusFour, ai.get());
        do {} while (!ai.weakCompareAndSetAcquire(minusFour, seven));
        mustEqual(seven, ai.get());
    }

    /**
     * repeated weakCompareAndSetRelease succeeds in changing value when equal
     * to expected
     */
    public void testWeakCompareAndSetRelease() {
        AtomicReference<Item> ai = new AtomicReference<>(one);
        do {} while (!ai.weakCompareAndSetRelease(one, two));
        do {} while (!ai.weakCompareAndSetRelease(two, minusFour));
        mustEqual(minusFour, ai.get());
        do {} while (!ai.weakCompareAndSetRelease(minusFour, seven));
        mustEqual(seven, ai.get());
    }

}
