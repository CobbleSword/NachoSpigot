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

import com.google.common.base.Function;

import java.util.List;

import static co.aikar.util.JSONUtil.toArray;

/**
 * <p>Lightweight object for tracking timing data</p>
 *
 * This is broken out to reduce memory usage
 */
class TimingData {
    static Function<Integer, TimingData> LOADER = TimingData::new;
    int id;
    int count = 0;
    int lagCount = 0;
    long totalTime = 0;
    long lagTotalTime = 0;

    int curTickCount = 0;
    int curTickTotal = 0;

    TimingData(int id) {
        this.id = id;
    }

    TimingData(TimingData data) {
        this.id = data.id;
        this.totalTime = data.totalTime;
        this.lagTotalTime = data.lagTotalTime;
        this.count = data.count;
        this.lagCount = data.lagCount;
    }

    void add(long diff) {
        ++curTickCount;
        curTickTotal += diff;
    }

    void processTick(boolean violated) {
        totalTime += curTickTotal;
        count += curTickCount;
        if (violated) {
            lagTotalTime += curTickTotal;
            lagCount += curTickCount;
        }
        curTickTotal = 0;
        curTickCount = 0;
    }

    void reset() {
        count = 0;
        lagCount = 0;
        curTickTotal = 0;
        curTickCount = 0;
        totalTime = 0;
        lagTotalTime = 0;
    }

    protected TimingData clone() {
        return new TimingData(this);
    }

    public List<?> export() {
        List list = toArray(
            id,
            count,
            totalTime);
        if (lagCount > 0) {
            list.add(lagCount);
            list.add(lagTotalTime);
        }
        return list;
    }
}
