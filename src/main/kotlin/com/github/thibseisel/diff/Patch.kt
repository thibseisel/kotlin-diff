package com.github.thibseisel.diff

import com.github.thibseisel.diff.myers.Change

/**
 * Describe the patch holding all deltas between the original and revised texts.
 *
 * @param T The type of the compared elements in the 'lines'.
 */
public class Patch<T>(estimatedPatchSize: Int) {
    private val _deltas = ArrayList<Delta<T>>(estimatedPatchSize)
        get() {
            field.sortBy { it.source.position }
            return field
        }

    /**
     * List of computed deltas.
     */
    public val deltas: List<Delta<T>>
        get() = _deltas

    /**
     * Apply this patch to the given target.
     *
     * @return The patched text.
     */
    public fun applyTo(target: List<T>): List<T> {
        val result = target.toMutableList()
        val it = _deltas.listIterator(_deltas.size)
        while (it.hasPrevious()) {
            val delta = it.previous()
            delta.applyTo(result)
        }

        return result
    }

    /**
     * Restore the text to original.
     * Opposite to [applyTo].
     *
     * @param target The given target.
     * @return The restored text.
     */
    public fun restore(target: List<T>): List<T> {
        val result = target.toMutableList()
        val it = _deltas.listIterator(_deltas.size)
        while (it.hasPrevious()) {
            val delta = it.previous()
            delta.restore(result)
        }

        return result
    }

    /**
     * Add the given delta to this patch.
     */
    internal fun addDelta(delta: Delta<T>) {
        _deltas.add(delta)
    }

    internal companion object {
        internal fun <T> generate(
            original: List<T>,
            revised: List<T>,
            changes: List<Change>
        ): Patch<T> {
            val patch = Patch<T>(changes.size)
            for (change in changes) {
                val orgChunk = buildChunk(change.startOriginal, change.endOriginal, original)
                val revChunk = buildChunk(change.startRevised, change.endRevised, revised)
                patch.addDelta(
                    when (change.deltaType) {
                        DeltaType.DELETE -> DeleteDelta(orgChunk, revChunk)
                        DeltaType.INSERT -> InsertDelta(orgChunk, revChunk)
                        DeltaType.CHANGE -> ChangeDelta(orgChunk, revChunk)
                    }
                )
            }

            return patch
        }

        private fun <T> buildChunk(start: Int, end: Int, data: List<T>) =
            Chunk(start, data.slice(start until end))
    }
}
