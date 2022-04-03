/*
 * This file is licensed under the MIT License (MIT).
 *
 * Copyright (c) 2014 Daniel Ennis <http://aikar.co>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package co.aikar.timings;

import co.aikar.util.LoadingMap;
import co.aikar.util.MRUMapCache;

import java.util.ArrayDeque;
import java.util.Map;

/**
 * <p>Used as a basis for fast HashMap key comparisons for the Timing Map.</p>
 *
 * This class uses interned strings giving us the ability to do an identity check instead of equals() on the strings
 */
final class TimingIdentifier {
    /**
     * Holds all groups. Autoloads on request for a group by name.
     */
    static final Map<String, TimingGroup> GROUP_MAP = MRUMapCache.of(
        LoadingMap.newIdentityHashMap(TimingGroup::new, 64)
    );
    static final TimingGroup DEFAULT_GROUP = getGroup("Minecraft");
    final String group;
    final String name;
    final TimingHandler groupHandler;
    final boolean protect;
    private final int hashCode;

    TimingIdentifier(String group, String name, Timing groupHandler, boolean protect) {
        this.group = group != null ? group.intern() : DEFAULT_GROUP.name;
        this.name = name.intern();
        this.groupHandler = groupHandler != null ? groupHandler.getTimingHandler() : null;
        this.protect = protect;
        this.hashCode = (31 * this.group.hashCode()) + this.name.hashCode();
    }

    static TimingGroup getGroup(String groupName) {
        if (groupName == null) {
            return DEFAULT_GROUP;
        }

        return GROUP_MAP.get(groupName.intern());
    }

    // We are using .intern() on the strings so it is guaranteed to be an identity comparison.
    @SuppressWarnings("StringEquality")
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        TimingIdentifier that = (TimingIdentifier) o;
        return group == that.group && name == that.name;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    static class TimingGroup {

        private static int idPool = 1;
        final int id = idPool++;

        final String name;
        ArrayDeque<TimingHandler> handlers = new ArrayDeque<>(64);

        private TimingGroup(String name) {
            this.name = name;
        }
    }
}
