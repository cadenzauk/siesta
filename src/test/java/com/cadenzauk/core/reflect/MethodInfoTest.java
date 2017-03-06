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
import org.junit.Test;

import javax.xml.bind.annotation.XmlElement;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class MethodInfoTest {
    @Test
    public void annotationPresent() throws Exception {
        MethodInfo<MethodInfoTestClass,String> methodInfo = MethodInfo.of(MethodInfoTestClass::methodWithAnnotation);

        Optional<XmlElement> annotation = methodInfo.annotation(XmlElement.class);

        assertThat(annotation.isPresent(), is(true));
    }

    @Test
    public void annotationNotPresent() throws Exception {
        MethodInfo<MethodInfoTestClass,String> methodInfo = MethodInfo.of(MethodInfoTestClass::methodWithoutAnnotation);

        Optional<XmlElement> annotation = methodInfo.annotation(XmlElement.class);

        assertThat(annotation.isPresent(), is(false));
    }

    @Test
    public void findGetterForFieldNoPrefix() throws Exception {
        Optional<MethodInfo<MethodInfoTestClass,String>> noprefix = MethodInfo.findGetterForField(FieldInfo.of(MethodInfoTestClass.class, "noprefix", String.class));

        assertThat(noprefix.isPresent(), is(true));
        assertThat(noprefix.map(MethodInfo::method), is(Optional.of(ClassUtil.getDeclaredMethod(MethodInfoTestClass.class, "noprefix"))));
    }

    @Test
    public void findGetterForFieldGetPrefix() throws Exception {
        Optional<MethodInfo<MethodInfoTestClass,Integer>> noprefix = MethodInfo.findGetterForField(FieldInfo.of(MethodInfoTestClass.class, "prefixedWithGet", Integer.class));

        assertThat(noprefix.isPresent(), is(true));
        assertThat(noprefix.map(MethodInfo::method), is(Optional.of(ClassUtil.getDeclaredMethod(MethodInfoTestClass.class, "getPrefixedWithGet"))));
    }

    @Test
    public void findGetterForFieldIsPrefix() throws Exception {
        Optional<MethodInfo<MethodInfoTestClass,Boolean>> noprefix = MethodInfo.findGetterForField(FieldInfo.of(MethodInfoTestClass.class, "prefixedWithIs", Boolean.TYPE));

        assertThat(noprefix.isPresent(), is(true));
        assertThat(noprefix.map(MethodInfo::method), is(Optional.of(ClassUtil.getDeclaredMethod(MethodInfoTestClass.class, "isPrefixedWithIs"))));
    }

    @Test
    public void ofNonOptional() throws Exception {
        MethodInfo<MethodInfoTestClass,String> result = MethodInfo.of(MethodInfoTestClass::string);

        assertThat(result.declaringClass(), equalTo(MethodInfoTestClass.class));
        assertThat(result.method(), notNullValue());
        assertThat(result.method(), equalTo(ClassUtil.getDeclaredMethod(MethodInfoTestClass.class, "string")));
        assertThat(result.actualType(), equalTo(String.class));
        assertThat(result.effectiveType(), equalTo(String.class));
    }

    @Test
    public void ofOptional() throws Exception {
        MethodInfo<MethodInfoTestClass,Integer> result = MethodInfo.of(MethodInfoTestClass::optionalInteger);

        assertThat(result.declaringClass(), equalTo(MethodInfoTestClass.class));
        assertThat(result.method(), notNullValue());
        assertThat(result.method(), equalTo(ClassUtil.getDeclaredMethod(MethodInfoTestClass.class, "optionalInteger")));
        assertThat(result.actualType(), equalTo(Optional.class));
        assertThat(result.effectiveType(), equalTo(Integer.class));
    }

    private static class MethodInfoTestClass {
        private String noprefix;
        private Optional<Integer> prefixedWithGet;
        private boolean prefixedWithIs;

        String string() {
            return "a string";
        }

        Optional<Integer> optionalInteger() {
            return Optional.empty();
        }

        @XmlElement
        String methodWithAnnotation() {
            return "xmlElement";
        }

        String methodWithoutAnnotation() {
            return "xmlElement";
        }

        public String noprefix() {
            return noprefix;
        }

        public Optional<Integer> getPrefixedWithGet() {
            return prefixedWithGet;
        }

        public boolean isPrefixedWithIs() {
            return prefixedWithIs;
        }
    }
}