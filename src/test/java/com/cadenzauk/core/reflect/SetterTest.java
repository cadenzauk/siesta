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
import com.cadenzauk.core.reflect.util.ClassUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class SetterTest {
    @Test
    void cannotInstantiate() {
        calling(() -> Factory.forClass(Setter.class).get())
            .shouldThrow(RuntimeException.class)
            .withCause(InvocationTargetException.class)
            .withCause(RuntimeInstantiationException.class);
    }

    @Test
    void forFieldNonOptionalWithNoSetter() throws Exception {
        BiConsumer<SetterTestClass,Optional<String>> setter
            = Setter.forField(SetterTestClass.class, String.class, ClassUtil.getDeclaredField(SetterTestClass.class, "string"));
        SetterTestClass target = new SetterTestClass();

        setter.accept(target, Optional.of("Trevor"));

        assertThat(target.string, is("Trevor"));
    }

    @Test
    void forFieldOptionalWithNoSetter() throws Exception {
        BiConsumer<SetterTestClass,Optional<Long>> setter
            = Setter.forField(SetterTestClass.class, Long.class, ClassUtil.getDeclaredField(SetterTestClass.class, "optionalLong"));
        SetterTestClass target = new SetterTestClass();

        setter.accept(target, Optional.of(628318530717L));

        assertThat(target.optionalLong, is(Optional.of(628318530717L)));
    }

    @Test
    void forFieldNonOptionalAndWithPrefixSetter() throws Exception {
        BiConsumer<SetterTestClass,Optional<LocalDate>> setter
            = Setter.forField(SetterTestClass.class, LocalDate.class, ClassUtil.getDeclaredField(SetterTestClass.class, "localDate"));
        SetterTestClass target = mock(SetterTestClass.class);

        setter.accept(target, Optional.of(LocalDate.of(2001, Month.JANUARY, 1)));

        verify(target).withLocalDate(LocalDate.of(2001, Month.JANUARY, 1));
        verifyNoMoreInteractions(target);
    }

    @Test
    void forFieldOptionalAndWithPrefixSetter() throws Exception {
        BiConsumer<SetterTestClass,Optional<String>> setter
            = Setter.forField(SetterTestClass.class, String.class, ClassUtil.getDeclaredField(SetterTestClass.class, "optionalString"));
        SetterTestClass target = mock(SetterTestClass.class);

        setter.accept(target, Optional.of("Bruce"));

        verify(target).withOptionalString(Optional.of("Bruce"));
        verifyNoMoreInteractions(target);
    }

    @Test
    void forFieldNonOptionalAndSetPrefixSetter() throws Exception {
        BiConsumer<SetterTestClass,Optional<Integer>> setter
            = Setter.forField(SetterTestClass.class, Integer.class, ClassUtil.getDeclaredField(SetterTestClass.class, "integer"));
        SetterTestClass target = mock(SetterTestClass.class);

        setter.accept(target, Optional.of(55));

        verify(target).setInteger(55);
        verifyNoMoreInteractions(target);
    }

    @Test
    void forFieldOptionalAndSetPrefixSetter() throws Exception {
        BiConsumer<SetterTestClass,Optional<BigDecimal>> setter
            = Setter.forField(SetterTestClass.class, BigDecimal.class, ClassUtil.getDeclaredField(SetterTestClass.class, "optionalBigDecimal"));
        SetterTestClass target = mock(SetterTestClass.class);

        setter.accept(target, Optional.of(BigDecimal.ZERO));

        verify(target).setOptionalBigDecimal(Optional.of(BigDecimal.ZERO));
        verifyNoMoreInteractions(target);
    }

    @Test
    void forFieldNonOptionalAndunprefixedSetter() throws Exception {
        BiConsumer<SetterTestClass,Optional<Character>> setter
            = Setter.forField(SetterTestClass.class, Character.class, ClassUtil.getDeclaredField(SetterTestClass.class, "character"));
        SetterTestClass target = mock(SetterTestClass.class);

        setter.accept(target, Optional.of('z'));

        verify(target).character('z');
        verifyNoMoreInteractions(target);
    }

    @Test
    void forFieldOptionalAndSetUnprefixedSetter() throws Exception {
        BiConsumer<SetterTestClass,Optional<UUID>> setter
            = Setter.forField(SetterTestClass.class, UUID.class, ClassUtil.getDeclaredField(SetterTestClass.class, "optionalUuid"));
        SetterTestClass target = mock(SetterTestClass.class);
        UUID uuid = UUID.randomUUID();

        setter.accept(target, Optional.of(uuid));

        verify(target).optionalUuid(Optional.of(uuid));
        verifyNoMoreInteractions(target);
    }

    @Test
    void forFieldOfWrongTypeFieldThrows() {
        calling(() -> Setter.forField(SetterTestClass.class, Integer.class, ClassUtil.getDeclaredField(SetterTestClass.class, "string")))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage(is("Cannot convert private java.lang.String com.cadenzauk.core.reflect.SetterTest$SetterTestClass.string into " +
                "a BiConsumer<class com.cadenzauk.core.reflect.SetterTest$SetterTestClass,Optional<class java.lang.Integer>>."));
    }

    @Test
    void forFieldOfWrongTypeMethodThrows() {
        calling(() -> Setter.forField(SetterTestClass.class, String.class, ClassUtil.getDeclaredField(SetterTestClass.class, "optionalBigDecimal")))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage(is("Cannot convert void com.cadenzauk.core.reflect.SetterTest$SetterTestClass.setOptionalBigDecimal(java.util.Optional) into " +
                "a BiConsumer<class com.cadenzauk.core.reflect.SetterTest$SetterTestClass,Optional<class java.lang.String>>."));
    }

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    static class SetterTestClass {
        private String string;
        private Optional<Long> optionalLong;
        private LocalDate localDate;
        private Optional<String> optionalString;
        private Integer integer;
        private Optional<BigDecimal> optionalBigDecimal;
        private Character character;
        private Optional<UUID> optionalUuid;

        SetterTestClass withLocalDate(LocalDate localDate) {
            this.localDate = localDate;
            return this;
        }

        SetterTestClass withOptionalString(Optional<String> optionalString) {
            this.optionalString = optionalString;
            return this;
        }

        void setInteger(Integer integer) {
            this.integer = integer;
        }

        void setOptionalBigDecimal(Optional<BigDecimal> optionalBigDecimal) {
            this.optionalBigDecimal = optionalBigDecimal;
        }

        void character(Character character) {
            this.character = character;
        }

        void optionalUuid(Optional<UUID> optionalUuid) {
            this.optionalUuid = optionalUuid;
        }
    }
}