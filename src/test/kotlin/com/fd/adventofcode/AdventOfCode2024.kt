
package com.fd.adventofcode

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Scanner
import kotlin.math.abs
import kotlin.text.indexOf

class AdventOfCode2024 : AdventBase(2024) {
    @Test
    fun `day 1, historian hysteria`() {
        val input = getInput(1).map { it.split(Regex("\\s+")).map { it.toLong() } }
        val left = input.map { it[0] }.sorted()
        val right = input.map { it[1] }.sorted()
        val result = left.zip(right).sumOf { (l, r) -> abs(r - l) }
        assertEquals("Day 1.1", 1603498, result)

        val score = left.sumOf { l -> right.count { it == l } * l }
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

        fun countXmas(input: List<String>) = input.sumOf { Regex("XMAS").findAll(it).count() }

        val normal = countXmas(input)
        val backwards = countXmas(input.rotated180())
        val up = countXmas(input.rotatedRight())
        val down = countXmas(input.rotatedLeft())
        val diagn = countXmas(input.rotated45())
        val diagb = countXmas(input.rotated180().rotated45())
        val diagu = countXmas(input.rotatedRight().rotated45())
        val diagd = countXmas(input.rotatedLeft().rotated45())

        val total = normal + backwards + up + down + diagn + diagb + diagu + diagd
        assertEquals("Day 4.1", 2496, total)

        fun List<List<Char>>.xmas(row: Int, col: Int): Boolean = row in 1..<lastIndex && col in 1..<this[0].lastIndex &&
                this[row][col] == 'A' &&
                ((this[row+1][col+1] == 'M' && this[row+1][col-1] == 'M' && this[row-1][col+1] == 'S' && this[row-1][col-1] == 'S') ||
                (this[row+1][col+1] == 'M' && this[row+1][col-1] == 'S' && this[row-1][col+1] == 'M' && this[row-1][col-1] == 'S') ||
                (this[row+1][col+1] == 'S' && this[row+1][col-1] == 'S' && this[row-1][col+1] == 'M' && this[row-1][col-1] == 'M')||
                (this[row+1][col+1] == 'S' && this[row+1][col-1] == 'M' && this[row-1][col+1] == 'S' && this[row-1][col-1] == 'M'))

        val xmascount = (0 until input.size).map { row -> (0 until input[0].length).count { col -> input.toMatrix().xmas(row, col) }.also { print("$it ") } }.sum()
        assertEquals("Day 4.2", 1967, xmascount)
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

        val validatedMiddles = updates.mapNotNull { it.sorted().run { if (it != this) this[size/2] else null } }
        val sum2 = validatedMiddles.sumOf { it.num }
        assertEquals("Day 5.2", 4719, sum2)
    }

