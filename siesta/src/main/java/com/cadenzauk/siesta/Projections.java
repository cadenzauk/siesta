/*
 * Copyright (c) 2017, 2020 Cadenza United Kingdom Limited
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
package com.cadenzauk.siesta;

import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.projection.ProjectionList;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.core.tuple.Tuple4;
import com.cadenzauk.core.tuple.Tuple5;
import com.cadenzauk.core.tuple.Tuple6;
import com.cadenzauk.core.tuple.Tuple7;
import com.cadenzauk.core.tuple.Tuple8;
import com.cadenzauk.core.tuple.Tuple9;
import com.cadenzauk.core.tuple.Tuple10;
import com.cadenzauk.core.tuple.Tuple11;
import com.cadenzauk.core.tuple.Tuple12;
import com.cadenzauk.core.tuple.Tuple13;
import com.cadenzauk.core.tuple.Tuple14;
import com.cadenzauk.core.tuple.Tuple15;
import com.cadenzauk.core.tuple.Tuple16;
import com.cadenzauk.core.tuple.Tuple17;
import com.cadenzauk.core.tuple.Tuple18;
import com.cadenzauk.core.tuple.Tuple19;
import com.cadenzauk.core.tuple.Tuple20;
import com.google.common.collect.ImmutableList;

import java.util.function.Function;

public interface Projections {
    static <R1, R2> Projection<Tuple2<R1,R2>> of2(Projection<R1> lhs, Projection<R2> rhs) {
        Function<Scope,RowMapperFactory<Tuple2<R1,R2>>> rowMapperFactory = scope -> RowMapperFactories.of(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(false, ImmutableList.of(lhs, rhs), rowMapperFactory);
    }

    static <R1, R2, R3> Projection<Tuple3<R1,R2,R3>> of3(Projection<Tuple2<R1,R2>> lhs, Projection<R3> rhs) {
        Function<Scope,RowMapperFactory<Tuple3<R1,R2,R3>>> rowMapperFactory = scope -> RowMapperFactories.add3rd(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4> Projection<Tuple4<R1,R2,R3,R4>> of4(Projection<Tuple3<R1,R2,R3>> lhs, Projection<R4> rhs) {
        Function<Scope,RowMapperFactory<Tuple4<R1,R2,R3,R4>>> rowMapperFactory = scope -> RowMapperFactories.add4th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5> Projection<Tuple5<R1,R2,R3,R4,R5>> of5(Projection<Tuple4<R1,R2,R3,R4>> lhs, Projection<R5> rhs) {
        Function<Scope,RowMapperFactory<Tuple5<R1,R2,R3,R4,R5>>> rowMapperFactory = scope -> RowMapperFactories.add5th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5, R6> Projection<Tuple6<R1,R2,R3,R4,R5,R6>> of6(Projection<Tuple5<R1,R2,R3,R4,R5>> lhs, Projection<R6> rhs) {
        Function<Scope,RowMapperFactory<Tuple6<R1,R2,R3,R4,R5,R6>>> rowMapperFactory = scope -> RowMapperFactories.add6th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5, R6, R7> Projection<Tuple7<R1,R2,R3,R4,R5,R6,R7>> of7(Projection<Tuple6<R1,R2,R3,R4,R5,R6>> lhs, Projection<R7> rhs) {
        Function<Scope,RowMapperFactory<Tuple7<R1,R2,R3,R4,R5,R6,R7>>> rowMapperFactory = scope -> RowMapperFactories.add7th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5, R6, R7, R8> Projection<Tuple8<R1,R2,R3,R4,R5,R6,R7,R8>> of8(Projection<Tuple7<R1,R2,R3,R4,R5,R6,R7>> lhs, Projection<R8> rhs) {
        Function<Scope,RowMapperFactory<Tuple8<R1,R2,R3,R4,R5,R6,R7,R8>>> rowMapperFactory = scope -> RowMapperFactories.add8th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5, R6, R7, R8, R9> Projection<Tuple9<R1,R2,R3,R4,R5,R6,R7,R8,R9>> of9(Projection<Tuple8<R1,R2,R3,R4,R5,R6,R7,R8>> lhs, Projection<R9> rhs) {
        Function<Scope,RowMapperFactory<Tuple9<R1,R2,R3,R4,R5,R6,R7,R8,R9>>> rowMapperFactory = scope -> RowMapperFactories.add9th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5, R6, R7, R8, R9, R10> Projection<Tuple10<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10>> of10(Projection<Tuple9<R1,R2,R3,R4,R5,R6,R7,R8,R9>> lhs, Projection<R10> rhs) {
        Function<Scope,RowMapperFactory<Tuple10<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10>>> rowMapperFactory = scope -> RowMapperFactories.add10th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11> Projection<Tuple11<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11>> of11(Projection<Tuple10<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10>> lhs, Projection<R11> rhs) {
        Function<Scope,RowMapperFactory<Tuple11<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11>>> rowMapperFactory = scope -> RowMapperFactories.add11th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12> Projection<Tuple12<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12>> of12(Projection<Tuple11<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11>> lhs, Projection<R12> rhs) {
        Function<Scope,RowMapperFactory<Tuple12<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12>>> rowMapperFactory = scope -> RowMapperFactories.add12th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13> Projection<Tuple13<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13>> of13(Projection<Tuple12<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12>> lhs, Projection<R13> rhs) {
        Function<Scope,RowMapperFactory<Tuple13<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13>>> rowMapperFactory = scope -> RowMapperFactories.add13th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14> Projection<Tuple14<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14>> of14(Projection<Tuple13<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13>> lhs, Projection<R14> rhs) {
        Function<Scope,RowMapperFactory<Tuple14<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14>>> rowMapperFactory = scope -> RowMapperFactories.add14th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15> Projection<Tuple15<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15>> of15(Projection<Tuple14<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14>> lhs, Projection<R15> rhs) {
        Function<Scope,RowMapperFactory<Tuple15<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15>>> rowMapperFactory = scope -> RowMapperFactories.add15th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16> Projection<Tuple16<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15,R16>> of16(Projection<Tuple15<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15>> lhs, Projection<R16> rhs) {
        Function<Scope,RowMapperFactory<Tuple16<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15,R16>>> rowMapperFactory = scope -> RowMapperFactories.add16th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17> Projection<Tuple17<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15,R16,R17>> of17(Projection<Tuple16<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15,R16>> lhs, Projection<R17> rhs) {
        Function<Scope,RowMapperFactory<Tuple17<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15,R16,R17>>> rowMapperFactory = scope -> RowMapperFactories.add17th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17, R18> Projection<Tuple18<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15,R16,R17,R18>> of18(Projection<Tuple17<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15,R16,R17>> lhs, Projection<R18> rhs) {
        Function<Scope,RowMapperFactory<Tuple18<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15,R16,R17,R18>>> rowMapperFactory = scope -> RowMapperFactories.add18th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17, R18, R19> Projection<Tuple19<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15,R16,R17,R18,R19>> of19(Projection<Tuple18<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15,R16,R17,R18>> lhs, Projection<R19> rhs) {
        Function<Scope,RowMapperFactory<Tuple19<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15,R16,R17,R18,R19>>> rowMapperFactory = scope -> RowMapperFactories.add19th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }

    static <R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17, R18, R19, R20> Projection<Tuple20<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15,R16,R17,R18,R19,R20>> of20(Projection<Tuple19<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15,R16,R17,R18,R19>> lhs, Projection<R20> rhs) {
        Function<Scope,RowMapperFactory<Tuple20<R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15,R16,R17,R18,R19,R20>>> rowMapperFactory = scope -> RowMapperFactories.add20th(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }
}
