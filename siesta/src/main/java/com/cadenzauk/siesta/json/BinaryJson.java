package com.cadenzauk.siesta.json;

import java.util.Objects;

public class BinaryJson {
    private final String data;

    public BinaryJson(String data) {
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
        BinaryJson other = (BinaryJson) o;
        return Objects.equals(data, other.data);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(data);
    }

    public String data() {
        return data;
    }

    public static BinaryJson of(String data) {
        return new BinaryJson(data);
    }

    public static BinaryJson binaryJson(String data) {
        return new BinaryJson(data);
    }
}
