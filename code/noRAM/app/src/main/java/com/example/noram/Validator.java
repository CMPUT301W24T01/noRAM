package com.example.noram;

import android.util.Pair;

public interface Validator {
    static <T> Pair<Boolean, String> validate(T object) {
        return new Pair<Boolean, String>(false, "");
    }
}
