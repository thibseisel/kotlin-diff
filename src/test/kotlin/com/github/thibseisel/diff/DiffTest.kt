package com.github.thibseisel.diff

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.property.checkAll

class DiffTest : StringSpec({
    "Diffing same list should produce no deltas" {
        checkAll<List<String>> { list ->
            diff(list, list).deltas.shouldBeEmpty()
        }
    }

    "Adding should generate 1 InsertDelta" {
        val original = emptyList<String>()
        val revised = original + "Hello World!"
        val patch = diff(original, revised)
        patch.deltas.shouldContain(
            InsertDelta(
                original = Chunk(0, emptyList()),
                revised = Chunk(0, listOf("Hello World!"))
            )
        )
    }

    "Deleting should generate 1 DeleteDelta" {
        val original = listOf("Hello World!")
        val revised = emptyList<String>()

        val patch = diff(original, revised)
        patch.deltas.shouldContain(
            DeleteDelta(
                original = Chunk(0, listOf("Hello World!")),
                revised = Chunk(0, emptyList())
            )
        )
    }
})
