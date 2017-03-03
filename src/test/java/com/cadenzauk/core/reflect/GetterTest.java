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

package com.cadenzauk.core.reflect;

import com.cadenzauk.core.lang.RuntimeInstantiationException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.function.Function;

import static com.cadenzauk.core.reflect.util.ClassUtil.getDeclaredField;
import static com.cadenzauk.core.reflect.util.MethodUtil.fromReference;
import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GetterTest {
    @Test
    public void cannotInstantiate() {
        calling(() -> Factory.forClass(Getter.class).get())
            .shouldThrow(RuntimeException.class)
            .withCause(InvocationTargetException.class)
            .withCause(RuntimeInstantiationException.class);
    }

    @Test
    public void forFieldWithNoGetter() throws Exception {
        Function<GetterTestClass,Optional<String>> getter
            = Getter.forField(GetterTestClass.class, String.class, getDeclaredField(GetterTestClass.class, "fieldWithNoGetter"));

        Optional<String> result = getter.apply(new GetterTestClass());

        assertThat(result, is(Optional.of("whatever")));
    }

    @Test
    public void forFieldWithNoGetterGivenWrongType() {
        calling(() -> Getter.forField(GetterTestClass.class, Byte.class, getDeclaredField(GetterTestClass.class, "fieldWithNoGetter")))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage(is("Cannot convert private java.lang.String com.cadenzauk.core.reflect.GetterTest$GetterTestClass.fieldWithNoGetter " +
                "into a Function<class com.cadenzauk.core.reflect.GetterTest$GetterTestClass,Optional<class java.lang.Byte>>."));
    }

    @Test
    public void forOptionalFieldWithNoGetter() throws Exception {
        Function<GetterTestClass,Optional<Long>> fieldWithNoGetter
            = Getter.forField(GetterTestClass.class, Long.class, getDeclaredField(GetterTestClass.class, "optionalFieldWithNoGetter"));

        Optional<Long> result = fieldWithNoGetter.apply(new GetterTestClass());

        assertThat(result, is(Optional.of(54323928574L)));
    }

    @Test
    public void forFieldWithUnprefixedGetter() throws Exception {
        Function<GetterTestClass,Optional<BigDecimal>> fieldWithNoGetter
            = Getter.forField(GetterTestClass.class, BigDecimal.class, getDeclaredField(GetterTestClass.class, "fieldWithUnprefixedGetter"));
        GetterTestClass mock = mock(GetterTestClass.class);
        when(mock.fieldWithUnprefixedGetter()).thenReturn(BigDecimal.TEN);

        Optional<BigDecimal> result = fieldWithNoGetter.apply(mock);

        assertThat(result, is(Optional.of(BigDecimal.TEN)));
        verify(mock).fieldWithUnprefixedGetter();
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void forOptionalFieldWithUnprefixedGetter() throws Exception {
        Function<GetterTestClass,Optional<Integer>> fieldWithNoGetter
            = Getter.forField(GetterTestClass.class, Integer.class, getDeclaredField(GetterTestClass.class, "optionalFieldWithUnprefixedGetter"));
        GetterTestClass mock = mock(GetterTestClass.class);
        when(mock.optionalFieldWithUnprefixedGetter()).thenReturn(Optional.of(3141569));

        Optional<Integer> result = fieldWithNoGetter.apply(mock);

        assertThat(result, is(Optional.of(3141569)));
        verify(mock).optionalFieldWithUnprefixedGetter();
        verifyNoMoreInteractions(mock);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void forFieldWithGetPrefixedGetter() throws Exception {
        Function<GetterTestClass,Optional<Short>> fieldWithNoGetter
            = Getter.forField(GetterTestClass.class, Short.TYPE, getDeclaredField(GetterTestClass.class, "fieldWithGetPrefixedGetter"));
        GetterTestClass mock = mock(GetterTestClass.class);
        when(mock.getFieldWithGetPrefixedGetter()).thenReturn((short) 31415);

        Optional<Short> result = fieldWithNoGetter.apply(mock);

        assertThat(result, is(Optional.of((short) 31415)));
        verify(mock).getFieldWithGetPrefixedGetter();
        verifyNoMoreInteractions(mock);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void forOptionalFieldWithGetPrefixedGetter() throws Exception {
        Function<GetterTestClass,Optional<LocalDate>> fieldWithNoGetter
            = Getter.forField(GetterTestClass.class, LocalDate.class, getDeclaredField(GetterTestClass.class, "optionalFieldWithGetPrefixedGetter"));
        GetterTestClass mock = mock(GetterTestClass.class);
        when(mock.getOptionalFieldWithGetPrefixedGetter()).thenReturn(Optional.of(LocalDate.of(1969, Month.JULY, 20)));

        Optional<LocalDate> result = fieldWithNoGetter.apply(mock);

        assertThat(result, is(Optional.of(LocalDate.of(1969, Month.JULY, 20))));
        verify(mock).getOptionalFieldWithGetPrefixedGetter();
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void forOptionalFieldWithGetPrefixedGetterGivenWrongType() {
        calling(() -> Getter.forField(GetterTestClass.class, Byte.class, getDeclaredField(GetterTestClass.class, "optionalFieldWithGetPrefixedGetter")))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage(is("Cannot convert public java.util.Optional com.cadenzauk.core.reflect.GetterTest$GetterTestClass.getOptionalFieldWithGetPrefixedGetter() " +
                "into a Function<class com.cadenzauk.core.reflect.GetterTest$GetterTestClass,Optional<class java.lang.Byte>>."));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void forFieldWithIsPrefixedGetter() throws Exception {
        Function<GetterTestClass,Optional<Boolean>> fieldWithNoGetter
            = Getter.forField(GetterTestClass.class, Boolean.TYPE, getDeclaredField(GetterTestClass.class, "fieldWithIsPrefixedGetter"));
        GetterTestClass mock = mock(GetterTestClass.class);
        when(mock.isFieldWithIsPrefixedGetter()).thenReturn(true);

        Optional<Boolean> result = fieldWithNoGetter.apply(mock);

        assertThat(result, is(Optional.of(true)));
        verify(mock).isFieldWithIsPrefixedGetter();
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void forOptionalFieldWithIsPrefixedGetter() throws Exception {
        Function<GetterTestClass,Optional<Boolean>> fieldWithNoGetter
            = Getter.forField(GetterTestClass.class, Boolean.class, getDeclaredField(GetterTestClass.class, "optionalFieldWithIsPrefixedGetter"));
        GetterTestClass mock = mock(GetterTestClass.class);
        when(mock.isOptionalFieldWithIsPrefixedGetter()).thenReturn(Optional.of(true));

        Optional<Boolean> result = fieldWithNoGetter.apply(mock);

        assertThat(result, is(Optional.of(true)));
        verify(mock).isOptionalFieldWithIsPrefixedGetter();
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void forFieldUsingSupertype() throws Exception {
        Function<GetterTestClass,Optional<Number>> getter
            = Getter.forField(GetterTestClass.class, Number.class, getDeclaredField(GetterTestClass.class, "fieldWithUnprefixedGetter"));
        GetterTestClass mock = mock(GetterTestClass.class);
        when(mock.fieldWithUnprefixedGetter()).thenReturn(BigDecimal.ONE);

        Optional<Number> result = getter.apply(mock);

        assertThat(result, is(Optional.of(BigDecimal.ONE)));
        verify(mock).fieldWithUnprefixedGetter();
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void isGetter() throws Exception {
        assertThat(Getter.isGetter(
            fromReference(GetterTestClass::fieldWithUnprefixedGetter),
            getDeclaredField(GetterTestClass.class, "fieldWithUnprefixedGetter")),
            is(true));
        assertThat(Getter.isGetter(
            fromReference(GetterTestClass::getFieldWithGetPrefixedGetter),
            getDeclaredField(GetterTestClass.class, "fieldWithGetPrefixedGetter")),
            is(true));
        assertThat(Getter.isGetter(
            fromReference(GetterTestClass::optionalFieldWithUnprefixedGetter),
            getDeclaredField(GetterTestClass.class, "optionalFieldWithUnprefixedGetter")),
            is(true));
        assertThat(Getter.isGetter(
            fromReference(GetterTestClass::getOptionalFieldWithGetPrefixedGetter),
            getDeclaredField(GetterTestClass.class, "optionalFieldWithGetPrefixedGetter")),
            is(true));
        assertThat(Getter.isGetter(
            fromReference(GetterTestClass::isFieldWithIsPrefixedGetter),
            getDeclaredField(GetterTestClass.class, "fieldWithIsPrefixedGetter")),
            is(true));
        assertThat(Getter.isGetter(
            fromReference(GetterTestClass::isOptionalFieldWithIsPrefixedGetter),
            getDeclaredField(GetterTestClass.class, "optionalFieldWithIsPrefixedGetter")),
            is(true));
    }

    @SuppressWarnings("unused")
    private static class GetterTestClass {
        private String fieldWithNoGetter = "whatever";
        private Optional<Long> optionalFieldWithNoGetter = Optional.of(54323928574L);
        private BigDecimal fieldWithUnprefixedGetter;
        private Optional<Integer> optionalFieldWithUnprefixedGetter;
        private short fieldWithGetPrefixedGetter;
        private Optional<LocalDate> optionalFieldWithGetPrefixedGetter;
        private boolean fieldWithIsPrefixedGetter;
        private Optional<Boolean> optionalFieldWithIsPrefixedGetter;

        public BigDecimal fieldWithUnprefixedGetter() {
            return fieldWithUnprefixedGetter;
        }

        public Optional<Integer> optionalFieldWithUnprefixedGetter() {
            return optionalFieldWithUnprefixedGetter;
        }

        public short getFieldWithGetPrefixedGetter() {
            return fieldWithGetPrefixedGetter;
        }

        public Optional<LocalDate> getOptionalFieldWithGetPrefixedGetter() {
            return optionalFieldWithGetPrefixedGetter;
        }

        public boolean isFieldWithIsPrefixedGetter() {
            return fieldWithIsPrefixedGetter;
        }

        public Optional<Boolean> isOptionalFieldWithIsPrefixedGetter() {
            return optionalFieldWithIsPrefixedGetter;
        }
    }
}