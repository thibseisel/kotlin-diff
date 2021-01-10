package com.github.thibseisel.diff

public fun interface Equalizer<in T> {
    public fun areEquals(a: T, b: T): Boolean
}
