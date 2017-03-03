/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.reflect.util;

import com.cadenzauk.core.lang.RuntimeInstantiationException;
import com.cadenzauk.core.reflect.Factory;
import org.junit.Test;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FieldUtilTest {
    @Test
    public void cannotInstantiate() {
        calling(() -> Factory.forClass(FieldUtil.class).get())
            .shouldThrow(RuntimeException.class)
            .withCause(InvocationTargetException.class)
            .withCause(RuntimeInstantiationException.class);
    }

    @Test
    public void set() throws Exception {
        ClassWithStringField target = new ClassWithStringField();
        String value = UUID.randomUUID().toString();
        Field field = ClassUtil.getDeclaredField(ClassWithStringField.class, "stringField");

        FieldUtil.set(field, target, value);

        assertThat(target.getStringField(), equalTo(value));
    }

    @Test
    public void get() throws Exception {
        ClassWithStringField target = new ClassWithStringField();
        String value = UUID.randomUUID().toString();
        target.setStringField(value);
        Field field = ClassUtil.getDeclaredField(ClassWithStringField.class, "stringField");

        Object result = FieldUtil.get(field, target);

        assertThat(result, equalTo(value));
    }

    @Test
    public void hasAnnotationPresent() throws Exception {
        Field field = ClassUtil.getDeclaredField(ClassWithStringField.class, "stringField");

        boolean result = FieldUtil.hasAnnotation(XmlElement.class, field);

        assertThat(result, is(true));
    }

    @Test
    public void hasAnnotationNotPresent() throws Exception {
        Field field = ClassUtil.getDeclaredField(ClassWithStringField.class, "stringField");

        boolean result = FieldUtil.hasAnnotation(XmlAttribute.class, field);

        assertThat(result, is(false));
    }

    private static class ClassWithStringField {
        @XmlElement
        private String stringField;

        public String getStringField() {
            return stringField;
        }

        public void setStringField(String stringField) {
            this.stringField = stringField;
        }
    }

}
