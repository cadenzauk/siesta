/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.ddl.action;

import java.util.Objects;

public abstract class LoggableAction extends Action {
    private final String definition;
    private final String id;
    private final String author;
    private final boolean logged;

    protected LoggableAction(Builder builder) {
        definition = builder.definition;
        id = builder.id;
        author = builder.author == null ? System.getProperty("user.name") : builder.author;
        logged = builder.logged;

        if (logged) {
            Objects.requireNonNull(definition, "Missing definition()");
            Objects.requireNonNull(id, "Missing id()");
        }
    }

    public String definition() {
        return definition;
    }

    public String id() {
        return id;
    }

    public String author() {
        return author;
    }

    public boolean logged() {
        return logged;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + id + ")";
    }

    public static class Builder<B extends Builder<B>>  {
        private String definition;
        protected String id;
        private String author;
        private boolean logged = true;

        protected Builder() {
        }

        public B definition(String val) {
            definition = val;
            return self();
        }

        public B id(String val) {
            id = val;
            return self();
        }

        public B author(String val) {
            author = val;
            return self();
        }

        public B logged(boolean val) {
            logged = val;
            return self();
        }

        @SuppressWarnings("unchecked")
        private B self() {
            return (B)this;
        }
    }
}
