package com.fd.adventofcode

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.PriorityQueue
import java.util.Scanner
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow


class AdventOfCode2023 : AdventBase(2023) {
    @Test
    fun day1() {
        val input = getInput(1)
        val sum = input.sumOf { line ->
            "${line.first { it.isDigit() }}${line.last { it.isDigit() }}".toInt()
        }
        assertEquals("Day 1.1", 54877, sum)

        val sum2 = input.sumOf { line ->
            val re = Regex("one|two|three|four|five|six|seven|eight|nine|1|2|3|4|5|6|7|8|9")
            fun toDigit(match: MatchResult): CharSequence =
                when (match.value) {
                    "one" -> "1"
                    "two" -> "2"
                    "three" -> "3"
                    "four" -> "4"
                    "five" -> "5"
                    "six" -> "6"
                    "seven" -> "7"
                    "eight" -> "8"
                    "nine" -> "9"
                    else -> match.value
                }

            val digit1 = re.find(line)!!.value.replace(re, ::toDigit)
            fun Regex.findLast(s: String) =
                (line.length-1 downTo 0).firstNotNullOf { this.matchAt(s, it) }
            val digit2 = re.findLast(line).value.replace(re, ::toDigit)

            "$digit1$digit2".toInt()
        }
        assertEquals("Day 1.2", 54100, sum2)
    }

    @Test
    fun day2() {
        val input = getInput(2)
        data class Round(val red: Int, val green: Int, val blue: Int) {
            fun canMatch(limits: Round) = red <= limits.red && green <= limits.green && blue <= limits.blue
            fun cube() = red * green * blue
        }
        data class Game(val id: Int, val rounds: List<Round>) {
            fun canMatch(numbers: Round) = rounds.all { it.canMatch(numbers) }
            fun minimumPossible() = rounds.fold(Round(0, 0, 0)) { acc, round ->
                Round(max(acc.red, round.red), max(acc.green, round.green), max(acc.blue, round.blue))
            }
        }
        // Game 2: 2 green, 2 blue, 16 red; 14 red; 13 red, 13 green, 2 blue; 7 red, 7 green, 2 blue
        val games = input.map { line ->
            val sc = Scanner(line).useDelimiter("[:;] ")
            val id = sc.skip("Game ").nextInt()
            val rounds = sc.asSequence().map { roundStr ->
                val colors = roundStr.split(", ")
                fun getCount(name: String) = colors.find { it.endsWith(name) }?.let { Scanner(it).nextInt() } ?: 0
                Round(getCount("red"), getCount("green"), getCount("blue"))
            }
            Game(id, rounds.toList())
        }
        val limits = Round(12, 13, 14)
        val idSum = games.filter { it.canMatch(limits) }.sumOf { it.id }
        assertEquals("Day 2.1", 2283, idSum)

        val minCubeSum = games.sumOf { it.minimumPossible().cube() }
        assertEquals("Day 2.2", 78669, minCubeSum)
    }

    @Test
    fun day3() {
        val input = getInput(3)
        val sum = input.mapIndexed { lineNum, line ->
            Regex("\\d+").findAll(line).filter {
                for (i in max(lineNum - 1, 0)..min(lineNum + 1, input.size - 1)) {
                    for (j in max(it.range.first - 1, 0)..min(it.range.last + 1, input[lineNum].length - 1)) {
                        if (!(input[i][j].isDigit() || input[i][j] == '.')) return@filter true
                    }
                }
                false
            }.sumOf { it.value.toInt() }
        }.sum()
        assertEquals("Day 3.1", 498559, sum)

        val gears = mutableMapOf<Pair<Int, Int>, MutableList<Int>>()
        input.forEachIndexed { lineNum, line ->
            Regex("\\d+").findAll(line).forEach {
                for (i in max(lineNum - 1, 0)..min(lineNum + 1, input.size - 1)) {
                    for (j in max(it.range.first - 1, 0)..min(it.range.last + 1, input[lineNum].length - 1)) {
                        if (input[i][j] == '*') {
                            gears.putIfAbsent(i to j, mutableListOf())
                            gears[i to j]!!.add(it.value.toInt())
                        }
                    }
                }
            }
        }
        val gearSum = gears.filter { it.value.size == 2 }.map { it.value[0] * it.value[1] }.sum()
        assertEquals("Day 3.2", 72246648, gearSum)
    }

