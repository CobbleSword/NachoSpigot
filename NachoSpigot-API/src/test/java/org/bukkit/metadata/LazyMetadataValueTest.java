package org.bukkit.metadata;

import org.bukkit.plugin.TestPlugin;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LazyMetadataValueTest {
    private LazyMetadataValue subject;
    private final TestPlugin plugin = new TestPlugin("x");

    @Test
    public void testLazyInt() {
        int value = 10;
        subject = makeSimpleCallable(value);

        assertEquals(value, subject.value());
    }

    @Test
    public void testLazyDouble() {
        double value = 10.5;
        subject = makeSimpleCallable(value);

        assertEquals(value, (Double)subject.value(), 0.01);
    }

    @Test
    public void testLazyString() {
        String value = "TEN";
        subject = makeSimpleCallable(value);

        assertEquals(value, subject.value());
    }

    @Test
    public void testLazyBoolean() {
        boolean value = false;
        subject = makeSimpleCallable(value);

        assertEquals(value, subject.value());
    }

    @Test(expected=MetadataEvaluationException.class)
    public void testEvalException() {
        subject = new LazyMetadataValue(plugin, LazyMetadataValue.CacheStrategy.CACHE_AFTER_FIRST_EVAL, () -> {
            throw new RuntimeException("Gotcha!");
        });
        subject.value();
    }

    @Test
    public void testCacheStrategyCacheAfterFirstEval() {
        final Counter counter = new Counter();
        final int value = 10;
        subject = new LazyMetadataValue(plugin, LazyMetadataValue.CacheStrategy.CACHE_AFTER_FIRST_EVAL, () -> {
            counter.increment();
            return value;
        });

        subject.value();
        subject.value();
        assertEquals(value, subject.value());
        assertEquals(1, counter.value());

        subject.invalidate();
        subject.value();
        assertEquals(2, counter.value());
    }

    @Test
    public void testCacheStrategyNeverCache() {
        final Counter counter = new Counter();
        final int value = 10;
        subject = new LazyMetadataValue(plugin, LazyMetadataValue.CacheStrategy.NEVER_CACHE, () -> {
            counter.increment();
            return value;
        });

        subject.value();
        subject.value();
        assertEquals(value, subject.value());
        assertEquals(3, counter.value());
    }

    @Test
    public void testCacheStrategyEternally() {
        final Counter counter = new Counter();
        final int value = 10;
        subject = new LazyMetadataValue(plugin, LazyMetadataValue.CacheStrategy.CACHE_ETERNALLY, () -> {
            counter.increment();
            return value;
        });

        subject.value();
        subject.value();
        assertEquals(value, subject.value());
        assertEquals(1, counter.value());

        subject.invalidate();
        subject.value();
        assertEquals(value, subject.value());
        assertEquals(1, counter.value());
    }

    private LazyMetadataValue makeSimpleCallable(final Object value) {
        return new LazyMetadataValue(plugin, () -> value);
    }

    private static class Counter {
        private int c = 0;

        public void increment() {
            c++;
        }

        public int value() {
            return c;
        }
    }
}
