package com.fd.adventofcode

import org.junit.Test
import kotlin.test.assertEquals

class UtilTest : AdventBase(0) {
    @Test
    fun intToCircledTest() {
        assertEquals('②', 2.toCircled())
    }

    @Test
    fun iotaTest() {
        assertEquals(50, iota(50).first())
        assertEquals(5, iota().drop(5).first())
    }

    @Test
    fun rotatedLeftTest() {
        val input = listOf("abc", "def")
        val rotated = input.toMatrix().rotatedLeft().toStrings()
        assertEquals(listOf("cf", "be", "ad"), rotated)
    }

    @Test
    fun rotatedRightTest() {
        val input = listOf("abc", "def")
        val rotated = input.toMatrix().rotatedRight().toStrings()
        assertEquals(input.toMatrix().rotatedLeft().rotatedLeft().rotatedLeft().toStrings(), rotated)
        assertEquals(listOf("da", "eb", "fc"), rotated)
    }
}