    @Test
    fun day4() {
        val input = getInput(4)
        val winCounts = input.map { line ->
            val sc = Scanner(line).useDelimiter("(Card\\s+\\d+: | \\| )")
            val winning = Scanner(sc.next()).asSequence().toList()
            val mine = Scanner(sc.next()).asSequence()
            mine.count { it in winning }
        }
        val sum = winCounts.sumOf { 2.0.pow((it - 1).toDouble()).toInt() }
        assertEquals("Day 4.1", 25231, sum)

        val counts = MutableList(input.size) { 1 }
        winCounts.forEachIndexed { i, count ->
            for (id in i + 1..i + count) counts[id] += counts[i]
        }
        val sum2 = counts.sum()
        assertEquals("Day 4.2", 9721255, sum2)
    }

    @Test
    fun day5() {
        val input = getString(5).split("\n\n")
        val seeds = input[0].split(" ").drop(1).map { it.toLong() }
        val maps = input.drop(1).map { mapBlock ->
            mapBlock.split("\n").drop(1).map { mapLine ->
                val (to, from, len) = mapLine.split(" ").map { it.toLong() }
                from..<(from + len) to (to - from)
            }
        }
        fun mapSeeds(seeds: Sequence<Long>) = seeds.map { seed ->
            fun useMap(item: Long, m: List<Pair<LongRange, Long>>): Long {
                for (mapping in m) if (item in mapping.first) return item + mapping.second
                return item
            }
            maps.fold(seed) { item, m -> useMap(item, m) }
        }.min()

        val loc = mapSeeds(seeds.asSequence())
        assertEquals("Day 5.1", 600279879, loc)

        // naive: takes 12 minutes
        //val loc2 = seeds.chunked(2) { (seed, len) ->
        //    mapSeeds((seed..<seed + len).asSequence())
        //}.min()

        // optimized: takes 2 ms. 240000x speedup!
        fun mapSeedsStepped(seeds: LongRange): Long {
            fun useMap(item: Long, m: List<Pair<LongRange, Long>>): Pair<Long, Long> {
                for (mapping in m) if (item in mapping.first) return item + mapping.second to mapping.first.last - item + 1
                return item to m.fold(Long.MAX_VALUE) { len, mapping ->
                    if (mapping.first.first > item) min(mapping.first.first - item, len) else len
                }
            }
            fun useMaps(seed: Long): Pair<Long, Long> =
                maps.fold(seed to Long.MAX_VALUE) { (item, len), m ->
                    val (newItem, newLen) = useMap(item, m)
                    newItem to min(len, newLen)
                }

            var seed = seeds.first()
            var minLoc = Long.MAX_VALUE
            while (seed <= seeds.last()) {
                val (newLoc, step) = useMaps(seed)
                minLoc = min(newLoc, minLoc)
                seed += step
            }
            return minLoc
        }

        val loc2 = seeds.chunked(2) { (seed, len) ->
            mapSeedsStepped(seed..<seed + len)
        }.min()
        assertEquals("Day 5.2", 20191102, loc2)
    }

    @Test
    fun day6() {
        val (times, dists) = getInput(6).map { Scanner(it).asSequence().drop(1).map { it.toInt() }.toList() }
        val numWays = times.zip(dists).map { (time, dist) ->
            (1..<time).map { hold -> hold * (time - hold) }.count { it > dist }
        }.reduce { acc, elem -> acc * elem }
        assertEquals("Day 6.1", 114400, numWays)

        val time = times.joinToString("").toLong()
        val dist = dists.joinToString("").toLong()
        val numWays2 = (1..<time).asSequence().map { hold -> hold * (time - hold) }.count { it > dist }
        assertEquals("Day 6.2", 21039729, numWays2)
    }

