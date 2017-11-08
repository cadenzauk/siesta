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

import com.cadenzauk.core.reflect.util.ClassUtil;
import org.junit.jupiter.api.Test;

import javax.xml.bind.annotation.XmlElement;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class MethodInfoTest {
    @Test
    void annotationPresent() {
        MethodInfo<MethodInfoTestClass,String> methodInfo = MethodInfo.of(MethodInfoTestClass::methodWithAnnotation);

        Optional<XmlElement> annotation = methodInfo.annotation(XmlElement.class);

        assertThat(annotation.isPresent(), is(true));
    }

    @Test
    void annotationNotPresent() {
        MethodInfo<MethodInfoTestClass,String> methodInfo = MethodInfo.of(MethodInfoTestClass::methodWithoutAnnotation);

        Optional<XmlElement> annotation = methodInfo.annotation(XmlElement.class);

        assertThat(annotation.isPresent(), is(false));
    }

    @Test
    void findGetterForFieldNoPrefix() {
        Optional<MethodInfo<MethodInfoTestClass,String>> noPrefix = MethodInfo.findGetterForField(FieldInfo.of(MethodInfoTestClass.class, "noPrefix", String.class));

        assertThat(noPrefix.isPresent(), is(true));
        assertThat(noPrefix.map(MethodInfo::method), is(Optional.of(ClassUtil.getDeclaredMethod(MethodInfoTestClass.class, "noPrefix"))));
    }

    @Test
    void findGetterForFieldGetPrefix() {
        Optional<MethodInfo<MethodInfoTestClass,Integer>> noPrefix = MethodInfo.findGetterForField(FieldInfo.of(MethodInfoTestClass.class, "prefixedWithGet", Integer.class));

        assertThat(noPrefix.isPresent(), is(true));
        assertThat(noPrefix.map(MethodInfo::method), is(Optional.of(ClassUtil.getDeclaredMethod(MethodInfoTestClass.class, "getPrefixedWithGet"))));
    }

    @Test
    void findGetterForFieldIsPrefix() {
        Optional<MethodInfo<MethodInfoTestClass,Boolean>> noPrefix = MethodInfo.findGetterForField(FieldInfo.of(MethodInfoTestClass.class, "prefixedWithIs", Boolean.TYPE));

        assertThat(noPrefix.isPresent(), is(true));
        assertThat(noPrefix.map(MethodInfo::method), is(Optional.of(ClassUtil.getDeclaredMethod(MethodInfoTestClass.class, "isPrefixedWithIs"))));
    }

    @Test
    void ofNonOptional() {
        MethodInfo<MethodInfoTestClass,String> result = MethodInfo.of(MethodInfoTestClass::string);

        assertThat(result.declaringClass(), equalTo(MethodInfoTestClass.class));
        assertThat(result.method(), notNullValue());
        assertThat(result.method(), equalTo(ClassUtil.getDeclaredMethod(MethodInfoTestClass.class, "string")));
        assertThat(result.actualType(), equalTo(String.class));
        assertThat(result.effectiveClass(), equalTo(String.class));
    }

    @Test
    void ofOptional() {
        MethodInfo<MethodInfoTestClass,Integer> result = MethodInfo.of(MethodInfoTestClass::optionalInteger);

        assertThat(result.declaringClass(), equalTo(MethodInfoTestClass.class));
        assertThat(result.method(), notNullValue());
        assertThat(result.method(), equalTo(ClassUtil.getDeclaredMethod(MethodInfoTestClass.class, "optionalInteger")));
        assertThat(result.actualType(), equalTo(Optional.class));
        assertThat(result.effectiveClass(), equalTo(Integer.class));
    }

    @SuppressWarnings("unused")
    private static class MethodInfoTestClass {
        private String noPrefix;
        private Optional<Integer> prefixedWithGet;
        private boolean prefixedWithIs;

        @SuppressWarnings("SameReturnValue")
        String string() {
            return "a string";
        }

        Optional<Integer> optionalInteger() {
            return Optional.empty();
        }

        @SuppressWarnings("SameReturnValue")
        @XmlElement
        String methodWithAnnotation() {
            return "xmlElement";
        }

        @SuppressWarnings("SameReturnValue")
        String methodWithoutAnnotation() {
            return "xmlElement";
        }

        String noPrefix() {
            return noPrefix;
        }

        Optional<Integer> getPrefixedWithGet() {
            return prefixedWithGet;
        }

        boolean isPrefixedWithIs() {
            return prefixedWithIs;
        }
    }
}