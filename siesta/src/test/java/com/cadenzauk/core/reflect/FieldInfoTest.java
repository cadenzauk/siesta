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
import com.cadenzauk.core.sql.QualifiedName;
import com.google.common.reflect.TypeToken;
import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class FieldInfoTest {
    @Test
    void equalsAndHashCode() {
        new EqualsTester()
            .addEqualityGroup(FieldInfo.of(ClassWithField.class, optionalStringField()), FieldInfo.of(ClassWithField.class, optionalStringField()))
            .addEqualityGroup(FieldInfo.of(ClassWithField.class, stringField()), FieldInfo.of(ClassWithField.class, stringField()), FieldInfo.of(ClassWithField.class, stringField()))
            .testEquals();
    }

    @Test
    void toStringGeneric() {
        Field field = optionalStringField();
        FieldInfo<ClassWithField,?> fieldInfo = FieldInfo.of(ClassWithField.class, field);

        String result = fieldInfo.toString();

        assertThat(result, is("Optional<String> ClassWithField.optionalString"));
    }

    @Test
    void toStringNonGeneric() {
        Field field = stringField();
        FieldInfo<ClassWithField,?> fieldInfo = FieldInfo.of(ClassWithField.class, field);

        String result = fieldInfo.toString();

        assertThat(result, is("String ClassWithField.string"));
    }

    @Test
    void optionalGetterOnOptionalField() {
        Field field = optionalStringField();
        FieldInfo<ClassWithField,?> fieldInfo = FieldInfo.of(ClassWithField.class, field);
        ClassWithField target = mock(ClassWithField.class);
        when(target.optionalString()).thenReturn(Optional.of("Bobby"));

        Optional<?> result = fieldInfo.optionalGetter().apply(target);

        assertThat(result, is(Optional.of("Bobby")));
        verify(target).optionalString();
        verifyNoMoreInteractions(target);
    }

    @Test
    void optionalGetterOnNonOptionalField() {
        Field field = stringField();
        FieldInfo<ClassWithField,?> fieldInfo = FieldInfo.of(ClassWithField.class, field);
        ClassWithField target = mock(ClassWithField.class);
        when(target.string()).thenReturn("Freddy");

        Optional<?> result = fieldInfo.optionalGetter().apply(target);

        assertThat(result, is(Optional.of("Freddy")));
        verify(target).string();
        verifyNoMoreInteractions(target);
    }

    @Test
    void ofOptionalWithoutType() {
        Field field = optionalStringField();

        FieldInfo<ClassWithField,?> fieldInfo = FieldInfo.of(ClassWithField.class, field);

        assertThat(fieldInfo.declaringType(), equalTo(TypeToken.of(ClassWithField.class)));
        assertThat(fieldInfo.declaringClass(), equalTo(ClassWithField.class));
        assertThat(fieldInfo.name(), equalTo("optionalString"));
        assertThat(fieldInfo.field(), sameInstance(field));
        assertThat(fieldInfo.fieldType(), equalTo(Optional.class));
        assertThat(fieldInfo.effectiveClass(), equalTo(String.class));
    }

    @Test
    void ofNonOptionalWithoutType() {
        Field field = stringField();

        FieldInfo<ClassWithField,?> fieldInfo = FieldInfo.of(ClassWithField.class, field);

        assertThat(fieldInfo.declaringType(), equalTo(TypeToken.of(ClassWithField.class)));
        assertThat(fieldInfo.declaringClass(), equalTo(ClassWithField.class));
        assertThat(fieldInfo.name(), equalTo("string"));
        assertThat(fieldInfo.field(), sameInstance(field));
        assertThat(fieldInfo.fieldType(), equalTo(String.class));
        assertThat(fieldInfo.effectiveClass(), equalTo(String.class));
    }

    @Test
    void ofOptionalWithType() {
        Field field = optionalStringField();

        FieldInfo<ClassWithField,String> fieldInfo = FieldInfo.of(ClassWithField.class, field, String.class);

        assertThat(fieldInfo.declaringType(), equalTo(TypeToken.of(ClassWithField.class)));
        assertThat(fieldInfo.declaringClass(), equalTo(ClassWithField.class));
        assertThat(fieldInfo.name(), equalTo("optionalString"));
        assertThat(fieldInfo.field(), sameInstance(field));
        assertThat(fieldInfo.fieldType(), equalTo(Optional.class));
        assertThat(fieldInfo.effectiveClass(), equalTo(String.class));
    }

    @Test
    void ofNonOptionalWithType() {
        Field field = stringField();

        FieldInfo<ClassWithField,String> fieldInfo = FieldInfo.of(ClassWithField.class, field, String.class);

        assertThat(fieldInfo.declaringType(), equalTo(TypeToken.of(ClassWithField.class)));
        assertThat(fieldInfo.declaringClass(), equalTo(ClassWithField.class));
        assertThat(fieldInfo.name(), equalTo("string"));
        assertThat(fieldInfo.field(), sameInstance(field));
        assertThat(fieldInfo.fieldType(), equalTo(String.class));
        assertThat(fieldInfo.effectiveClass(), equalTo(String.class));
    }

    @Test
    void ofOptionalByName() {
        FieldInfo<ClassWithField,String> fieldInfo = FieldInfo.of(ClassWithField.class, "optionalString", String.class);

        assertThat(fieldInfo.declaringType(), equalTo(TypeToken.of(ClassWithField.class)));
        assertThat(fieldInfo.declaringClass(), equalTo(ClassWithField.class));
        assertThat(fieldInfo.name(), equalTo("optionalString"));
        assertThat(fieldInfo.fieldType(), equalTo(Optional.class));
        assertThat(fieldInfo.effectiveClass(), equalTo(String.class));
    }

    @Test
    void ofNonOptionalByName() {
        FieldInfo<ClassWithField,String> fieldInfo = FieldInfo.of(ClassWithField.class, "string", String.class);

        assertThat(fieldInfo.declaringType(), equalTo(TypeToken.of(ClassWithField.class)));
        assertThat(fieldInfo.declaringClass(), equalTo(ClassWithField.class));
        assertThat(fieldInfo.name(), equalTo("string"));
        assertThat(fieldInfo.fieldType(), equalTo(String.class));
        assertThat(fieldInfo.effectiveClass(), equalTo(String.class));
    }

    @Test
    void ofWrongTypeThrowsException() {
        calling(() -> FieldInfo.of(ClassWithField.class, "string", Integer.class))
            .shouldThrow(NoSuchElementException.class)
            .withMessage(is("No field called string of type class java.lang.Integer in class com.cadenzauk.core.reflect.FieldInfoTest$ClassWithField"));
    }

    @Test
    void ofOptionalGetter() {
        FieldInfo<ClassWithField,String> fieldInfo = FieldInfo.ofGetter(MethodInfo.of(ClassWithField::optionalString))
            .orElseThrow(() -> new AssertionError("Should have got FieldInfo for optionalString but didn't"));

        assertThat(fieldInfo.declaringType(), equalTo(TypeToken.of(ClassWithField.class)));
        assertThat(fieldInfo.declaringClass(), equalTo(ClassWithField.class));
        assertThat(fieldInfo.name(), equalTo("optionalString"));
        assertThat(fieldInfo.fieldType(), equalTo(Optional.class));
        assertThat(fieldInfo.effectiveClass(), equalTo(String.class));
    }

    @Test
    void ofGetter() {
        FieldInfo<ClassWithField,String> fieldInfo = FieldInfo.ofGetter(MethodInfo.of(ClassWithField::string))
            .orElseThrow(() -> new AssertionError("Should have got FieldInfo for string but didn't"));

        assertThat(fieldInfo.declaringType(), equalTo(TypeToken.of(ClassWithField.class)));
        assertThat(fieldInfo.declaringClass(), equalTo(ClassWithField.class));
        assertThat(fieldInfo.name(), equalTo("string"));
        assertThat(fieldInfo.fieldType(), equalTo(String.class));
        assertThat(fieldInfo.effectiveClass(), equalTo(String.class));
    }

    @Test
    void annotationPresent() {
        FieldInfo<ClassWithField,String> fieldInfo = FieldInfo.ofGetter(MethodInfo.of(ClassWithField::string))
            .orElseThrow(() -> new AssertionError("Should have got FieldInfo for string but didn't"));

        Optional<XmlElement> result = fieldInfo.annotation(XmlElement.class);

        assertThat(result.isPresent(), is(true));
        assertThat(result.map(XmlElement::name), is(Optional.of("horace")));
    }

    @Test
    void annotationNotPresent() {
        FieldInfo<ClassWithField,String> fieldInfo = FieldInfo.ofGetter(MethodInfo.of(ClassWithField::optionalString))
            .orElseThrow(() -> new AssertionError("Should have got FieldInfo for optionalString but didn't"));

        Optional<XmlAttribute> result = fieldInfo.annotation(XmlAttribute.class);

        assertThat(result, is(Optional.empty()));
    }

    @Test
    void optionalGetterForExtendingClassIsFromAbstractClass() {

        AnExtendingClass extendingClassMock = Mockito.mock(AnExtendingClass.class);
        when(extendingClassMock.field()).thenReturn("ABC");

        FieldInfo<AnAbstractClass,String> fieldInfoAbstractClass = FieldInfo.of(AnAbstractClass.class, "field", String.class);
        FieldInfo<AnExtendingClass,String> fieldInfoExtendingClass = FieldInfo.of(AnExtendingClass.class, "field", String.class);

        Optional<String> fieldAbstract = fieldInfoAbstractClass.optionalGetter().apply(extendingClassMock);
        Optional<String> fieldExtending = fieldInfoExtendingClass.optionalGetter().apply(extendingClassMock);

        assertThat(fieldAbstract, is(Optional.of("ABC")));
        assertThat(fieldExtending, is(Optional.of("ABC")));
    }

    private Field stringField() {
        return ClassUtil.getDeclaredField(ClassWithField.class, "string");
    }

    private Field optionalStringField() {
        return ClassUtil.getDeclaredField(ClassWithField.class, "optionalString");
    }

    @SuppressWarnings("unused")
    private static class ClassWithField {
        private Optional<String> optionalString;
        @XmlElement(name = "horace")
        private String string;

        Optional<String> optionalString() {
            return optionalString;
        }

        String string() {
            return string;
        }
    }

    @SuppressWarnings("unused")
    private static abstract class AnAbstractClass {
        private String field;

        protected AnAbstractClass(String field){
            this.field = field;
        }

        String field() {
            return field;
        }
    }

    @SuppressWarnings("unused")
    private static class AnExtendingClass extends AnAbstractClass {

        public AnExtendingClass(String field){
            super(field);
        }
    }

}
