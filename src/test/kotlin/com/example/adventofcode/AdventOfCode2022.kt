package com.example.adventofcode

import org.junit.Test

import org.junit.Assert.*
import com.google.common.collect.Comparators.greatest
import kotlin.system.measureTimeMillis


class AdventOfCode2022 : AdventBase(2022) {
    @Test
    fun day1() {
        val input = getString(1)
        val sums = input.split("\n\n").map { it.lines().sumOf { it.toInt() } }
        println("2022.1.1: ${sums.max()}")

        // complexity O(n + k log k)
        var v1: Int
        val v1millis = measureTimeMillis {
            v1 = sums.stream().collect(greatest(3, compareBy { it })).sum()
        }
        println("2022.1.2: $v1 in $v1millis")

        // complexity O(n log n)
        var v2: Int
        val v2millis = measureTimeMillis {
            v2 = sums.sortedDescending().take(3).sum()
        }
        println("2022.1.2 O(n log n): $v2 in $v2millis")
    }

    @Test
    fun day2() {
        val input = getInput(2).map { it[0] - 'A' to it[2] - 'X' }
        val sum = input.sumOf { (t, m) -> m + 1 + (m - t + 1 + 3) % 3 * 3 }
        println("2022.2.1: $sum")
        assertEquals(12276, sum)

        val sum2 = input.sumOf { (t, w) -> (t + w - 1 + 3) % 3 + 1 + w * 3 }
        println("2022.2.2: $sum2")
        assertEquals(9975, sum2)
    }

    @Test
    fun day3() {
        val input = getInput(3)
        fun priority(c: Char) = c.lowercaseChar() - 'a' + 1 + if (c.isUpperCase()) 26 else 0
        val sum = input.map {
            it.substring(0, it.length / 2).toSet().intersect(it.substring(it.length / 2).toSet()).first()
        }.sumOf(::priority)
        println("2022.3.1: $sum")
        assertEquals(7831, sum)

        val sum2 = input.chunked(3) {
            it.map { it.toSet() }.reduce { acc, s -> acc.intersect(s) }.first()
        }.sumOf(::priority)
        println("2022.3.2: $sum2")
        assertEquals(2683, sum2)
    }

    @Test
    fun day4() {
        val input = getInput(4)
        infix fun IntRange.contains(r: IntRange) = first in r && last in r
        val count = input.count {
            val (a1, z1, a2, z2) = it.split(Regex("[-,]")).map { it.toInt() }
            a1..z1 contains a2..z2 || a2..z2 contains a1..z1
        }
        println("2022.4.1: $count")

        infix fun IntRange.overlaps(r: IntRange) = first in r || last in r
        val count2 = input.count {
            val (a1, z1, a2, z2) = it.split(Regex("[-,]")).map { it.toInt() }
            a1..z1 overlaps a2..z2 || a2..z2 overlaps a1..z1
        }
        println("2022.4.2: $count2")
    }
}