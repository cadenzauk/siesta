package com.cadenzauk.siesta.dialect;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class VersionNo implements Comparable<VersionNo> {
    private final int[] parts;

    public VersionNo(String version) {
        this.parts = Arrays.stream(version.split("[^0-9]+"))
            .mapToInt(Integer::parseInt)
            .toArray();
    }

    @Override
    public int compareTo(@NotNull VersionNo o) {
        return new CompareToBuilder()
            .append(parts, o.parts)
            .toComparison();
    }

    public boolean isAtLeast(VersionNo other) {
        return compareTo(other) >= 0;
    }
}
