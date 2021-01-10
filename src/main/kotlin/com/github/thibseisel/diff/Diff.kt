package com.github.thibseisel.diff

import com.github.thibseisel.diff.myers.MyersDiffAlgorithm

public fun <T> diff(
    original: List<T>,
    revised: List<T>,
    equalizer: Equalizer<T> = Equalizer { a, b -> a == b }
): Patch<T> {
    val myers = MyersDiffAlgorithm(equalizer)
    return Patch.generate(original, revised, myers.computeDiff(original, revised))
}


