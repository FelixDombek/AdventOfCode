
package com.fd.adventofcode

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Scanner
import kotlin.math.abs

class AdventOfCode2024 : AdventBase(2024) {
    @Test
    fun `day 1, historian hysteria`() {
        val input = getInput(1).map { it.split(Regex("\\s+")).map { it.toLong() } }
        val left = input.map { it[0] }.sorted()
        val right = input.map { it[1] }.sorted()
        val result = left.zip(right).map { (l, r) -> abs(r - l) }.sum()
        assertEquals("Day 1.1", 1603498, result)

        val score = left.map { l -> right.count { it == l } * l }.sum()
        assertEquals("Day 1.2", 25574739, score)
    }

    @Test
    fun `day 2, red-nosed reports`() {
        val reports = getInput(2).map { Scanner(it).findAllInt() }
        fun isValid(report: List<Int>): Boolean {
            val sorted = report.sorted()
            return (sorted == report || sorted.reversed() == report) && report.zipWithNext().all {
                (a, b) -> abs(a - b) >= 1 && abs(a - b) <= 3
            }
        }
        val numValid = reports.count { isValid(it) }
        assertEquals("Day 2.1", 670, numValid)

        val numDampenedValid = reports.count {
            for (i in it.indices) {
                val dampened = it.subList(0, i) + it.subList(i + 1, it.size)
                if (isValid(dampened)) return@count true
            }
            false
        }
        assertEquals("Day 2.2", 700, numDampenedValid)
    }

    @Test
    fun `day 3, mull it over`() {
        val input = getString(3)
        val ops = Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)").findAll(input)
        val sum = ops.map { it.groupValues.drop(1).map { it.toInt() }.zipWithNext { l, r -> l * r } }.flatten().sum()
        assertEquals("Day 3.1", 183380722, sum)

