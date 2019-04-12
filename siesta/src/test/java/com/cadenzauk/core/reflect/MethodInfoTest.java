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
import com.google.common.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import javax.xml.bind.annotation.XmlElement;
import java.util.Optional;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
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

        assertThat(result.referringClass(), equalTo(MethodInfoTestClass.class));
        assertThat(result.method(), notNullValue());
        assertThat(result.method(), equalTo(ClassUtil.getDeclaredMethod(MethodInfoTestClass.class, "string")));
        assertThat(result.actualType(), equalTo(String.class));
        assertThat(result.effectiveClass(), equalTo(String.class));
    }

    @Test
    void ofOptional() {
        MethodInfo<MethodInfoTestClass,Integer> result = MethodInfo.of(MethodInfoTestClass::optionalInteger);

        assertThat(result.referringClass(), equalTo(MethodInfoTestClass.class));
        assertThat(result.method(), notNullValue());
        assertThat(result.method(), equalTo(ClassUtil.getDeclaredMethod(MethodInfoTestClass.class, "optionalInteger")));
        assertThat(result.actualType(), equalTo(Optional.class));
        assertThat(result.effectiveClass(), equalTo(Integer.class));
    }

    @Test
    void findGetterForSuperclass() {
        MethodInfo<MethodInfoTestClass,String> result = MethodInfo.of(MethodInfoTestClass::name);

        assertThat(result.referringClass(), equalTo(MethodInfoTestClass.class));
    }

    @Test
    void findOptionalGetterForSuperclass() {
        MethodInfo<MethodInfoTestClass,String> result = MethodInfo.of(MethodInfoTestClass::optionalName);

        assertThat(result.referringClass(), equalTo(MethodInfoTestClass.class));
    }

    @Test
    void effectiveTypeReturnsCorrectValue() {
        MethodInfo<MethodInfoTestClass,String> sut = MethodInfo.of(MethodInfoTestClass::optionalName);

        TypeToken<String> result = sut.effectiveType();

        assertThat(result, is(TypeToken.of(String.class)));
    }

    @Test
    void declaringTypeReturnsTheCorrectValueForSuperClass() {
        MethodInfo<MethodInfoTestClass,String> sut = MethodInfo.of(MethodInfoTestClass::optionalName);

        TypeToken<? super MethodInfoTestClass> result = sut.declaringType();

        assertThat(result, is(TypeToken.of(MethodInfoSuperClass.class)));
    }

    @Test
    void referringTypeReturnsTheCorrectValueForSuperClassMethod() {
        MethodInfo<MethodInfoSuperClass,String> sut = MethodInfo.of(MethodInfoSuperClass::optionalName);

        TypeToken<MethodInfoSuperClass> result = sut.referringType();

        assertThat(result, is(TypeToken.of(MethodInfoSuperClass.class)));
    }

    @Test
    void referringTypeReturnsTheCorrectValueForSuperClassMethodAsSubclass() {
        MethodInfo<MethodInfoTestClass,String> sut = MethodInfo.of(MethodInfoSuperClass::optionalName);

        TypeToken<MethodInfoTestClass> result = sut.referringType();

        assertThat(result, is(TypeToken.of(MethodInfoTestClass.class)));
    }

    @Test
    void referringTypeReturnsTheCorrectValueForSuperClassMethodInSubclass() {
        MethodInfo<MethodInfoTestClass,String> sut = MethodInfo.of(MethodInfoTestClass::optionalName);

        TypeToken<? super MethodInfoTestClass> result = sut.referringType();

        assertThat(result, is(TypeToken.of(MethodInfoTestClass.class)));
    }

    @Test
    void fieldForGetterWithNoPrefixReturnsField() {
        MethodInfo<MethodInfoTestClass,String> sut = MethodInfo.of(MethodInfoTestClass::noPrefix);

        Optional<FieldInfo<MethodInfoTestClass,?>> result = sut.field();

        assertThat(result.map(FieldInfo::name), is(Optional.of("noPrefix")));
    }

    @Test
    void fieldForGetterWithGetPrefixReturnsField() {
        MethodInfo<MethodInfoTestClass,Integer> sut = MethodInfo.of(MethodInfoTestClass::getPrefixedWithGet);

        Optional<FieldInfo<MethodInfoTestClass,?>> result = sut.field();

        assertThat(result.map(FieldInfo::name), is(Optional.of("prefixedWithGet")));
    }

    @Test
    void fieldForGetterWithIsPrefixReturnsField() {
        MethodInfo<MethodInfoTestClass,Boolean> sut = MethodInfo.of(MethodInfoTestClass::isPrefixedWithIs);

        Optional<FieldInfo<MethodInfoTestClass,?>> result = sut.field();

        assertThat(result.map(FieldInfo::name), is(Optional.of("prefixedWithIs")));
    }

    @Test
    void fieldForFieldlessGetterWithNoPrefixReturnsEmpty() {
        MethodInfo<MethodInfoTestClass,String> sut = MethodInfo.of(MethodInfoTestClass::fieldlessNoPrefix);

        Optional<FieldInfo<MethodInfoTestClass,?>> result = sut.field();

        assertThat(result, is(Optional.empty()));
    }

    @Test
    void fieldForFieldlessGetterWithGetPrefixReturnsField() {
        MethodInfo<MethodInfoTestClass,Integer> sut = MethodInfo.of(MethodInfoTestClass::getFieldlessPrefixedWithGet);

        Optional<FieldInfo<MethodInfoTestClass,?>> result = sut.field();

        assertThat(result, is(Optional.empty()));
    }

    @Test
    void fieldForFieldlessGetterWithIsPrefixReturnsField() {
        MethodInfo<MethodInfoTestClass,Boolean> sut = MethodInfo.of(MethodInfoTestClass::isFieldlessPrefixedWithIs);

        Optional<FieldInfo<MethodInfoTestClass,?>> result = sut.field();

        assertThat(result, is(Optional.empty()));
    }

    @Test
    void propertyNameForGetterWithNoPrefixReturnsFieldName() {
        MethodInfo<MethodInfoTestClass,String> sut = MethodInfo.of(MethodInfoTestClass::noPrefix);

        String result = sut.propertyName();

        assertThat(result, is("noPrefix"));
    }

    @Test
    void propertyNameForGetterWithGetPrefixReturnsFieldName() {
        MethodInfo<MethodInfoTestClass,Integer> sut = MethodInfo.of(MethodInfoTestClass::getPrefixedWithGet);

        String result = sut.propertyName();

        assertThat(result, is("prefixedWithGet"));
    }

    @Test
    void propertyNameForGetterWithIsPrefixReturnsFieldName() {
        MethodInfo<MethodInfoTestClass,Boolean> sut = MethodInfo.of(MethodInfoTestClass::isPrefixedWithIs);

        String result = sut.propertyName();

        assertThat(result, is("prefixedWithIs"));
    }

    @Test
    void propertyNameForFieldlessGetterWithNoPrefixReturnsMethodName() {
        MethodInfo<MethodInfoTestClass,String> sut = MethodInfo.of(MethodInfoTestClass::fieldlessNoPrefix);

        String result = sut.propertyName();

        assertThat(result, is("fieldlessNoPrefix"));
    }

    @Test
    void propertyNameForFieldlessGetterWithGetPrefixReturnsNameWithoutGet() {
        MethodInfo<MethodInfoTestClass,Integer> sut = MethodInfo.of(MethodInfoTestClass::getFieldlessPrefixedWithGet);

        String result = sut.propertyName();

        assertThat(result, is("fieldlessPrefixedWithGet"));
    }

    @Test
    void propertyNameForFieldlessGetterWithIsPrefixReturnsMethodName() {
        MethodInfo<MethodInfoTestClass,Boolean> sut = MethodInfo.of(MethodInfoTestClass::isFieldlessPrefixedWithIs);

        String result = sut.propertyName();

        assertThat(result, is("isFieldlessPrefixedWithIs"));
    }

    private static class MethodInfoSuperClass {
        @SuppressWarnings("SameReturnValue")
        String name() {
            return "name";
        }

        Optional<String> optionalName() {
            return Optional.of("name");
        }
    }

    @SuppressWarnings({"unused", "SameReturnValue"})
    private static class MethodInfoTestClass extends MethodInfoSuperClass {
        private String noPrefix;
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

        String noPrefix() {
            return noPrefix;
        }

        Optional<Integer> getPrefixedWithGet() {
            return prefixedWithGet;
        }

        boolean isPrefixedWithIs() {
            return prefixedWithIs;
        }

        String fieldlessNoPrefix() {
            return "";
        }

        Optional<Integer> getFieldlessPrefixedWithGet() {
            return Optional.empty();
        }

        boolean isFieldlessPrefixedWithIs() {
            return false;
        }
    }
}