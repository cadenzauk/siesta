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

package com.cadenzauk.siesta.ddl.log.action;

import com.cadenzauk.siesta.ddl.action.Action;
import com.cadenzauk.siesta.ddl.log.intercept.ActionLogTableCreator;

import javax.persistence.Table;
import java.time.ZonedDateTime;

@Table(name = ActionLogTableCreator.LOG_TABLE_NAME)
public class ActionLogEntry extends Action {
    private final String definitionId;
    private final String actionId;
    private final String author;
    private final String executedBy;
    private final ZonedDateTime executionTs;

    private ActionLogEntry(Builder builder) {
        definitionId = builder.definitionId;
        actionId = builder.actionId;
        author = builder.author;
        executedBy = builder.executedBy;
        executionTs = builder.executionTs;
    }

    public String definitionId() {
        return definitionId;
    }

    public String actionId() {
        return actionId;
    }

    public String author() {
        return author;
    }

    public String executedBy() {
        return executedBy;
    }

    public ZonedDateTime executionTs() {
        return executionTs;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private ZonedDateTime executionTs;
        private String definitionId;
        private String actionId;
        private String author;
        private String executedBy;

        private Builder() {
        }

        public Builder definitionId(String val) {
            definitionId = val;
            return this;
        }

        public Builder actionId(String val) {
            actionId = val;
            return this;
        }

        public Builder author(String val) {
            author = val;
            return this;
        }

        public Builder executedBy(String val) {
            executedBy = val;
            return this;
        }

        public Builder executionTs(ZonedDateTime val) {
            executionTs = val;
            return this;
        }

        public ActionLogEntry build() {
            return new ActionLogEntry(this);
        }
    }
}
