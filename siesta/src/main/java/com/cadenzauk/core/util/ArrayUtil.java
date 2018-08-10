/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cadenzauk.core.util;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class ArrayUtil extends UtilityClass {
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<T> elementType, int size) {
        return (T[]) Array.newInstance(elementType, size);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(TypeToken<T> elementType, int size) {
        return (T[]) Array.newInstance(elementType.getRawType(), size);
    }

    @SuppressWarnings("unchecked")
    public static <T> IntFunction<T[]> generator(Class<? super T> elementClass) {
        return size -> (T[])ArrayUtil.newArray(elementClass, size);
    }

    @SuppressWarnings("unchecked")
    public static <T> IntFunction<T[]> generator(T prototype) {
        return size -> (T[])ArrayUtil.newArray(((Class<T>)prototype.getClass()), size);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] arrayOf(int size, T value) {
        T[] result = newArray((Class<T>)value.getClass(), size);
        Arrays.fill(result, value);
        return result;
    }

    public static char[] arrayOf(int size, char value) {
        char[] result = new char[size];
        Arrays.fill(result, value);
        return result;
    }

    public static boolean[] arrayOf(int size, boolean value) {
        boolean[] result = new boolean[size];
        Arrays.fill(result, value);
        return result;
    }

    public static byte[] arrayOf(int size, byte value) {
        byte[] result = new byte[size];
        Arrays.fill(result, value);
        return result;
    }

    public static short[] arrayOf(int size, short value) {
        short[] result = new short[size];
        Arrays.fill(result, value);
        return result;
    }

    public static int[] arrayOf(int size, int value) {
        int[] result = new int[size];
        Arrays.fill(result, value);
        return result;
    }

    public static long[] arrayOf(int size, long value) {
        long[] result = new long[size];
        Arrays.fill(result, value);
        return result;
    }

    public static float[] arrayOf(int size, float value) {
        float[] result = new float[size];
        Arrays.fill(result, value);
        return result;
    }

    public static double[] arrayOf(int size, double value) {
        double[] result = new double[size];
        Arrays.fill(result, value);
        return result;
    }

    public static Stream<Boolean> stream(boolean[] booleans) {
        return IntStream.range(0, booleans.length).mapToObj(i -> booleans[i]);
    }

    public static Stream<Byte> stream(byte[] bytes) {
        return IntStream.range(0, bytes.length).mapToObj(i -> bytes[i]);
    }

    public static Stream<Character> stream(char[] chars) {
        return IntStream.range(0, chars.length).mapToObj(i -> chars[i]);
    }

    public static Stream<Short> stream(short[] shorts) {
        return IntStream.range(0, shorts.length).mapToObj(i -> shorts[i]);
    }

    public static Stream<Float> stream(float[] floats) {
        return IntStream.range(0, floats.length).mapToObj(i -> floats[i]);
    }
}
