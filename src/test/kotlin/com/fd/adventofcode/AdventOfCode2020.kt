import com.fd.adventofcode.AdventBase
import org.junit.Test
import org.junit.Assert.assertEquals
import java.util.Scanner
import java.util.regex.Pattern

class AdventOfCode2020 : AdventBase(2020) {
    @Test
    fun day1() {
        val entries = getInput(1).map { it.toInt() }.sorted()
        val day1 = entries.map { ei -> entries.map { ej -> ei to ej } }.flatten()
            .filter { (i, j) -> i + j == 2020 }
            .map { (i, j) -> i * j }.first()
        assertEquals("Day 1.1", 471019, day1)

        val day2 = run {
            for (i in entries.indices) {
                for (j in i+1..<entries.size) {
                    for (k in j+1..<entries.size) {
                        if (entries[i] + entries[j] + entries[k] == 2020) {
                            return@run entries[i] * entries[j] * entries[k]
                        }
                    }
                }
            }
        }

        assertEquals("Day 1.2", 103927824, day2)
    }

    @Test
    fun day2() {
        data class PassPolicy(val min: Int, val max: Int, val ch: Char, val pass: String)
        val input = getInput(2).map {
            val sc = Scanner(it).useDelimiter(Pattern.compile("[- :]+"))
            PassPolicy(sc.nextInt(), sc.nextInt(), sc.next().first(), sc.next())
        }
        val valid = input.count { (min, max, ch, pass) -> pass.count { it == ch } in min..max }
        assertEquals("Day 2.1", 572, valid)

        val valid2 = input.count { (min, max, ch, pass) -> (pass[min-1] == ch) xor (pass[max-1] == ch) }
        assertEquals("Day 2.2", 306, valid2)
    }

    @Test
    fun day3 () {
        val input = getInput(3)
        val width = input[0].length
        val height = input.size
        val trees = run {
            var x = 0
            var y = 0
            var count = 0
            while (y < height) {
                if (input[y][x % width] == '#') {
                    count++
                }
                x += 3
                y++
            }
            count
        }
        assertEquals("Day 3.1", 156, trees)

        val day3_2 = run {
            val slopes = listOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2)
            slopes.map { (dx, dy) ->
                var x = 0
                var y = 0
                var count = 0L
                while (y < height) {
                    if (input[y][x % width] == '#') {
                        count++
                    }
                    x += dx
                    y += dy
                }
                count
            }.reduce { a, b -> a * b }
        }
        assertEquals("Day 3.2", 3521829480, day3_2)
    }
}