    @Test
    fun day7() {
        val input = getInput(7)

        fun type1(s: String) = s.asSequence().groupBy { it }
            .let { (5 - it.size) * 10 + it.values.maxOf { it.size } }
        fun type2(s: String) = s.replace("J", "").asSequence().groupBy { it }
            .let { (5 - max(it.size, 1)) * 10 + (it.values.maxOfOrNull { it.size } ?: 0) + s.count { it == 'J' } }
        fun type(s: String, r1: Boolean) = if (r1) type1(s) else type2(s)

        fun value(s: String, r1: Boolean) = s.replace(Regex("[AKQJT]")) { when (it.value.first()) {
                'T' -> "A"
                'J' -> if (r1) "B" else "1"
                'Q' -> "C"
                'K' -> "D"
                'A' -> "E"
                else -> throw IllegalStateException("???")
            } }.toInt(16)

        fun strength(s: String, r1: Boolean) = (type(s, r1) shl 20) + value(s, r1)

        fun winnings(ss: List<String>, r1: Boolean) = ss.map { it.split(" ") }
            .sortedBy { strength(it[0], r1) }
            .mapIndexed { i, (_, n) ->
                //println("${i + 1}: $s - $n - t ${type(s, r1)} - v ${value(s, r1).toString(16)} - s ${strength(s, r1).toString(16)}}")
                (i + 1) * n.toInt()
            }.sum()

        val win = winnings(input, true)
        assertEquals("Day 7.1", 251216224, win)

        val win2 = winnings(input, false)
        assertEquals("Day 7.2", 250825971, win2)
    }

    @Test
    fun day8() {
        val (ops, graph) = getString(8).split("\n\n").map { it.lines() }.let { (ops, graph) ->
            ops[0].map { if (it == 'L') 0 else 1 } to graph.map {
                Regex("\\w+").findAll(it).map { it.value }.toList()
            }.associate { (n, l, r) -> n to listOf(l, r) }
        }

        fun steps(node: String, end: (String, Int) -> Boolean): Int {
            var count = 0
            var n = node
            while (!end(n, count)) n = graph[n]!![ops[count++ % ops.size]]
            return count
            // or
            // generateSequence(0) { it + 1 }.fold(node) { n, c ->
            //    if (!end(n, c)) graph[n]!![ops[c % ops.size]] else return c
            // }.length
        }

        val count = steps("AAA") { n, _ -> n == "ZZZ" }
        assertEquals("Day 8.1", 17141, count)

        val nodes = graph.keys.filter { it.endsWith('A') }

        // Not needed, but determines if the Z's are positioned after full circles (they are).
        // A--->----\  More precisely: The path from A-Z is exactly as long as one circle (Z-Z),
        //     |_Z__|  meaning we can simply take the LCM below, the prefix doesn't matter.
        fun circleLen(node: String): Int {
            val path = mutableMapOf<Pair<String, Int>, Int>()
            val zs = mutableMapOf<String, MutableList<Int>>()
            var circlenode = ""
            val steps = steps(node) { n, count ->
                val end = path.putIfAbsent(n to count % ops.size, count) != null
                if (!end) {
                    if (n.endsWith('Z')) zs.putIfAbsent(n, mutableListOf(count))?.add(count)
                } else circlenode = n
                end
            }
            val prefixLen = path[circlenode to steps % ops.size]!!
            val circleLen = path.size - prefixLen
            //println("start: $it, prefix: ${prefixlen}, circle: ${circlelen}, zs: $zs")
            return circleLen
        }

        val circles = nodes.map { circleLen(it) }

        // now that we know that, we could determine the Z positions much easier:
        val circles2 = nodes.map { steps(it) { n, _ -> n.endsWith('Z') }.toLong() }
        assertEquals("$circles", "$circles2")

        fun lcm(a: Long, b: Long): Long {
            val larger = max(a, b)
            val smaller = min(a, b)
            val maxLcm = a * b
            return LongProgression.fromClosedRange(larger, maxLcm, larger).find { it % smaller == 0L }!!
        }
        fun lcm(nums: List<Long>) = nums.reduce { acc, n -> lcm(acc, n) }

        val count2 = lcm(circles2)
        assertEquals("Day 8.2", 10818234074807, count2)
    }

