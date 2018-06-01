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

package com.cadenzauk.siesta.grammar.expression;

import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.Sequence;
import com.google.common.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SequenceExpressionTest {
    @Mock
    private Sequence<Integer> intSequence;

    @Mock
    private Scope scope;

    @Mock
    private RowMapper<Integer> rowMapper;

    @Test
    void label() {
        SequenceExpression<Integer> sut1 = new SequenceExpression<>(intSequence);
        SequenceExpression<Integer> sut2 = new SequenceExpression<>(intSequence);
        when(scope.newLabel()).thenReturn(563L).thenReturn(564L);

        String label1 = sut1.label(scope);
        String label2 = sut1.label(scope);
        String label3 = sut2.label(scope);

        assertThat(label1, is("sequence_563"));
        assertThat(label2, is("sequence_563"));
        assertThat(label3, is("sequence_564"));
    }

    @Test
    void rowMapper() {
        when(intSequence.rowMapper("fred")).thenReturn(rowMapper);
        SequenceExpression<Integer> sut = new SequenceExpression<>(intSequence);

        RowMapper<Integer> result = sut.rowMapper(scope, Optional.of("fred"));

        assertThat(result, sameInstance(rowMapper));
    }

    @Test
    void type() {
        TypeToken<Integer> intType = TypeToken.of(Integer.class);
        when(intSequence.type()).thenReturn(intType);
        SequenceExpression<Integer> sut = new SequenceExpression<>(intSequence);

        TypeToken<Integer> result = sut.type();

        assertThat(result, sameInstance(intType));
    }

    @Test
    void sql() {
        when(intSequence.sql()).thenReturn("sql to get sequence value");
        SequenceExpression<Integer> sut = new SequenceExpression<>(intSequence);

        String result = sut.sql(scope);

        assertThat(result, is("sql to get sequence value"));
    }

    @Test
    void args() {
        SequenceExpression<Integer> sut = new SequenceExpression<>(intSequence);

        List<Object> result = sut.args(scope).collect(toList());

        assertThat(result, iterableWithSize(0));
    }

    @Test
    void precedence() {
        SequenceExpression<Integer> sut = new SequenceExpression<>(intSequence);

        Precedence result = sut.precedence();

        assertThat(result, is(Precedence.UNARY));
    }

    @Test
    void single() {
        when(intSequence.single()).thenReturn(54321);
        SequenceExpression<Integer> sut = new SequenceExpression<>(intSequence);

        int result = sut.single();

        assertThat(result, is(54321));
    }

}