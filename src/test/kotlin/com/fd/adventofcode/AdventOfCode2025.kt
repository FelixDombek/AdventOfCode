package com.fd.adventofcode

import org.junit.Test
import org.junit.Assert.assertEquals

class AdventOfCode2025 : AdventBase(2025) {
    @Test
    fun `day 1, secret entrance`() {
        val input = getInput(1).map { it.first() to it.drop(1).toInt() }
        var pos = 50
        val numPos = 100
        var visited0 = 0
        for ((dir, steps) in input) {
            pos = (pos + steps * if (dir == 'L') -1 else 1) % numPos
            if (pos == 0) ++visited0
        }
        assertEquals("Day 1.1", if (isExample) 3 else 1011, visited0)

        pos = 50
        visited0 = 0
        for ((dir, steps) in input) {
            val tmpPos = if (dir == 'L' && pos != 0) numPos - pos else pos
            val newPos = tmpPos + steps
            val vis = newPos / numPos
            visited0 += vis
            pos = (if (dir == 'L') numPos - (newPos % numPos) else newPos % numPos) % numPos
        }
        assertEquals("Day 1.2", if (isExample) 6 else 5937, visited0)
    }

    @Test
    fun `day 2, gift shop`() {
        val input = getString(2).split(",").map { it.split("-").let { it[0].toLong()..it[1].toLong() } }
        fun sumInvalid(re: Regex) = input.sumOf { it.filter { id -> re.matches("$id") }.sum() }
        val sum1 = sumInvalid(Regex("(\\d+)\\1"))
        assertEquals("Day 2.1", if (isExample) 1 else 23534117921, sum1)

        val sum2 = sumInvalid(Regex("(\\d+)\\1+"))
        assertEquals("Day 2.2", if (isExample) 1 else 31755323497, sum2)
    }

    @Test
    fun `day 3, lobby`() {
        val input = getInput(3)
        fun sumJoltage(numDigits: Int) = input.sumOf { bank ->
            var joltage = 0L
            var startIndex = 0
            for (digit in numDigits downTo 1) {
                var highest = "${bank[startIndex]}".toInt()
                var highestIndex = startIndex
                for (i in (startIndex + 1)..(bank.length - digit)) {
                    val ci = "${bank[i]}".toInt()
                    if (ci > highest) {
                        highest = ci
                        highestIndex = i
                    }
                }
                joltage = joltage * 10L + highest
                startIndex = highestIndex + 1
            }
            joltage
        }

        val sum1 = sumJoltage(2)
        assertEquals("Day 3.1", if (isExample) 357 else 16887, sum1)
        val sum2 = sumJoltage(12)
        assertEquals("Day 3.2", if (isExample) 3121910778619 else 167302518850275, sum2)
    }

    @Test
    fun `day 4, printing department`() {
        val map = getInput(4).toMatrix()
        fun countAdj(x: Int, y: Int): Int {
            var count = 0
            for (i in -1..1) {
                for (j in -1..1) {
                    if (i == 0 && j == 0) continue
                    if ((x + i) !in map.indices || (y + j) !in map[x].indices) continue
                    if (map[x + i][y + j] == '@') ++count
                }
            }
            return count
        }
        var accessible = 0
        map.forEachIndexed { x, line ->
            line.forEachIndexed { y, c ->
                if (c == '@' && countAdj(x, y) < 4) ++accessible
            }
        }
        assertEquals("Day 4.1", if (isExample) 13 else 1416, accessible)

        var changed = true
        accessible = 0
        while (changed) {
            changed = false
            for (x in map.indices) {
                for (y in map[x].indices) {
                    if (map[x][y] == '@' && countAdj(x, y) < 4) {
                        map[x][y] = '.'
                        ++accessible
                        changed = true
                    }
                }
            }
        }
        assertEquals("Day 4.2", if (isExample) 43 else 9086, accessible)
    }

