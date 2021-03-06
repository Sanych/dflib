package com.nhl.dflib.series.builder;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.series.IntArraySeries;

import java.util.Arrays;

/**
 * An expandable list of primitive int values that has minimal overhead and can be converted to compact and efficient
 * immutable {@link IntSeries}.
 *
 * @since 0.6
 */
public class IntAccumulator {

    private int[] data;
    private int size;

    public IntAccumulator() {
        this(10);
    }

    public IntAccumulator(int capacity) {
        this.size = 0;
        this.data = new int[capacity];
    }

    public void fill(int from, int to, int value) {
        if (to - from < 1) {
            return;
        }

        if (data.length <= to) {
            expand(to);
        }

        Arrays.fill(data, from, to, value);
        size += to - from;
    }

    public void add(int value) {

        if (size == data.length) {
            expand(data.length * 2);
        }

        data[size++] = value;
    }

    public void set(int pos, int value) {

        if (pos >= size) {
            throw new IndexOutOfBoundsException(pos + " is out of bounds for " + size);
        }

        data[pos] = value;
    }

    public IntSeries toIntSeries() {
        int[] data = compactData();

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        return new IntArraySeries(data, 0, size);
    }

    public int size() {
        return size;
    }

    private int[] compactData() {
        if (data.length == size) {
            return data;
        }

        int[] newData = new int[size];
        System.arraycopy(data, 0, newData, 0, size);
        return newData;
    }

    private void expand(int newCapacity) {
        int[] newData = new int[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);

        this.data = newData;
    }
}
