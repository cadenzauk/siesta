package com.cadenzauk.siesta.dialect.function.json;

import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.dialect.function.SimpleFunctionSpec;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JsonObjectFunctionSpec extends SimpleFunctionSpec {
    public JsonObjectFunctionSpec(String name) {
        super(name);
    }

    @Override
    public String sql(Scope scope, String... argsSql) {
        String args = IntStream.range(0, argsSql.length / 2)
            .mapToObj(i -> "key " + argsSql[2*i] + " value " + argsSql[2*i+1])
            .collect(Collectors.joining(", "));
        return String.format("%s(%s)", name, args);
    }
}
