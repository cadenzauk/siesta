/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited
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

package com.cadenzauk.core.tuple;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class TupleTest {
    private static Arguments toStringTestCase(Tuple sut, String expected) {
        return Arguments.of(sut, expected);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> toStringArgs() {
        return Stream.of(
            // Tuple2
            toStringTestCase(Tuple.of("", 100), "(, 100)"),
            toStringTestCase(Tuple.of("foo", 100), "(foo, 100)"),
            toStringTestCase(Tuple.of(null, 100), "(null, 100)"),
            toStringTestCase(Tuple.of("foo", null), "(foo, null)"),
            toStringTestCase(Tuple.of(null, null), "(null, null)"),
            // Tuple3
            toStringTestCase(Tuple.of(2.0, "", 100), "(2.0, , 100)"),
            toStringTestCase(Tuple.of("foo", 100, "baz"), "(foo, 100, baz)"),
            toStringTestCase(Tuple.of(null, null, null), "(null, null, null)"),
            // Tuple4
            toStringTestCase(Tuple.of(1, 2, 3, 4), "(1, 2, 3, 4)"),
            toStringTestCase(Tuple.of("a", "b", "c", "d"), "(a, b, c, d)"),
            toStringTestCase(Tuple.of(null, null, null, null), "(null, null, null, null)"),
            // Tuple5
            toStringTestCase(Tuple.of(1, 2, 3, 4, 5), "(1, 2, 3, 4, 5)"),
            toStringTestCase(Tuple.of("a", "b", "c", "d", "e"), "(a, b, c, d, e)"),
            toStringTestCase(Tuple.of(null, null, null, null, null), "(null, null, null, null, null)"),
            // Tuple6
            toStringTestCase(Tuple.of(1, 2, 3, 4, 5, 6), "(1, 2, 3, 4, 5, 6)"),
            toStringTestCase(Tuple.of("a", "b", "c", "d", "e", "f"), "(a, b, c, d, e, f)"),
            toStringTestCase(Tuple.of(null, null, null, null, null, null), "(null, null, null, null, null, null)"),
            // Tuple7
            toStringTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), "(1, 2, 3, 4, 5, 6, 7)"),
            toStringTestCase(Tuple.of("a", "b", "c", "d", "e", "f", "g"), "(a, b, c, d, e, f, g)"),
            toStringTestCase(Tuple.of(null, null, null, null, null, null, null), "(null, null, null, null, null, null, null)"),
            // Tuple8
            toStringTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), "(1, 2, 3, 4, 5, 6, 7, 8)"),
            toStringTestCase(Tuple.of("a", "b", "c", "d", "e", "f", "g", "h"), "(a, b, c, d, e, f, g, h)"),
            toStringTestCase(Tuple.of(null, null, null, null, null, null, null, null), "(null, null, null, null, null, null, null, null)"),
            // Tuple9
            toStringTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), "(1, 2, 3, 4, 5, 6, 7, 8, 9)"),
            toStringTestCase(Tuple.of("a", "b", "c", "d", "e", "f", "g", "h", "i"), "(a, b, c, d, e, f, g, h, i)"),
            toStringTestCase(Tuple.of(null, null, null, null, null, null, null, null, null), "(null, null, null, null, null, null, null, null, null)")
        );
    }

    @ParameterizedTest
    @MethodSource("toStringArgs")
    void toStringIsCorrect(Tuple sut, String expected) {
        String result = sut.toString();

        assertThat(result, is(expected));
    }


    private static Arguments equalsTestCase(Tuple sut, Tuple rhs, boolean areEqual) {
        return Arguments.of(sut, rhs, areEqual);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> parametersForEquals() {
        return Stream.of(
            // Tuple2
            equalsTestCase(Tuple.of("", 100), Tuple.of("", 100), true),
            equalsTestCase(Tuple.of("foo", 100), Tuple.of("foo", 100), true),
            equalsTestCase(Tuple.of("foo", null), Tuple.of("foo", null), true),
            equalsTestCase(Tuple.of(null, null), Tuple.of(null, null), true),
            equalsTestCase(Tuple.of(null, 100), Tuple.of("", 100), false),
            equalsTestCase(Tuple.of(null, 100), null, false),
            equalsTestCase(Tuple.of(null, null), null, false),
            // Tuple3
            equalsTestCase(Tuple.of(1, 2, 3), Tuple.of(1, 2, 3), true),
            equalsTestCase(Tuple.of(1, 2, 3), Tuple.of(0, 2, 3), false),
            equalsTestCase(Tuple.of(1, 2, 3), Tuple.of(1, 0, 3), false),
            equalsTestCase(Tuple.of(1, 2, 3), Tuple.of(1, 2, 0), false),
            equalsTestCase(Tuple.of(1, 2, 3), Tuple.of(1, 2), false),
            equalsTestCase(Tuple.of(null, null, null), Tuple.of(null, null, null), true),
            equalsTestCase(Tuple.of(null, null, null), null, false),
            // Tuple4
            equalsTestCase(Tuple.of(1, 2, 3, 4), Tuple.of(1, 2, 3, 4), true),
            equalsTestCase(Tuple.of(1, 2, 3, 4), Tuple.of(0, 2, 3, 4), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4), Tuple.of(1, 0, 3, 4), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4), Tuple.of(1, 2, 0, 4), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4), Tuple.of(1, 2, 3, 0), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4), Tuple.of(1, 2, 3), false),
            equalsTestCase(Tuple.of(null, null, null, null), Tuple.of(null, null, null, null), true),
            equalsTestCase(Tuple.of(null, null, null, null), null, false),
            // Tuple5
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5), Tuple.of(1, 2, 3, 4, 5), true),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5), Tuple.of(0, 2, 3, 4, 5), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5), Tuple.of(1, 0, 3, 4, 5), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5), Tuple.of(1, 2, 0, 4, 5), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5), Tuple.of(1, 2, 3, 0, 5), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5), Tuple.of(1, 2, 3, 4, 0), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5), Tuple.of(1, 2, 3, 4), false),
            equalsTestCase(Tuple.of(null, null, null, null, null), Tuple.of(null, null, null, null, null), true),
            equalsTestCase(Tuple.of(null, null, null, null, null), null, false),
            // Tuple6
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6), Tuple.of(1, 2, 3, 4, 5, 6), true),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6), Tuple.of(0, 2, 3, 4, 5, 6), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6), Tuple.of(1, 0, 3, 4, 5, 6), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6), Tuple.of(1, 2, 0, 4, 5, 6), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6), Tuple.of(1, 2, 3, 0, 5, 6), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6), Tuple.of(1, 2, 3, 4, 0, 6), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6), Tuple.of(1, 2, 3, 4, 5, 0), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6), Tuple.of(1, 2, 3, 4, 5), false),
            equalsTestCase(Tuple.of(null, null, null, null, null, null), Tuple.of(null, null, null, null, null, null), true),
            equalsTestCase(Tuple.of(null, null, null, null, null, null), null, false),
            // Tuple7
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple.of(1, 2, 3, 4, 5, 6, 7), true),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple.of(0, 2, 3, 4, 5, 6, 7), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple.of(1, 0, 3, 4, 5, 6, 7), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple.of(1, 2, 0, 4, 5, 6, 7), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple.of(1, 2, 3, 0, 5, 6, 7), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple.of(1, 2, 3, 4, 0, 6, 7), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple.of(1, 2, 3, 4, 5, 0, 7), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple.of(1, 2, 3, 4, 5, 6, 0), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple.of(1, 2, 3, 4, 5, 6), false),
            equalsTestCase(Tuple.of(null, null, null, null, null, null, null), Tuple.of(null, null, null, null, null, null, null), true),
            equalsTestCase(Tuple.of(null, null, null, null, null, null, null), null, false),
            // Tuple8
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), true),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple.of(0, 2, 3, 4, 5, 6, 7, 8), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple.of(1, 0, 3, 4, 5, 6, 7, 8), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple.of(1, 2, 0, 4, 5, 6, 7, 8), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple.of(1, 2, 3, 0, 5, 6, 7, 8), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple.of(1, 2, 3, 4, 0, 6, 7, 8), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple.of(1, 2, 3, 4, 5, 0, 7, 8), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple.of(1, 2, 3, 4, 5, 6, 0, 8), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple.of(1, 2, 3, 4, 5, 6, 7, 0), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple.of(1, 2, 3, 4, 5, 6, 7), false),
            equalsTestCase(Tuple.of(null, null, null, null, null, null, null, null), Tuple.of(null, null, null, null, null, null, null, null), true),
            equalsTestCase(Tuple.of(null, null, null, null, null, null, null, null), null, false),
            // Tuple9
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), true),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple.of(0, 2, 3, 4, 5, 6, 7, 8, 9), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple.of(1, 0, 3, 4, 5, 6, 7, 8, 9), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple.of(1, 2, 0, 4, 5, 6, 7, 8, 9), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple.of(1, 2, 3, 0, 5, 6, 7, 8, 9), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple.of(1, 2, 3, 4, 0, 6, 7, 8, 9), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple.of(1, 2, 3, 4, 5, 0, 7, 8, 9), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple.of(1, 2, 3, 4, 5, 6, 0, 8, 9), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple.of(1, 2, 3, 4, 5, 6, 7, 0, 9), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 0), false),
            equalsTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), false),
            equalsTestCase(Tuple.of(null, null, null, null, null, null, null, null, null), Tuple.of(null, null, null, null, null, null, null, null, null), true),
            equalsTestCase(Tuple.of(null, null, null, null, null, null, null, null, null), null, false)
        );
    }

    @SuppressWarnings({"EqualsWithItself", "ConstantConditions"})
    @ParameterizedTest
    @MethodSource("parametersForEquals")
    void equalsIsCorrect(Tuple sut, Tuple rhs, boolean areEqual) {
        boolean result1 = sut.equals(sut);
        boolean result2 = sut.equals(rhs);

        assertThat(result1, is(true));
        assertThat(result2, is(areEqual));
    }

    private static Arguments hashCodeTestCase(Tuple sut, Tuple equal, Tuple notEqual) {
        return Arguments.of(sut, equal, notEqual);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> hashCodeArgs() {
        return Stream.of(
            // Tuple2
            hashCodeTestCase(Tuple.of(1, 2), Tuple.of(1, 2), Tuple.of(1, 0)),
            hashCodeTestCase(Tuple.of(null, 2), Tuple.of(null, 2), Tuple.of(1, 2)),
            hashCodeTestCase(Tuple.of(1, null), Tuple.of(1, null), Tuple.of(1, 2)),
            // Tuple3
            hashCodeTestCase(Tuple.of(1, 2, 3), Tuple.of(1, 2, 3), Tuple.of(1, 2, 0)),
            hashCodeTestCase(Tuple.of(null, 2, 3), Tuple.of(null, 2, 3), Tuple.of(1, 2, 3)),
            hashCodeTestCase(Tuple.of(1, null, 3), Tuple.of(1, null, 3), Tuple.of(1, 2, 3)),
            hashCodeTestCase(Tuple.of(1, 2, null), Tuple.of(1, 2, null), Tuple.of(1, 2, 3)),
            // Tuple4
            hashCodeTestCase(Tuple.of(1, 2, 3, 4), Tuple.of(1, 2, 3, 4), Tuple.of(1, 2, 3, 0)),
            hashCodeTestCase(Tuple.of(null, 2, 3, 4), Tuple.of(null, 2, 3, 4), Tuple.of(1, 2, 3, 4)),
            hashCodeTestCase(Tuple.of(1, null, 3, 4), Tuple.of(1, null, 3, 4), Tuple.of(1, 2, 3, 4)),
            hashCodeTestCase(Tuple.of(1, 2, null, 4), Tuple.of(1, 2, null, 4), Tuple.of(1, 2, 3, 4)),
            hashCodeTestCase(Tuple.of(1, 2, 3, null), Tuple.of(1, 2, 3, null), Tuple.of(1, 2, 3, 4)),
            // Tuple5
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, 5), Tuple.of(1, 2, 3, 4, 5), Tuple.of(1, 2, 3, 4, 0)),
            hashCodeTestCase(Tuple.of(null, 2, 3, 4, 5), Tuple.of(null, 2, 3, 4, 5), Tuple.of(1, 2, 3, 4, 5)),
            hashCodeTestCase(Tuple.of(1, null, 3, 4, 5), Tuple.of(1, null, 3, 4, 5), Tuple.of(1, 2, 3, 4, 5)),
            hashCodeTestCase(Tuple.of(1, 2, null, 4, 5), Tuple.of(1, 2, null, 4, 5), Tuple.of(1, 2, 3, 4, 5)),
            hashCodeTestCase(Tuple.of(1, 2, 3, null, 5), Tuple.of(1, 2, 3, null, 5), Tuple.of(1, 2, 3, 4, 5)),
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, null), Tuple.of(1, 2, 3, 4, null), Tuple.of(1, 2, 3, 4, 5)),
            // Tuple6
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, 5, 6), Tuple.of(1, 2, 3, 4, 5, 6), Tuple.of(1, 2, 3, 4, 5, 0)),
            hashCodeTestCase(Tuple.of(null, 2, 3, 4, 5, 6), Tuple.of(null, 2, 3, 4, 5, 6), Tuple.of(1, 2, 3, 4, 5, 6)),
            hashCodeTestCase(Tuple.of(1, null, 3, 4, 5, 6), Tuple.of(1, null, 3, 4, 5, 6), Tuple.of(1, 2, 3, 4, 5, 6)),
            hashCodeTestCase(Tuple.of(1, 2, null, 4, 5, 6), Tuple.of(1, 2, null, 4, 5, 6), Tuple.of(1, 2, 3, 4, 5, 6)),
            hashCodeTestCase(Tuple.of(1, 2, 3, null, 5, 6), Tuple.of(1, 2, 3, null, 5, 6), Tuple.of(1, 2, 3, 4, 5, 6)),
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, null, 6), Tuple.of(1, 2, 3, 4, null, 6), Tuple.of(1, 2, 3, 4, 5, 6)),
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, 5, null), Tuple.of(1, 2, 3, 4, 5, null), Tuple.of(1, 2, 3, 4, 5, 6)),
            // Tuple7
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple.of(1, 2, 3, 4, 5, 6, 0)),
            hashCodeTestCase(Tuple.of(null, 2, 3, 4, 5, 6, 7), Tuple.of(null, 2, 3, 4, 5, 6, 7), Tuple.of(1, 2, 3, 4, 5, 6, 7)),
            hashCodeTestCase(Tuple.of(1, null, 3, 4, 5, 6, 7), Tuple.of(1, null, 3, 4, 5, 6, 7), Tuple.of(1, 2, 3, 4, 5, 6, 7)),
            hashCodeTestCase(Tuple.of(1, 2, null, 4, 5, 6, 7), Tuple.of(1, 2, null, 4, 5, 6, 7), Tuple.of(1, 2, 3, 4, 5, 6, 7)),
            hashCodeTestCase(Tuple.of(1, 2, 3, null, 5, 6, 7), Tuple.of(1, 2, 3, null, 5, 6, 7), Tuple.of(1, 2, 3, 4, 5, 6, 7)),
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, null, 6, 7), Tuple.of(1, 2, 3, 4, null, 6, 7), Tuple.of(1, 2, 3, 4, 5, 6, 7)),
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, 5, null, 7), Tuple.of(1, 2, 3, 4, 5, null, 7), Tuple.of(1, 2, 3, 4, 5, 6, 7)),
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, 5, 6, null), Tuple.of(1, 2, 3, 4, 5, 6, null), Tuple.of(1, 2, 3, 4, 5, 6, 7)),
            // Tuple8
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple.of(1, 2, 3, 4, 5, 6, 7, 0)),
            hashCodeTestCase(Tuple.of(null, 2, 3, 4, 5, 6, 7, 8), Tuple.of(null, 2, 3, 4, 5, 6, 7, 8), Tuple.of(1, 2, 3, 4, 5, 6, 7, 8)),
            hashCodeTestCase(Tuple.of(1, null, 3, 4, 5, 6, 7, 8), Tuple.of(1, null, 3, 4, 5, 6, 7, 8), Tuple.of(1, 2, 3, 4, 5, 6, 7, 8)),
            hashCodeTestCase(Tuple.of(1, 2, null, 4, 5, 6, 7, 8), Tuple.of(1, 2, null, 4, 5, 6, 7, 8), Tuple.of(1, 2, 3, 4, 5, 6, 7, 8)),
            hashCodeTestCase(Tuple.of(1, 2, 3, null, 5, 6, 7, 8), Tuple.of(1, 2, 3, null, 5, 6, 7, 8), Tuple.of(1, 2, 3, 4, 5, 6, 7, 8)),
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, null, 6, 7, 8), Tuple.of(1, 2, 3, 4, null, 6, 7, 8), Tuple.of(1, 2, 3, 4, 5, 6, 7, 8)),
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, 5, null, 7, 8), Tuple.of(1, 2, 3, 4, 5, null, 7, 8), Tuple.of(1, 2, 3, 4, 5, 6, 7, 8)),
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, 5, 6, null, 8), Tuple.of(1, 2, 3, 4, 5, 6, null, 8), Tuple.of(1, 2, 3, 4, 5, 6, 7, 8)),
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, null), Tuple.of(1, 2, 3, 4, 5, 6, 7, null), Tuple.of(1, 2, 3, 4, 5, 6, 7, 8)),
            // Tuple9
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple.of(1, 2, 3, 4, 6, 7, 8, 0)),
            hashCodeTestCase(Tuple.of(null, 2, 3, 4, 5, 6, 7, 8, 9), Tuple.of(null, 2, 3, 4, 5, 6, 7, 8, 9), Tuple.of(1, 2, 3, 4, 6, 7, 8, 9)),
            hashCodeTestCase(Tuple.of(1, null, 3, 4, 5, 6, 7, 8, 9), Tuple.of(1, null, 3, 4, 5, 6, 7, 8, 9), Tuple.of(1, 2, 3, 4, 6, 7, 8, 9)),
            hashCodeTestCase(Tuple.of(1, 2, null, 4, 5, 6, 7, 8, 9), Tuple.of(1, 2, null, 4, 5, 6, 7, 8, 9), Tuple.of(1, 2, 3, 4, 6, 7, 8, 9)),
            hashCodeTestCase(Tuple.of(1, 2, 3, null, 5, 6, 7, 8, 9), Tuple.of(1, 2, 3, null, 5, 6, 7, 8, 9), Tuple.of(1, 2, 3, 4, 6, 7, 8, 9)),
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, null, 6, 7, 8, 9), Tuple.of(1, 2, 3, 4, null, 6, 7, 8, 9), Tuple.of(1, 2, 3, 4, 6, 7, 8, 9)),
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, 5, null, 7, 8, 9), Tuple.of(1, 2, 3, 4, 5, null, 7, 8, 9), Tuple.of(1, 2, 3, 4, 6, 7, 8, 9)),
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, 5, 6, null, 8, 9), Tuple.of(1, 2, 3, 4, 5, 6, null, 8, 9), Tuple.of(1, 2, 3, 4, 6, 7, 8, 9)),
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, null, 9), Tuple.of(1, 2, 3, 4, 5, 6, 7, null, 9), Tuple.of(1, 2, 3, 4, 6, 7, 8, 9)),
            hashCodeTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, null), Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, null), Tuple.of(1, 2, 3, 4, 6, 7, 8, 9))
        );
    }

    @ParameterizedTest
    @MethodSource("hashCodeArgs")
    void hashCodeIsCorrect(Tuple sut, Tuple equal, Tuple notEqual) {
        assertThat(sut.hashCode(), is(equal.hashCode()));
        assertThat(sut.hashCode(), not(notEqual.hashCode()));
    }

    private static <T extends Tuple> Arguments itemTestCase(T sut, Function<T,Integer> getter, int expected) {
        return Arguments.of(sut, getter, expected);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> itemArgs() {
        return Stream.of(
            itemTestCase(Tuple.of(1, 2), Tuple2::item1, 1),
            itemTestCase(Tuple.of(1, 2), Tuple2::item2, 2),
            itemTestCase(Tuple.of(1, 2, 3), Tuple3::item1, 1),
            itemTestCase(Tuple.of(1, 2, 3), Tuple3::item2, 2),
            itemTestCase(Tuple.of(1, 2, 3), Tuple3::item3, 3),
            itemTestCase(Tuple.of(1, 2, 3, 4), Tuple4::item1, 1),
            itemTestCase(Tuple.of(1, 2, 3, 4), Tuple4::item2, 2),
            itemTestCase(Tuple.of(1, 2, 3, 4), Tuple4::item3, 3),
            itemTestCase(Tuple.of(1, 2, 3, 4), Tuple4::item4, 4),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5), Tuple5::item1, 1),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5), Tuple5::item2, 2),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5), Tuple5::item3, 3),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5), Tuple5::item4, 4),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5), Tuple5::item5, 5),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6), Tuple6::item1, 1),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6), Tuple6::item2, 2),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6), Tuple6::item3, 3),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6), Tuple6::item4, 4),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6), Tuple6::item5, 5),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6), Tuple6::item6, 6),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple7::item1, 1),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple7::item2, 2),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple7::item3, 3),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple7::item4, 4),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple7::item5, 5),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple7::item6, 6),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), Tuple7::item7, 7),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple8::item1, 1),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple8::item2, 2),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple8::item3, 3),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple8::item4, 4),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple8::item5, 5),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple8::item6, 6),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple8::item7, 7),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), Tuple8::item8, 8),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple9::item1, 1),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple9::item2, 2),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple9::item3, 3),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple9::item4, 4),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple9::item5, 5),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple9::item6, 6),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple9::item7, 7),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple9::item8, 8),
            itemTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), Tuple9::item9, 9));
    }

    @ParameterizedTest
    @MethodSource("itemArgs")
    <T extends Tuple> void item(T sut, Function<T,Integer> getter, int expected) {
        Integer actual = getter.apply(sut);

        assertThat(actual, is(expected));
    }

    private static <T extends Tuple> Arguments mapTestCase(T sut, Function<T,List<Integer>> mapper, List<Integer> expected) {
        return Arguments.of(sut, mapper, expected);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> mapArgs() {
        return Stream.of(
            mapTestCase(Tuple.of(1, 2), t -> t.map(ImmutableList::of), ImmutableList.of(1, 2)),
            mapTestCase(Tuple.of(1, 2, 3), t -> t.map(ImmutableList::of), ImmutableList.of(1, 2, 3)),
            mapTestCase(Tuple.of(1, 2, 3, 4), t -> t.map(ImmutableList::of), ImmutableList.of(1, 2, 3, 4)),
            mapTestCase(Tuple.of(1, 2, 3, 4, 5), t -> t.map(ImmutableList::of), ImmutableList.of(1, 2, 3, 4, 5)),
            mapTestCase(Tuple.of(1, 2, 3, 4, 5, 6), t -> t.map(ImmutableList::of), ImmutableList.of(1, 2, 3, 4, 5, 6)),
            mapTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7), t -> t.map(ImmutableList::of), ImmutableList.of(1, 2, 3, 4, 5, 6, 7)),
            mapTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8), t -> t.map(ImmutableList::of), ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8)),
            mapTestCase(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9), t -> t.map(ImmutableList::of), ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8, 9))
        );
    }

    @ParameterizedTest
    @MethodSource("mapArgs")
    <T extends Tuple> void map(T sut, Function<T,List<Integer>> mapper, List<Integer> expected) {
        List<Integer> actual = mapper.apply(sut);

        assertThat(actual, is(expected));
    }
}