    @Test
    fun `day 6, guard gallivant`() {
        val map = getInput(1006).map { it.replace(".", "@") }
        val matrix = map.toMatrix()
        val xlen = matrix[0].size
        val ylen = matrix.size
        val (sx, sy) = '^'.let { s -> map.indexOfFirst { s in it }.let { map[it].indexOf(s) to it } }
        var (cx, cy) = sx to sy
        var dir = matrix[sy][sx]
        while (cx in matrix[0].indices && cy in matrix.indices) {
            matrix[cy][cx] = 'X'
            when (dir) {
                '^' -> { if (cy > 0 && matrix[cy-1][cx] == '#') { dir = '>' } else { cy -= 1 } }
                'v' -> { if (cy+1 < ylen && matrix[cy+1][cx] == '#') { dir = '<' } else { cy += 1 } }
                '<' -> { if (cx > 0 && matrix[cy][cx-1] == '#') { dir = '^' } else { cx -= 1 } }
                '>' -> { if (cx+1 < xlen && matrix[cy][cx+1] == '#') { dir = 'v' } else { cx += 1 } }
                else -> break
            }
        }
        val visited = matrix.sumOf { it.count { c -> c == 'X' } }
        //assertEquals("Day 6.1", 4647, visited)
        assertEquals("Day 6.1", 41, visited)

        fun dir2bits(dir: Char) = when (dir) { '^' -> 1 ; 'v' -> 2 ; '<' -> 4 ; '>' -> 8 ; else -> 0 }
        fun setDir(dir: Char, orig: Char): Char = (orig.code or dir2bits(dir)).toChar()
        fun hasDir(dir: Char, orig: Char): Boolean = (orig.code and dir2bits(dir)) != 0
        fun findloop(mat: List<MutableList<Char>>, x: Int, y: Int, d: Char): Boolean {
            var cx = x
            var cy = y
            var dir = d
            while (cx in mat[0].indices && cy in mat.indices) {
                if (hasDir(dir, mat[cy][cx])) return true.also { println ("Loop from $x:$y to $cx:$cy - $dir") }
                mat[cy][cx] = setDir(dir, mat[cy][cx])
                when (dir) {
                    '^' -> { if (cy > 0 && mat[cy-1][cx] == '#') { dir = '>' } else { cy -= 1 } }
                    'v' -> { if (cy+1 < ylen && mat[cy+1][cx] == '#') { dir = '<' } else { cy += 1 } }
                    '<' -> { if (cx > 0 && mat[cy][cx-1] == '#') { dir = '^' } else { cx -= 1 } }
                    '>' -> { if (cx+1 < xlen && mat[cy][cx+1] == '#') { dir = 'v' } else { cx += 1 } }
                    else -> break
                }
            }
            return false.also { println("No loop at $x:$y - $d") }
        }
        fun obstacleOnRight(mat: List<MutableList<Char>>, x: Int, y: Int, d: Char): Boolean {
            return when (d) {
                '^' -> { mat[y].subList(x, mat[y].size).contains('#') }
                'v' -> { mat[y].subList(0, x).contains('#') }
                '<' -> { mat.column(x).subList(0, y).contains('#') }
                '>' -> { mat.column(x).subList(y, mat.size).contains('#') }
                else -> false
            }.also { println("OonR: $x-$y - $d = $it") }
        }
        var mat = map.toMatrix().also { it.printBoxed('@') }
        cx = sx
        cy = sy
        dir = mat[sy][sx]
        mat[cy][cx] = setDir(dir, '@')
        var possible = 0
        while (cx in matrix[0].indices && cy in matrix.indices) {
            mat[cy][cx] = setDir(dir, mat[cy][cx])
            when (dir) {
                '^' -> { if (cy > 0 && mat[cy-1][cx] == '#') {
                    dir = '>'
                    if (obstacleOnRight(mat, cx, cy, dir) && (cy != sy || cx+1 != sx) && cx+1 < xlen && mat[cy][cx+1] == '@') { if (findloop(mat.deepcopy().apply { this[cy][cx+1] = '#' ; println("^> $cx-$cy - $dir") ; this.printBoxed('@') }, cx, cy, dir)) ++possible }
                } else {
                    cy -= 1
                    if (cy in matrix.indices && obstacleOnRight(mat, cx, cy, dir) && (cy-1 != sy || cx != sx) && cy-1 >= 0 && mat[cy-1][cx] == '@') { if (findloop(mat.deepcopy().apply { this[cy-1][cx] = '#' ; println("^^ $cx-$cy - $dir") ; this.printBoxed('@') }, cx, cy, dir)) ++possible }
                } }
                'v' -> { if (cy+1 < ylen && mat[cy+1][cx] == '#') {
                    dir = '<'
                    if (obstacleOnRight(mat, cx, cy, dir) && (cy != sy || cx-1 != sx) && cx-1 >= 0 && map[cy][cx-1] == '@') { if (findloop(mat.deepcopy().apply { this[cy][cx-1] = '#' ; println("v< $cx-$cy - $dir") ; this.printBoxed('@') }, cx, cy, dir)) ++possible }
                } else {
                    cy += 1
                    if (cy in matrix.indices && obstacleOnRight(mat, cx, cy, dir) && (cy+1 != sy || cx != sx) && cy+1 < ylen && map[cy+1][cx] == '@') { if (findloop(mat.deepcopy().apply { this[cy+1][cx] = '#' ; println("vv $cx-$cy - $dir") ; this.printBoxed('@') }, cx, cy, dir)) ++possible }
                } }
                '<' -> { if (cx > 0 && mat[cy][cx-1] == '#') {
                    dir = '^'
                    if (obstacleOnRight(mat, cx, cy, dir) && (cy-1 != sy || cx != sx) && cy-1 >= 0&& map[cy-1][cx] == '@') { if (findloop(mat.deepcopy().apply { this[cy-1][cx] = '#' ; println("<^ $cx-$cy - $dir") ; this.printBoxed('@') }, cx, cy, dir)) ++possible }
                } else {
                    cx -= 1
                    if (cx in matrix[0].indices && obstacleOnRight(mat, cx, cy, dir) && (cy != sy || cx-1 != sx) && cx-1 >= 0 && map[cy][cx-1] == '@') { if (findloop(mat.deepcopy().apply { this[cy][cx-1] = '#' ; println("<< $cx-$cy - $dir") ; this.printBoxed('@') }, cx, cy, dir)) ++possible }
                } }
                '>' -> { if (cx+1 < xlen && mat[cy][cx+1] == '#') {
                    dir = 'v'
                    if (obstacleOnRight(mat, cx, cy, dir) && (cy+1 != sy || cx != sx) && cy+1 < ylen && map[cy+1][cx] == '@') { if (findloop(mat.deepcopy().apply { this[cy+1][cx] = '#' ; println(">v $cx-$cy - $dir") ; this.printBoxed('@') }, cx, cy, dir)) ++possible }
                } else {
                    cx += 1
                    if (cx in matrix[0].indices && obstacleOnRight(mat, cx, cy, dir) && (cy != sy || cx+1 != sx) && cx+1 < xlen && map[cy][cx+1] == '@') { if (findloop(mat.deepcopy().apply { this[cy][cx+1] = '#' ; println(">> $cx-$cy - $dir") ; this.printBoxed('@') }, cx, cy, dir)) ++possible }
                } }
                else -> break
            }
        }
        assertEquals("Day 6.2", 313, possible)
        assertEquals("Day 6.2", 6, possible)
    }

