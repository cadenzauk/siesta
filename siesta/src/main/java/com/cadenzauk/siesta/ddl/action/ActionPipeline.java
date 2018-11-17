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

import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Database;
import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.PriorityQueue;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

public class ActionPipeline {
    private static final Logger LOG = LoggerFactory.getLogger(ActionPipeline.class);
    private final PriorityQueue<Adapter<?>> interceptors = new PriorityQueue<>(comparing(Adapter::priority));

    public <T extends Action> void addInterceptor(ActionInterceptor<T> actionInterceptor) {
        interceptors.add(new Adapter<>(actionInterceptor));
    }

    public Stream<Action> process(Database database, Stream<Action> actionStream) {
        return actionStream
            .flatMap(action -> processAction(database, action));
    }

    private Stream<Action> processAction(Database database, Action action) {
        LOG.debug("Action {}", action);
        Stream<Action> stream = Stream.of(action);
        for (Adapter<?> interceptor : interceptors) {
            stream = stream.flatMap(a -> interceptor.intercept(database, a));
        }
        return stream;
    }

    private static class Adapter<T extends Action> {
        private final int priority;
        private final TypeToken<T> supportedType;
        private final ActionInterceptor<T> interceptor;

        private Adapter(ActionInterceptor<T> interceptor) {
            this.priority = interceptor.priority();
            this.supportedType = interceptor.supportedType();
            this.interceptor = interceptor;
        }

        private int priority() {
            return priority;
        }

        private Stream<Action> intercept(Database database, Action action) {
            LOG.trace("{} -> {}", action, interceptor);
            return OptionalUtil.as(supportedType, action)
                .filter(a -> interceptor.supports(database, a))
                .map(a -> interceptor.intercept(database, a))
                .orElseGet(() -> Stream.of(action));
        }

        @Override
        public String toString() {
            return interceptor.getClass().getName();
        }
    }
}