        val ops2 = Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)|don't\\(\\)|do\\(\\)").findAll(input)
        var enabled = true
        val sum2 = ops2.mapNotNull {
            if (it.value == "do()") { enabled = true ; null }
            else if (it.value == "don't()") { enabled = false ; null }
            else if (enabled) it.groupValues.drop(1).map { it.toInt() }.zipWithNext { l, r -> l * r }
            else null
        }.flatten().sum()
        assertEquals("Day 3.2", 82733683, sum2)
    }

    @Test
    fun `day 4, ceres search`() {
        val input = getInput(4)
        fun List<List<Char>>.diag(row: Int, col: Int): Boolean = (row + 3) in indices && (col + 3) in this[0].indices && this[row][col] == 'X' && this[row+1][col+1] == 'M' && this[row+2][col+2] == 'A' && this[row+3][col+3] == 'S'
        val normal = input.map { Regex("XMAS").findAll(it).count().also { print("$it ") } }.sum()
        println()
        val diagonal = (0 until input.size).map { row -> (0 until input[0].length).count { col -> input.toMatrix().diag(row, col) }.also { print("$it ") } }.sum()
        println()
        val backwards = input.map { Regex("SAMX").findAll(it).count().also { print("$it ") } }.sum()
        println()
        val up = input.transposed().map { Regex("XMAS").findAll(it).count().also { print("$it ") } }.sum()
        println()
        val down = input.transposed().map { Regex("SAMX").findAll(it).count().also { print("$it ") } }.sum()
        val d2 = with (input.toMatrix().rotatedLeft()) { (0 until size).map { row -> (0 until this[0].size).count { col -> this.diag(row, col) }.also { print("$it ") } }.sum() }
        val d3 = with (input.toMatrix().rotated180()) { (0 until size).map { row -> (0 until this[0].size).count { col -> this.diag(row, col) }.also { print("$it ") } }.sum() }
        val d4 = with (input.toMatrix().rotatedRight()) { (0 until size).map { row -> (0 until this[0].size).count { col -> this.diag(row, col) }.also { print("$it ") } }.sum() }

        assertEquals("Day 4.1", 2496, normal + diagonal + backwards + up + down + d2 + d3 + d4)

        fun List<List<Char>>.xmas(row: Int, col: Int): Boolean = (row+1) in indices && (row-1) in indices && (col+1) in this[0].indices && (col-1) in this[0].indices &&
                this[row][col] == 'A' && ((this[row+1][col+1] == 'M' && this[row+1][col-1] == 'M' && this[row-1][col+1] == 'S' && this[row-1][col-1] == 'S') ||
                (this[row+1][col+1] == 'M' && this[row+1][col-1] == 'S' && this[row-1][col+1] == 'M' && this[row-1][col-1] == 'S') ||
                (this[row+1][col+1] == 'S' && this[row+1][col-1] == 'S' && this[row-1][col+1] == 'M' && this[row-1][col-1] == 'M')||
                (this[row+1][col+1] == 'S' && this[row+1][col-1] == 'M' && this[row-1][col+1] == 'S' && this[row-1][col-1] == 'M'))

        val xmascount = (0 until input.size).map { row -> (0 until input[0].length).count { col -> input.toMatrix().xmas(row, col) }.also { print("$it ") } }.sum()
        assertEquals("Day 4.2", 1967, xmascount)
    }

    @Test
    fun `day 4, ceres search optimized`() {
        val input = getInput(4)

        // Helper to check diagonal matches
        fun List<List<Char>>.hasDiagonalMatch(row: Int, col: Int, target: String): Boolean {
            if (row + target.length > size || col + target.length > this[0].size) return false
            return target.indices.all { this[row + it][col + it] == target[it] }
        }

        // Generic function to count matches
        fun countMatches(input: List<List<Char>>, pattern: String, isDiagonal: Boolean = false): Int {
            val rows = input.size
            val cols = input[0].size
            return (0 until rows).sumOf { row ->
                (0 until cols).count { col ->
                    if (isDiagonal) input.hasDiagonalMatch(row, col, pattern)
                    else input[row].joinToString("").contains(pattern)
                }
            }
        }

        // Generate relevant transformations: Original, 90°, 180°, 270°
        val transformations = listOf(
            input.toMatrix(),
            input.toMatrix().rotatedLeft(),
            input.toMatrix().rotated180(),
            input.toMatrix().rotatedRight()
        )

        // Count matches for all transformations
        val totalMatches = transformations.sumOf { matrix ->
            countMatches(matrix, "XMAS") + countMatches(matrix, "XMAS", isDiagonal = true)
        }

        assertEquals("Day 4.1", 2496, totalMatches)

        // Part 2 - Check "XMAS" pattern logic
        fun List<List<Char>>.isXmasPattern(row: Int, col: Int): Boolean {
            if (row !in 1 until size - 1 || col !in 1 until this[0].size - 1) return false
            return this[row][col] == 'A' && listOf(
                listOf('M', 'M', 'S', 'S'),
                listOf('M', 'S', 'M', 'S'),
                listOf('S', 'S', 'M', 'M'),
                listOf('S', 'M', 'S', 'M')
            ).any { pattern ->
                this[row + 1][col + 1] == pattern[0] &&
                        this[row + 1][col - 1] == pattern[1] &&
                        this[row - 1][col + 1] == pattern[2] &&
                        this[row - 1][col - 1] == pattern[3]
            }
        }

        val xmasCount = (0 until input.size).sumOf { row ->
            (0 until input[0].length).count { col -> input.toMatrix().isXmasPattern(row, col) }
        }

        assertEquals("Day 4.2", 1967, xmasCount)
    }

    @Test
    fun `day 5, print queue`() {
        val input = getString(5).split("\n\n")
        val ordering = input[0].split("\n").map { with (Scanner(it).useDelimiter("\\|")) { nextInt() to nextInt() } }
        data class Page(val num: Int): Comparable<Page> {
            override fun compareTo(other: Page): Int {
                if (num == other.num) return 0
                if (ordering.contains(num to other.num)) return -1
                return 1
            }
        }

        val updates = input[1].split("\n").map { Scanner(it).useDelimiter(",").findAllInt().map { Page(it) } }
        val validMiddles = updates.filter { it == it.sorted() }.map { it[it.size/2] }
        val sum = validMiddles.sumOf { it.num }
        assertEquals("Day 5.1", 5964, sum)

        val validatedMiddles = updates.filter { it != it.sorted() }.map { it.sorted()[it.size/2] }
        val sum2 = validatedMiddles.sumOf { it.num }
        assertEquals("Day 5.2", 4719, sum2)
    }
}