    @Test
    fun `day 6, guard gallivant - brute force`() {
        val map = getInput(6).map { it.replace(".", "@") }
        val matrix = map.toMatrix()
        val xlen = matrix[0].size
        val ylen = matrix.size
        val (sx, sy) = '^'.let { s -> map.indexOfFirst { s in it }.let { map[it].indexOf(s) to it } }
        var (cx, cy) = sx to sy
        var dir = matrix[sy][sx]
        while (cx in matrix[0].indices && cy in matrix.indices) {
            matrix[cy][cx] = 'X'
            when (dir) {
                '^' -> { if (cy > 0 && matrix[cy-1][cx] == '#') { dir = '>' } else { cy -= 1 } }
                'v' -> { if (cy+1 < ylen && matrix[cy+1][cx] == '#') { dir = '<' } else { cy += 1 } }
                '<' -> { if (cx > 0 && matrix[cy][cx-1] == '#') { dir = '^' } else { cx -= 1 } }
                '>' -> { if (cx+1 < xlen && matrix[cy][cx+1] == '#') { dir = 'v' } else { cx += 1 } }
                else -> break
            }
        }
        val visited = matrix.sumOf { it.count { it == 'X' } }
        assertEquals("Day 6.1", 4647, visited)
        //assertEquals("Day 6.1", 41, visited)

        // track running time
        val start = System.currentTimeMillis()
        fun dir2bits(dir: Char) = when (dir) { '^' -> 1 ; 'v' -> 2 ; '<' -> 4 ; '>' -> 8 ; else -> 0 }
        fun setDir(dir: Char, orig: Char): Char = (orig.code or dir2bits(dir)).toChar()
        fun hasDir(dir: Char, orig: Char): Boolean = (orig.code and dir2bits(dir)) != 0
        fun findLoop(mat: List<MutableList<Char>>, x: Int, y: Int, d: Char): Boolean {
            var cx = x
            var cy = y
            var dir = d
            while (cx in mat[0].indices && cy in mat.indices) {
                if (hasDir(dir, mat[cy][cx])) return true
                mat[cy][cx] = setDir(dir, mat[cy][cx])
                when (dir) {
                    '^' -> { if (cy > 0 && mat[cy-1][cx] == '#') { dir = '>' } else { cy -= 1 } }
                    'v' -> { if (cy+1 < ylen && mat[cy+1][cx] == '#') { dir = '<' } else { cy += 1 } }
                    '<' -> { if (cx > 0 && mat[cy][cx-1] == '#') { dir = '^' } else { cx -= 1 } }
                    '>' -> { if (cx+1 < xlen && mat[cy][cx+1] == '#') { dir = 'v' } else { cx += 1 } }
                    else -> break
                }
            }
            return false
        }

        var possible = 0
        for (y in map.indices) {
            for (x in map[0].indices) {
                if ((x != sx || y != sy) && matrix[y][x] == 'X') {
                    var mat = map.toMatrix()
                    dir = mat[sy][sx]
                    mat[sy][sx] = '@'
                    mat[y][x] = '#'
                    if (findLoop(mat, sx, sy, dir)) ++possible
                }
            }
        }
        val end = System.currentTimeMillis()
        println("Time: ${end - start} ms")
        assertEquals("Day 6.2", 1723, possible)
    }

