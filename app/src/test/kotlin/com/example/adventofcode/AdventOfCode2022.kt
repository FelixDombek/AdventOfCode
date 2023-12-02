package com.example.adventofcode

import org.junit.Test

import org.junit.Assert.*
import java.io.File
import com.google.common.collect.Comparators.greatest
import kotlin.system.measureTimeMillis

class AdventOfCode2022 {
    @Test
    fun door01() {
        val input = File("advent2022-01.input.txt")
        assertTrue(input.absolutePath, input.isFile)

        // part 1
        val sums = input.readText().split("\n\n").map { it.lines().sumOf { it.toInt() } }
        println("maxElf: ${sums.max()}")

        // part 2

        // complexity O(n + k log k)
        var v1: Int
        val v1millis = measureTimeMillis {
            v1 = sums.stream().collect(greatest(3, compareBy { it })).sum()
        }
        println("max3: ${v1} in $v1millis")

        // complexity O(n log n)
        var v2: Int
        val v2millis = measureTimeMillis {
            v2 = sums.sortedDescending().take(3).sum()
        }
        println("max3 Onlogn: ${v2} in $v2millis")
    }
}