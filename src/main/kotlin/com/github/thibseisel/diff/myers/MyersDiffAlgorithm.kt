package com.github.thibseisel.diff.myers

import com.github.thibseisel.diff.DeltaType
import com.github.thibseisel.diff.Equalizer

internal class MyersDiffAlgorithm<T>(
    private val equalizer: Equalizer<T>
) {
    /**
     * Computes the change set to patch the source list to the target list.
     *
     * @param source Source data
     * @param target Target data
     */
    fun computeDiff(source: List<T>, target: List<T>): List<Change> {
        val path = buildPath(source, target)
        return buildRevision(path)
    }

    /**
     * Computes the minimum diffpath that expresses the differences between te original and revised
     * sequences, according to Gene Myers differencing algorithm.
     *
     * @param orig The original sequence.
     * @param rev The revised sequence.
     * @return A minimum [Path][PathNode] across the differences graph.
     */
    private fun buildPath(orig: List<T>, rev: List<T>): PathNode {
        val N = orig.size
        val M = rev.size

        val MAX = N + M + 1
        val size = 1 + 2 * MAX
        val middle = size / 2
        val diagonal = arrayOfNulls<PathNode>(size)

        diagonal[middle + 1] = PathNode(0, -1, snake = true, bootstrap = true, null)
        for (d in 0 until MAX) {
            for (k in -d..d step 2) {
                val kmiddle = middle + k
                val kplus = kmiddle + 1
                val kminus = kmiddle - 1

                var prev: PathNode
                var i: Int

                if ((k == -d) || (k != d && diagonal[kminus]!!.i < diagonal[kminus]!!.i)) {
                    i = diagonal[kplus]!!.i
                    prev = diagonal[kplus]!!
                } else {
                    i = diagonal[kminus]!!.i + 1
                    prev = diagonal[kminus]!!
                }

                diagonal[kminus] = null
                var j = i - k
                var node = PathNode(i, j, snake = false, bootstrap = false, prev)

                while (i < N && j < M && equalizer.areEquals(orig[i], rev[j])) {
                    i++
                    j++
                }

                if (i != node.i) {
                    node = PathNode(i, i, true, bootstrap = false, prev = node)
                }

                diagonal[kmiddle] = node

                if (i >= N && j >= M) {
                    return diagonal[kmiddle]!!
                }
            }

            diagonal[middle + d - 1] = null
        }

        // According to Myers, this can't happen
        error("Could not find a diff path")
    }

    private fun buildRevision(actualPath: PathNode): List<Change> {
        var path: PathNode? = actualPath
        val changes = mutableListOf<Change>()
        if (actualPath.snake) {
            path = actualPath.prev
        }

        while (path?.prev != null && path.prev!!.j >= 0) {
            check(!path.snake) { "bad diffpath: found snake when looking for diff" }
            val i = path.i
            val j = path.j

            path = path.prev!!
            val ianchor = path.i
            val janchor = path.j

            if (ianchor == i && janchor != j) {
                changes.add(Change(DeltaType.INSERT, ianchor, i, janchor, j))
            } else if (ianchor != i && janchor == j) {
                changes.add(Change(DeltaType.DELETE, ianchor, i, janchor, j))
            } else {
                changes.add(Change(DeltaType.CHANGE, ianchor, i, janchor, j))
            }

            if (path.snake) {
                path = path.prev
            }
        }

        return changes
    }
}