    @Test
    fun `day 7, bridge repair`() {
        val input = getInput(7).map { Scanner(it).useDelimiter(": | ").findAllLong() }
        fun recurse(target: Long, numbers: List<Long>, acc: Long? = null): Boolean {
            if (numbers.isEmpty()) return acc == target
            return recurse(target, numbers.drop(1), (acc ?: 0) + numbers.first()) ||
                    recurse(target, numbers.drop(1), (acc ?: 1) * numbers.first())
        }
        val result = input.filter { recurse(it[0], it.drop(1)) }.sumOf { it[0] }
        assertEquals("Day 7.1", 932137732557, result)

        fun recurse2(target: Long, numbers: List<Long>, acc: Long? = null): Boolean {
            if (numbers.isEmpty()) return acc == target
            return recurse2(target, numbers.drop(1), (acc ?: 0) + numbers.first()) ||
                    recurse2(target, numbers.drop(1), (acc ?: 1) * numbers.first()) ||
                    recurse2(target, numbers.drop(1), ((acc?.toString() ?: "") + "${numbers.first()}").toLong())
        }
        val result2 = input.filter { recurse2(it[0], it.drop(1)) }.sumOf { it[0] }
        assertEquals("Day 7.2", 932137732557, result2)
    }

    @Test
    fun `day 8, resonant collinearity`() {
        val map = getInput(8)
        val antennas = buildMap<Char, List<Pair<Int, Int>>> {
            map.forEachIndexed { y, row ->
                row.forEachIndexed { x, c -> if (c != '.') this[c] = (this[c] ?: emptyList()) + (x to y) }
            }
            forEach { (k, v) -> if (v.size == 1) remove(k) }
        }

        val antinodes = mutableSetOf<Pair<Int, Int>>()

        antennas.forEach { (k, v) -> v.forEach { a -> v.forEach { b ->
            if (a == b) return@forEach
            val dx = b.first - a.first
            val dy = b.second - a.second
            val a1 = Pair(a.first - dx, a.second - dy)
            val a2 = Pair(b.first + dx, b.second + dy)
            if (a1.first in map[0].indices && a1.second in map.indices) antinodes.add(a1)
            if (a2.first in map[0].indices && a2.second in map.indices) antinodes.add(a2)
        } } }

        map.toMatrix().also { mat -> antinodes.forEach { (x, y) -> mat[y][x] = 'X' } }.printBoxed()

        assertEquals("Day 8.1", 323, antinodes.size)

        val resonants = mutableSetOf<Pair<Int, Int>>()
        antennas.forEach { (k, v) -> v.forEach { a -> v.forEach { b ->
            if (a == b) return@forEach
            val dx = b.first - a.first
            val dy = b.second - a.second
            var a1 = a
            while (a1.first in map[0].indices && a1.second in map.indices) {
                resonants.add(a1)
                a1 = Pair(a1.first + dx, a1.second + dy)
            }
            var a2 = b
            while (a2.first in map[0].indices && a2.second in map.indices) {
                resonants.add(a2)
                a2 = Pair(a2.first - dx, a2.second - dy)
            }
        } } }

        map.toMatrix().also { mat -> resonants.forEach { (x, y) -> mat[y][x] = 'X' } }.printBoxed()

        assertEquals("Day 8.2", 0, resonants.size)
    }
}