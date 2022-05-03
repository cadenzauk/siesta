package com.cadenzauk.siesta.json;

import java.util.Objects;

public class Json {
    private final String data;

    public Json(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Json('" + data + "')";
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Json other = (Json) o;
        return Objects.equals(data, other.data);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(data);
    }

    public String data() {
        return data;
    }

    public static Json of(String data) {
        return new Json(data);
    }

    public static Json json(String data) {
        return new Json(data);
    }
}
