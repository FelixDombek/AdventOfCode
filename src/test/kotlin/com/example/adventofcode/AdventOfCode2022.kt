package com.fd.adventofcode

import org.junit.Test

import org.junit.Assert.*
import com.google.common.collect.Comparators.greatest
import java.util.Scanner
import kotlin.math.abs
import kotlin.system.measureTimeMillis


class AdventOfCode2022 : AdventBase(2022) {

    @Test
    fun day1() {
        val input = getString(1)
        val sums = input.split("\n\n").map { it.lines().sumOf { it.toInt() } }
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
    fun day2() {
        val input = getInput(2).map { it[0] - 'A' to it[2] - 'X' }
        val sum = input.sumOf { (t, m) -> m + 1 + (m - t + 1 + 3) % 3 * 3 }
        assertEquals("2022.2.1", 12276, sum)

        val sum2 = input.sumOf { (t, w) -> (t + w - 1 + 3) % 3 + 1 + w * 3 }
        assertEquals("2022.2.2", 9975, sum2)
    }

    @Test
    fun day3() {
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
    fun day4() {
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

    @Test
    fun day5() {
        val (crates, ops) = getString(5).split("\n\n").map { it.lines() }
        fun createStacks() = with (mutableMapOf<Int, MutableList<Char>>()) {
            crates.last().filter { it.isDigit() }.forEach { this[it.digitToInt()] = mutableListOf() }
            crates.reversed().drop(1).forEach {
                it.drop(1).windowed(1, 4).withIndex().filter { it.value[0].isLetter() }.forEach {
                    this[it.index + 1]!!.add(it.value[0])
                }
            }
            toMap()
        }
        val stacks = createStacks()
        ops.map { Regex("\\d+").findAll(it).map { it.value.toInt() }.toList() }.forEach { (n, from, to) ->
            repeat(n) { stacks[to]!!.add(stacks[from]!!.removeLast()) }
        }
        val top = stacks.map { (_, v) -> v.last() }.joinToString("")
        assertEquals("2022.5.1", "TDCHVHJTG", top)

        val stacks2 = createStacks()
        ops.map { Regex("\\d+").findAll(it).map { it.value.toInt() }.toList() }.forEach { (n, from, to) ->
            val tmp = mutableListOf<Char>()
            repeat(n) { tmp.add(stacks2[from]!!.removeLast()) }
            stacks2[to]!!.addAll(tmp.reversed())
        }
        val top2 = stacks2.map { (_, v) -> v.last() }.joinToString("")
        assertEquals("2022.5.2", "NGCMPJLHV", top2)
    }

    @Test
    fun day6() {
        val input = getString(6)
        fun marker(len: Int) = input.windowed(len).withIndex().find { it.value.toSet().size == len }!!.index + len
        val sop = marker(4)
        assertEquals("2022.6.1", 1909, sop)
        val som = marker(14)
        assertEquals("2022.6.2", 3380, som)
    }

    @Test
    fun day7() {
        val input = getInput(7)
        var cwd = ""
        val dirs = mutableMapOf(cwd to 0)
        input.map { it.split(" ") }.drop(1).forEach { when (val c = it.first()) {
            "$" -> when (it[1]) {
                "cd" -> if (it[2] == "..") cwd = cwd.replaceAfterLast('/', "").dropLast(1)
                        else { cwd += "/" + it[2] ; dirs[cwd] = 0 }
            }
            "dir" -> Unit
            else -> dirs[cwd] = dirs[cwd]!! + c.toInt()
        } }
        val totals = dirs.map { (k, _) -> k to dirs.filterKeys { it.startsWith(k) }.values.sum() }.toMap()
        val sum = totals.values.filter { it <= 100_000 }.sum()
        assertEquals("2022.7.1", 1297159, sum)

        val disk = 70_000_000
        val used = totals[""]!!
        val free = disk - used
        val size = 30_000_000
        val needed = size - free
        val delete = totals.filterValues { it >= needed }.minBy { (_, v) -> v }
        assertEquals("2022.7.2", 3866390, delete.value)
    }

    @Test
    fun day8() {
        val forest = getInput(8)
        fun north(x: Int, y: Int) = (y-1 downTo 0).map { forest[it][x] }
        fun south(x: Int, y: Int) = (y+1..<forest.size).map { forest[it][x] }
        fun west(x: Int, y: Int) = (x-1 downTo 0).map { forest[y][it] }
        fun east(x: Int, y: Int) = (x+1..<forest[y].length).map { forest[y][it] }

        val vis = forest.mapIndexed { y, line -> line.mapIndexed { x, tree ->
            sequenceOf(::north, ::south, ::west, ::east).any { it(x, y).all { it < tree } }
        }.count { it } }.sum()
        assertEquals("2022.8.1", 1812, vis)

        val scenic = forest.mapIndexed { y, line -> line.mapIndexed { x, tree ->
            sequenceOf(north(x, y), south(x, y), west(x, y), east(x, y)).fold(1) { acc, dir ->
                acc * dir.indexOfFirst { it >= tree }.let { if (it == -1) dir.size else it + 1 }
            }
        }.max() }.max()
        assertEquals("2022.8.2", 315495, scenic)
    }

    @Test
    fun day9() {
        val ops = getInput(1009).map { with(Scanner(it)) { next().first() to nextInt() } }
        operator fun Pair<Int, Int>.plus(rhs: Pair<Int, Int>) = first + rhs.first to second + rhs.second
        fun Pair<Int, Int>.nextTo(o: Pair<Int, Int>) = abs(first - o.first) <= 1 && abs(second - o.second) <= 1
        fun simulate(rope: MutableList<Pair<Int, Int>>): Int {
            val dir = mapOf('L' to (-1 to 0), 'R' to (1 to 0), 'U' to (0 to 1), 'D' to (0 to -1))
            val visited = mutableSetOf(rope.last())
            ops.forEach { op -> repeat(op.second) {
                println("$op, $it: $rope")
                val prev = rope.first()
                rope[0] += dir[op.first]!!
                rope.indices.drop(1).fold(prev) { p, i ->
                    if (rope[i].nextTo(rope[i-1])) return@repeat
                    val pp = rope[i] // BUG HERE: detect diagonal movement...
                    rope[i] = p
                    if (i == rope.indices.last) visited.add(rope.last())
                    pp
                }
            } }
            return visited.size
        }
        val count = simulate(MutableList(2) { 0 to 0 })
        //assertEquals("2022.9.1", 6236, count)

        val count2 = simulate(MutableList(10) { 0 to 0 })
        assertEquals("2022.9.2", 1, count2)
    }

}