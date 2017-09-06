package com.github.minaasham.offheap.largecollections;

import lombok.Value;

@Value
final class BadHashInteger {

    private final int value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BadHashInteger that = (BadHashInteger) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return 7;
    }
}
