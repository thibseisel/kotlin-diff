package com.github.thibseisel.diff.myers

import com.github.thibseisel.diff.DeltaType

internal class Change(
    val deltaType: DeltaType,
    val startOriginal: Int,
    val endOriginal: Int,
    val startRevised: Int,
    val endRevised: Int
)
