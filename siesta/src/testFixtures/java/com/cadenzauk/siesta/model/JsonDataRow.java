package com.cadenzauk.siesta.model;

import com.cadenzauk.siesta.json.BinaryJson;
import com.cadenzauk.siesta.json.Json;

import javax.persistence.Table;
import java.util.Objects;
import java.util.Optional;

@Table(name = "JSON_DATA")
public class JsonDataRow {
    private final long jsonId;
    private final Json data;
    private final Optional<BinaryJson> dataBinary;

    public JsonDataRow(long jsonId, Json data, Optional<BinaryJson> dataBinary) {
        this.jsonId = jsonId;
        this.data = data;
        this.dataBinary = dataBinary;
    }

    @Override
    public String toString() {
        return "JsonDataRow{" +
            "jsonId=" + jsonId +
            ", data=" + data +
            ", binary=" + dataBinary +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonDataRow that = (JsonDataRow) o;
        return jsonId == that.jsonId && Objects.equals(data, that.data) && Objects.equals(dataBinary, that.dataBinary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jsonId, data, dataBinary);
    }

    public long jsonId() {
        return jsonId;
    }

    public Json data() {
        return data;
    }

    public Optional<BinaryJson> dataBinary() {
        return dataBinary;
    }
}
