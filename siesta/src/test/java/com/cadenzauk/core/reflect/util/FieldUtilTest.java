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

package com.cadenzauk.core.reflect.util;

import org.junit.jupiter.api.Test;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static com.cadenzauk.core.testutil.IsUtilityClass.isUtilityClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class FieldUtilTest {
    @Test
    void isUtility() {
       assertThat(FieldUtil.class, isUtilityClass());
    }

    @Test
    void set() {
        ClassWithStringField target = new ClassWithStringField();
        String value = UUID.randomUUID().toString();
        Field field = ClassUtil.getDeclaredField(ClassWithStringField.class, "stringField");

        FieldUtil.set(field, target, value);

        assertThat(target.getStringField(), equalTo(value));
    }

    @Test
    void get() {
        ClassWithStringField target = new ClassWithStringField();
        String value = UUID.randomUUID().toString();
        target.setStringField(value);
        Field field = ClassUtil.getDeclaredField(ClassWithStringField.class, "stringField");

        Object result = FieldUtil.get(field, target);

        assertThat(result, equalTo(value));
    }

    @Test
    void getFromObjectSuccess() {
       ClassWithStringField target = new ClassWithStringField();
        String value = UUID.randomUUID().toString();
        target.setStringField(value);

        Object result = FieldUtil.get("stringField", target);

        assertThat(result, equalTo(value));
    }

    @Test
    void getFromObjectError() {
       ClassWithStringField target = new ClassWithStringField();

        calling(() -> FieldUtil.get("rubbish", target))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage(is("No such field as rubbish in class com.cadenzauk.core.reflect.util.FieldUtilTest$ClassWithStringField"));
    }

    @Test
    void genericTypeArgumentSuccess() {
        Optional<Class<?>> result = FieldUtil.genericTypeArgument(ClassUtil.getDeclaredField(ClassWithStringField.class, "optionalStringField"), 0);

        assertThat(result, is(Optional.of(String.class)));
    }

    @Test
    void genericTypeArgumentNotGeneric() {
        Optional<Class<?>> result = FieldUtil.genericTypeArgument(ClassUtil.getDeclaredField(ClassWithStringField.class, "stringField"), 0);

        assertThat(result, is(Optional.empty()));
    }

    @Test
    void genericTypeArgumentInvalidIndex() {
        calling(() -> FieldUtil.genericTypeArgument(ClassUtil.getDeclaredField(ClassWithStringField.class, "optionalStringField"), 1))
            .shouldThrow(ArrayIndexOutOfBoundsException.class);
    }

    @Test
    void hasAnnotationPresent() {
        Field field = ClassUtil.getDeclaredField(ClassWithStringField.class, "stringField");

        boolean result = FieldUtil.hasAnnotation(XmlElement.class, field);

        assertThat(result, is(true));
    }

    @Test
    void hasAnnotationNotPresent() {
        Field field = ClassUtil.getDeclaredField(ClassWithStringField.class, "stringField");

        boolean result = FieldUtil.hasAnnotation(XmlAttribute.class, field);

        assertThat(result, is(false));
    }

    @Test
    void annotationPresent() {
        Field field = ClassUtil.getDeclaredField(ClassWithStringField.class, "stringField");

        Optional<XmlElement> result = FieldUtil.annotation(XmlElement.class, field);

        assertThat(result.isPresent(), is(true));
    }

    @Test
    void annotationNotPresent() {
        Field field = ClassUtil.getDeclaredField(ClassWithStringField.class, "stringField");

        Optional<XmlAttribute> result = FieldUtil.annotation(XmlAttribute.class, field);

        assertThat(result, is(Optional.empty()));
    }

    private static class ClassWithStringField {
        @XmlElement
        private String stringField;

        private Optional<String> optionalStringField;

        public String getStringField() {
            return stringField;
        }

        public void setStringField(String stringField) {
            this.stringField = stringField;
        }
    }

}
