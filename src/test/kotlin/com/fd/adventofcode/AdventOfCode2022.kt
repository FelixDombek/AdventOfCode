package com.fd.adventofcode

import org.junit.Test

import org.junit.Assert.*
import com.google.common.collect.Comparators.greatest
import java.util.Scanner
import kotlin.math.abs
import kotlin.system.measureTimeMillis


class AdventOfCode2022 : AdventBase(2022) {

    @Test
    fun `day 1, calorie counting`() {
        val input = getBlocks(1)
        val sums = input.map { it.sumOf { it.toInt() } }
        assertEquals("2022.1.1", 71934, sums.max())

        // complexity O(n + k log k)
        var v1: Int
        val v1millis = measureTimeMillis {
            v1 = sums.stream().collect(greatest(3, compareBy { it })).sum()
        }
        assertEquals("2022.1.2", 211447, v1)
        println(v1millis)

        // complexity O(n log n)
        var v2: Int
        val v2millis = measureTimeMillis {
            v2 = sums.sortedDescending().take(3).sum()
        }
        assertEquals("2022.1.2 O(n log n)", 211447, v2)
        println(v2millis)
    }

    @Test
    fun `day 2, rock paper scissors`() {
        val input = getInput(2).map { it[0] - 'A' to it[2] - 'X' }
        val sum = input.sumOf { (t, m) -> m + 1 + (m - t + 1 + 3) % 3 * 3 }
        assertEquals("2022.2.1", 12276, sum)

        val sum2 = input.sumOf { (t, w) -> (t + w - 1 + 3) % 3 + 1 + w * 3 }
        assertEquals("2022.2.2", 9975, sum2)
    }

    @Test
    fun `day 3, rucksack reorganization`() {
        val input = getInput(3)
        fun priority(c: Char) = c.lowercaseChar() - 'a' + 1 + if (c.isUpperCase()) 26 else 0
        val sum = input.map {
            it.substring(0, it.length / 2).toSet().intersect(it.substring(it.length / 2).toSet()).first()
        }.sumOf(::priority)
        assertEquals("2022.3.1", 7831, sum)

        val sum2 = input.chunked(3) {
            it.map { it.toSet() }.reduce { acc, s -> acc.intersect(s) }.first()
        }.sumOf(::priority)
        assertEquals("2022.3.2", 2683, sum2)
    }

    @Test
    fun `day 4, camp cleanup`() {
        val input = getInput(4)
        infix fun IntRange.contains(r: IntRange) = first in r && last in r
        val count = input.count {
            val (a1, z1, a2, z2) = it.split(Regex("[-,]")).map { it.toInt() }
            a1..z1 contains a2..z2 || a2..z2 contains a1..z1
        }
        assertEquals("2022.4.1", 496, count)

        infix fun IntRange.overlaps(r: IntRange) = first in r || last in r
        val count2 = input.count {
            val (a1, z1, a2, z2) = it.split(Regex("[-,]")).map { it.toInt() }
            a1..z1 overlaps a2..z2 || a2..z2 overlaps a1..z1
        }
        assertEquals("2022.4.1", 847, count2)
    }

}