    @Test
    fun day9() {
        val input = getInput(9).map { Scanner(it).findAllInt() }

        fun extrapolate(line: List<Int>) = generateSequence(line) {
            it.zipWithNext { a, b -> b - a }.let { if (it.all { it == 0 }) null else it }
        }.toList().foldRight(0) { l, acc -> l.last() + acc }
        // or:
        // val lines = mutableListOf(line)
        // while (lines.last().any { it != 0 }) lines.add(lines.last().zipWithNext { a, b -> b - a })
        // return lines.reversed().drop(1).fold(0L) { acc, l -> l.last() + acc }

        val sum = input.sumOf { extrapolate(it) }
        assertEquals("Day 9.1", 1696140818, sum)

        val sum2 = input.sumOf { extrapolate(it.reversed()) }
        assertEquals("Day 9.2", 1152, sum2)
    }

    @Test
    fun day10() {
        val maze = getInput(10)
        val (sx, sy) = 'S'.let { s -> maze.indexOfFirst { s in it }.let { maze[it].indexOf(s) to it } }
        // get travel direction into current pos based on previous pos
        fun dir(px: Int, py: Int, cx: Int, cy: Int) = when { py < cy -> 'v'
                                                             py > cy -> '^'
                                                             px < cx -> '>'
                                                             else    -> '<' }
        // get delta of next pos based on tile type and travel direction
        fun next(d: Char, j: Char) = when (j) { '-'  -> if (d == '<') -1 to  0 else  1 to 0
                                                '|'  -> if (d == '^')  0 to -1 else  0 to 1
                                                'F'  -> if (d == '^')  1 to  0 else  0 to 1
                                                '7'  -> if (d == '^') -1 to  0 else  0 to 1
                                                'L'  -> if (d == '<')  0 to -1 else  1 to 0
                                                else -> if (d == '>')  0 to -1 else -1 to 0 }
        // determine next pos after start pos based on adjacent tiles
        var (cx, cy) = when { maze[sy-1][sx] in "F|7" -> sx   to sy-1
                              maze[sy+1][sx] in "L|J" -> sx   to sy+1
                              else                    -> sx+1 to sy   }
        // traverse pipe loop. loop can be a list for 10.1, but we'll make use of this mapping in 10.2
        val loop = mutableMapOf((sx to sy) to '?')
        var (px, py) = sx to sy
        while (cx to cy != sx to sy) {
            val d = dir(px, py, cx, cy)
            loop[(cx to cy)] = d
            px = cx
            py = cy
            next(d, maze[cy][cx]).run { cx += first ; cy += second }
        }
        loop[sx to sy] = dir(px, py, cx, cy)

        assertEquals("Day 10.1", 6875, loop.size / 2)

        fun nonloop(xy: Pair<Int, Int>) = // true if xy is a non-loop tile in the maze
            xy.second in maze.indices && xy.first in maze.first().indices && xy !in loop.keys
        val sides = List(2) { mutableSetOf<Pair<Int, Int>>() }
        // fill two lists for tiles left and right of the loop (we don't know yet which is inside or outside)
        for ((c, d) in loop) {
            when (d) { 'v'  -> -1 to  0 // delta to the tile on the right side of the path (negated: left side)
                       '^'  ->  1 to  0
                       '>'  ->  0 to  1
                       else ->  0 to -1
            }.let { listOf(c - it, c + it) }.zip(sides) { (x, y), side ->
                (listOf(x to y) + maze[c.second][c.first].let { when {
                    // special case for bend tiles: we need to mark their outer 3 tiles as belonging to the side
                    it == '7' && (c.first < x || c.second > y) -> listOf( 0 to -1,  1 to -1,  1 to 0)
                    it == 'F' && (c.first > x || c.second > y) -> listOf( 0 to -1, -1 to -1, -1 to 0)
                    it == 'J' && (c.first < x || c.second < y) -> listOf( 1 to  0,  1 to  1,  0 to 1)
                    it == 'L' && (c.first > x || c.second < y) -> listOf(-1 to  0, -1 to  1,  0 to 1)
                    else                                       -> emptyList() }.map { c + it }
                }).filter { nonloop(it) }.let { side.addAll(it) }
            }
        }

        // mark all tiles reachable from any marked tile as belonging to the same side (this marks all non-loop tiles)
        fun floodfill(side: MutableSet<Pair<Int, Int>>) {
            val stack = side.toMutableList() // or: with (side.toMutableList()) {
            while (stack.isNotEmpty()) {     //         generateSequence { removeLastOrNull() }.forEach {...}
                val t = stack.removeLast()
                (-1..1).forEach { dx -> (-1..1).forEach { dy ->
                    (t + (dx to dy)).let { if (nonloop(it)) side.add(it) && stack.add(it) }
                } }
            }
        }
        sides.forEach { floodfill(it) }

        assertEquals(maze.size * maze.first().length, sides.sumOf { it.size } + loop.size)
        assertTrue(sides.first().intersect(sides.last()).isEmpty())
        assertTrue(sides.first().intersect(loop.keys).isEmpty())
        assertTrue(sides.last().intersect(loop.keys).isEmpty())

        val inside = sides.first { 0 to 0 !in it }
        assertEquals("Day 10.2", 471, inside.size)
    }