    @Test
    fun `day 5, cafeteria`() {
        val input = getBlocks(5)
        val freshRanges = input[0].map { it.split("-").let { it[0].toLong()..it[1].toLong() } }
        val ingredients = input[1].map { it.toLong() }
        val numFresh = ingredients.count { ingredient -> freshRanges.any { it.contains(ingredient) } }
        assertEquals("Day 5.1", if (isExample) 3 else 756, numFresh)

        val mutableRanges = freshRanges.toMutableList()
        fun mergeOverlappingRanges(a: LongRange, b: LongRange) = minOf(a.first, b.first)..maxOf(a.last, b.last)
        fun areOverlapping(a: LongRange, b: LongRange) = a.first in b || a.last in b || b.first in a || b.last in a
        var changed = true
        while (changed) {
            changed = false
            for (i in mutableRanges.indices) {
                for (j in i + 1..mutableRanges.indices.last) {
                    if (areOverlapping(mutableRanges[i], mutableRanges[j])) {
                        mutableRanges[i] = mergeOverlappingRanges(mutableRanges[i], mutableRanges[j])
                        mutableRanges.removeAt(j)
                        changed = true
                        break
                    }
                }
            }
        }
        val totalFresh = mutableRanges.sumOf { it.last - it.first + 1 }
        assertEquals("Day 5.2", if (isExample) 14 else 355555479253787, totalFresh)
    }

    @Test
    fun `day 6, trash compactor`() {
        val inputRaw = getInput(6)
        val longestLineLen = inputRaw.maxOf { it.length }
        val input = inputRaw.map { it.padEnd(longestLineLen, ' ') }
        val problems = input.map { it.trim().split(Regex("\\s+")) }.transposed()
        val sum = problems.sumOf {
            val nums = it.dropLast(1).map { it.toLong() }
            nums.reduce { acc, num -> if (it.last().single() == '*') acc * num else acc + num }
        }
        assertEquals("Day 6.1", if (isExample) 4277556 else 5524274308182, sum)

        val problems2 = input.transposed()
        val group = mutableListOf<Long>()
        var op = '+'
        var sum2 = 0L
        fun solveGroup() = group.reduce { acc, num -> if (op == '*') acc * num else acc + num }.also { group.clear() }
        for (num in problems2) {
            if (num.matches(Regex("\\s+"))) { sum2 += solveGroup() ; continue }
            if (num.endsWith ("*") || num.endsWith("+")) op = num.last()
            group += num.filter { it.isDigit() }.toLong()
        }
        sum2 += solveGroup()
        assertEquals("Day 6.2", if (isExample) 3263827 else 8843673199391, sum2)
    }

    @Test
    fun `day 7, laboratories`() {
        val input = getInput(7)
        val mat = input.toMatrix()
        var splits = 0
        mat.zipWithNext { a, b ->
            for (i in a.indices) {
                when {
                    a[i] == 'S' -> b[i] = '|'
                    a[i] == '|' && b[i] != '^' -> b[i] = '|'
                    a[i] == '|' && b[i] == '^' -> { b[i-1] = '|' ; b[i+1] = '|' ; ++splits }
                }
            }
        }
        assertEquals("Day 7.1", if (isExample) 21 else 1562, splits)

        val mat2 = input.toMatrix()
        val counts = List(mat2.size) { MutableList(mat2[0].size) { 0L } }
        var col = 0
        mat2.zipWithNext { a, b ->
            ++col
            for (i in a.indices) {
                when {
                    a[i] == 'S' -> { b[i] = '|' ; counts[col][i] = 1 }
                    a[i] == '|' && b[i] != '^' -> { b[i] = '|' ; counts[col][i] += counts[col-1][i] }
                    a[i] == '|' && b[i] == '^' -> {
                        b[i-1] = '|' ; counts[col][i-1] += counts[col-1][i]
                        b[i+1] = '|' ; counts[col][i+1] += counts[col-1][i]
                    }
                }
            }
        }
        val total = counts.last().sum()
        assertEquals("Day 7.2", if (isExample) 40 else 24292631346665, total)
    }
}