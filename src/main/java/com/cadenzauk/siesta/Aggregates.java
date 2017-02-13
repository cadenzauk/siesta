/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.function.MethodReference;
import com.cadenzauk.siesta.aggregate.MaxFunction;
import com.cadenzauk.siesta.expression.UnresolvedColumn;

public class Aggregates {
    public static <T> TypedExpression<T> max(TypedExpression<T> arg) {
        return new MaxFunction<>(arg);
    }
    public static <T,R> TypedExpression<T> max(MethodReference<R,T> arg) {
        return new MaxFunction<>(UnresolvedColumn.of(arg));
    }
}