    @Test
    fun day11() {
        val space = getInput(11)
        val emptyRows = space.map { '#' !in it }
        val emptyCols = space.first().indices.map { i -> space.all { it[i] != '#' } }

        fun empties(from: Pair<Int, Int>, to: Pair<Int, Int>, factor: Int): Long =
            (factor - 1L) * (emptyRows.subList(from.second, to.second).count { it } +
                    emptyCols.subList(min(from.first, to.first), max(from.first, to.first)).count { it })

        val galaxies = space.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c -> if (c == '#') x to y else null }
        }

        fun dists(factor: Int) = galaxies.mapIndexed { i, to -> galaxies.subList(0, i).sumOf { from ->
            to.second - from.second + abs(to.first - from.first) + empties(from, to, factor)
        } }.sum()

        assertEquals("Day 11.1", 9734203, dists(2))
        assertEquals("Day 11.2", 568914596391, dists(1_000_000))
    }

    @Test
    fun day12() {
        class Row(val ss: String, g: List<Int>) {
            val s = "$ss."
            val gs = groupsToRanges(g)
            val cache = mutableMapOf<Pair<Int, Int>, Long>()
            fun count() = rec(ss.length, gs.indices.last)
            private fun fits(g: IntRange, c: Char) = g.all { s[it].let { it == c || it == '?' } }
            private fun rec(upperBound: Int, groupIdx: Int): Long {
                cache[upperBound to groupIdx]?.let { return it }
                val curGroup = gs[groupIdx]
                var count = 0L
                for (i in 0..<upperBound-curGroup.last) {
                    val curGroupPos = curGroup + i
                    val suffixPos = curGroupPos.last+1..upperBound
                    if (fits(curGroupPos, '#') && fits(suffixPos, '.'))
                        if (groupIdx == 0) if (fits(0..<curGroupPos.first, '.')) ++count else break
                        else count += rec(curGroupPos.first - 1, groupIdx - 1)
                }
                cache[upperBound to groupIdx] = count
                return count
            }
            private fun groupsToRanges(lens: List<Int>): List<IntRange> {
                var begin = 0
                return lens.map {
                    val end = begin + it
                    begin..<end.also { begin = end + 1 }
                }
            }
        }

        val input = getInput(12).map { with (Scanner(it).useDelimiter("[, ]")) { next() to findAllInt() } }
        val sum = input.sumOf { (s, g) -> Row(s, g).count() }
        assertEquals("Day 12.1", 6488, sum)

        val input2 = input.map { (s, g) -> 5.let { List(it) {s}.joinToString("?") to List(it) {g}.flatten() } }
        val sum2 = input2.sumOf{ (s, g) -> Row(s, g).count() }
        assertEquals("Day 12.2", 815364548481, sum2 )
    }

    @Test
    fun day13() {
        val fields = getString(13).split("\n\n").map { it.lines() }

        fun isSymmetric(f: List<String>, i: Int, errors: Int) =
            f.take(i).reversed().zip(f.drop(i)).sumOf { (l, r) -> l.zip(r).count { (l, r) -> l != r } } == errors

        fun score(f: List<String>, errors: Int = 0) =
            f.indices.drop(1).firstOrNull { isSymmetric(f, it, errors) } ?: 0

        fun combined(f: List<String>, errors: Int = 0) = 100 * score(f, errors) + score(f.transposed(), errors)

        val sum = fields.sumOf { combined(it) }
        assertEquals("Day 13.1", 29165, sum)

        val sum2 = fields.sumOf { combined(it, 1) }
        assertEquals("Day 13.2", 32192, sum2)
    }

    @Test
    fun day14() {
        var field = getInput(14).toMatrix().rotated180() // rotations needed for part 2
        fun MutableList<Char>.tiltCol() {
            var stop = 0
            forEachIndexed { i, c -> when (c) {
                '#' -> stop = i + 1
                'O' -> { if (stop != i) { this[stop] = 'O' ; this[i] = '.' } ; ++stop }
            } }
        }
        fun List<MutableList<Char>>.tiltField() = forEach { it.tiltCol() }
        fun step(f: List<MutableList<Char>>) = f.rotatedRight().also { it.tiltField() }
        fun weigh(f: List<List<Char>>) = f.rotated180().mapIndexed { i, l -> l.count { it == 'O' } * (f.size - i) }.sum()

        val weight = weigh(step(field).rotatedLeft())
        assertEquals("Day 14.1", if (isExample) 136 else 108889, weight)

        fun cycle(f: List<MutableList<Char>>) = (1..4).fold(f) { ff, _ -> step(ff) }
        val cache = mutableMapOf(field to 0)
        val weights = mutableMapOf(0 to weigh(field))
        var counter = 0
        var prev: Int?
        do {
            ++counter
            field = cycle(field)
            weights[counter] = weigh(field)
            prev = cache.putIfAbsent(field, counter)
        } while (prev == null)
        val cycleLen = counter - prev
        val target = 1_000_000_000
        val valueIndex = (target - prev) % cycleLen + prev
        val weight2 = weights[valueIndex]
        assertEquals("Day 14.2", if (isExample) 64 else 104671, weight2)
    }

    @Test
    fun day15() {
        val input = getString(15).split(",")
        fun HASH(s: String) = s.fold(0) { h, c -> (h + c.code) * 17 % 256 }
        val sum = input.sumOf { HASH(it) }
        assertEquals("Day 15.1", 521434, sum)

        val HASHMAP = Array(256) { mutableListOf<Pair<String, Int>>() }
        fun toOp(s: String) = with (Scanner(s).useDelimiter("[-=]")) { next() to if (hasNext()) nextInt() else null }
        input.map { with (toOp(it)) { Triple(first, HASHMAP[HASH(first)], second) } }.forEach { (label, box, focal) ->
            val lens = box.find { it.first == label }
            if (focal == null) box.remove(lens)
            else if (lens != null) box[box.indexOf(lens)] = label to focal
            else box.add(label to focal)
        }
        val power = HASHMAP.foldIndexed(0) { iBox, acc, box -> acc + box.foldIndexed(0) { iSlot, accBox, lens ->
            accBox + (iBox+1) * (iSlot+1) * lens.second
        } }
        assertEquals("Day 15.2", 248279, power)
    }

    @Test
    fun day16() {
        val input = getInput(16)
        fun beam(dir: Char, x: Int, y: Int): Int {
            val visited = mutableSetOf<Triple<Char, Int, Int>>()
            fun step(dir: Char, x: Int, y: Int) {
                if (y !in input.indices || x !in input[y].indices) return
                if (!visited.add(Triple(dir, x, y))) return

                when (input[y][x]) {
                    '\\' -> when (dir) {
                        '<' -> step('^', x, y-1)
                        '>' -> step('v', x, y+1)
                        '^' -> step('<', x-1, y)
                        'v' -> step('>', x+1, y)
                    }
                    '/' -> when (dir) {
                        '<' -> step('v', x, y+1)
                        '>' -> step('^', x, y-1)
                        '^' -> step('>', x+1, y)
                        'v' -> step('<', x-1, y)
                    }
                    '-' -> when (dir) {
                        '<' -> step('<', x-1, y)
                        '>' -> step('>', x+1, y)
                        '^', 'v' -> { step('<', x-1, y) ; step('>', x+1, y) }
                    }
                    '|' -> when (dir) {
                        '<', '>' -> { step('^', x, y-1) ; step('v', x, y+1) }
                        '^' -> step('^', x, y-1)
                        'v' -> step('v', x, y+1)
                    }
                    else -> when (dir) {
                        '<' -> step('<', x-1, y)
                        '>' -> step('>', x+1, y)
                        '^' -> step('^', x, y-1)
                        'v' -> step('v', x, y+1)
                    }
                }
            }

            step(dir, x, y)
            return visited.map { (_, x, y) -> x to y }.toSet().size
        }

        val active = beam('>', 0, 0)
        assertEquals("Day 16.1", 7562, active)

        val mostActive = max(
            input.indices.maxOf { max(beam('>', 0, it), beam('<', input.first().indices.last, it)) },
            input.first().indices.maxOf { max(beam('v', it, 0), beam('^', it, input.indices.last)) }
        )
        assertEquals("Day 16.2", 7793, mostActive)
    }

    @Test
    fun day17() {
        val input = getInput(17)
        data class Label(val cost: Int, val straight: Int, val x: Int, val y: Int, val pred: Label?) {
            override fun toString() = "lbl($cost, s$straight, $x,$y)"
            fun isValid() = x != -1
        }
        fun dijkstra(targetX: Int, targetY: Int, minStraight: Int, maxStraight: Int): Label {
            val queue = PriorityQueue<Label>(compareBy { it.cost })
            val settled = mutableMapOf<Triple<Pair<Int, Int>, Pair<Int, Int>, Int>, Int>()
            val start = Label(0, 0, 0, 0, Label(0, 0, -1, -1, null))
            queue.add(start)
            while (true) {
                val minlbl = queue.remove()
                val pred = minlbl.pred!!
                if (minlbl.x == targetX && minlbl.y == targetY && minlbl.straight >= minStraight) return minlbl
                listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1).forEach {
                    val (newX, newY) = (minlbl.x to minlbl.y) + it
                    /* check if in field */ if (!input.hasIndices(newX, newY)) return@forEach
                    val is180 = pred.x == newX && pred.y == newY
                    /* check if 180 turn */ if (is180) return@forEach
                    val isStraight = newX == pred.x || newY == pred.y
                    /* check if it may already turn */ if (pred.isValid() && !isStraight && minlbl.straight < minStraight) return@forEach
                    val newStraight = if (isStraight) minlbl.straight + 1 else 1
                    /* check if it may continue to go straight */ if (newStraight > maxStraight) return@forEach
                    val newCost = minlbl.cost + input[newY][newX].digitToInt()
                    val mapItem = Triple(minlbl.x to minlbl.y, newX to newY, newStraight)
                    val oldCost = settled[mapItem]
                    /* check if cheaper way already found */ if (oldCost != null && newCost >= oldCost) return@forEach
                    settled[mapItem] = newCost
                    val newLabel = Label(newCost, newStraight, newX, newY, minlbl)
                    queue.add(newLabel)
                }
            }
        }

        val finalLabel = dijkstra(input.first().indices.last, input.indices.last, 0, 3)
        assertEquals("Day 17.1", 956, finalLabel.cost)

        val finalLabel2 = dijkstra(input.first().indices.last, input.indices.last, 4, 10)
        assertEquals("Day 17.2", 1106, finalLabel2.cost)
    }

    @Test
    fun day18() {
        val ops = getInput(18).map { with (Scanner(it).useDelimiter("[ (#)]+")) { Pair(
            next().first() to nextInt(),
            with (nextInt(16)) { (when (this and 0xF) {0->'R';1->'D';2->'L';else->'U'}) to (this shr 4) }
        ) } }

        fun toPolygon(p: List<Pair<Char, Int>>) = p.scan(Point(0, 0)) { acc, op ->
            with (acc) { when (op.first) {
                'U' -> Point(x, y - op.second)
                'D' -> Point(x, y + op.second)
                'L' -> Point(x - op.second, y)
                else -> Point(x + op.second, y)
            } }
        }
        fun shoelace(p: List<Point>) = abs((p.indices.toList() + 0).zipWithNext().fold(0L) { acc, (i, j) ->
            acc + p[i].x * p[j].y - p[j].x * p[i].y
        }) / 2
        fun circum(p: List<Point>) = (p.indices.toList() + 0).zipWithNext().fold(0L) { acc, (i, j) ->
            acc + abs(p[i].x - p[j].x + p[i].y - p[j].y)
        }
        fun area(p: List<Point>) = shoelace(p) + circum(p) / 2 + 1

        val polygon = toPolygon(ops.map { it.first })
        assertEquals("Day 18.1", 56923, area(polygon))

        val polygon2 = toPolygon(ops.map { it.second })
        assertEquals("Day 18.2", 66296566363189, area(polygon2))
    }

    @Test
    fun day19() {
        val (workflowIn, partIn) = getString(19).split("\n\n").map { it.lines() }
        data class Rule(val action: String, val cat: Char?, val comp: Char?, val value: Int) {
            fun match(part: Map<Char, Int>) = cat == null || part[cat]!!.let { if (comp == '<') it < value else it > value }
        }
        val actionPat = "((?<AR%>[AR])|(?<wf%>[a-z]+))"
        val complexRulePat = "((?<cat>[amsx])(?<comp>[<>])(?<val>\\d+):${actionPat.replace('%', '1')})"
        val ruleRe = Regex("($complexRulePat|${actionPat.replace('%', '2')})")

        val workflows = workflowIn.associate { with (Scanner(it).useDelimiter("[{,}]")) {
            next() to asSequence().map { with (ruleRe.matchEntire(it)!!.groups) {
                Rule(
                    (get("AR1") ?: get("AR2") ?: get("wf1") ?: get("wf2"))!!.value,
                    get("cat")?.value?.single(),
                    get("comp")?.value?.single(),
                    get("val")?.value?.toInt() ?: 0
                )
            } }.toList()
        } }

        val parts = partIn.map { Scanner(it).useDelimiter("[{,}]").asSequence().map {
            it.split("=") }.associate { (k, v) -> k.single() to v.toInt() }
        }

        fun apply(part: Map<Char, Int>, id: String): Int =
            when (val wf = workflows[id]!!.first { it.match(part) }.action) {
                "A" -> part.values.sum()
                "R" -> 0
                else -> apply(part, wf)
            }
        val sum = parts.sumOf { apply(it, "in") }
        assertEquals("Day 19.1", if (isExample) 19114 else 348378, sum)
    }
}