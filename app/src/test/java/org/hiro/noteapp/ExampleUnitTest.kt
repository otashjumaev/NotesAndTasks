package org.hiro.noteapp

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val arraylist = arrayListOf<Int>(1, 2, 3, 4, 5)
        println(arraylist)
        arraylist.remove(3)
        println(arraylist.indexOf(5))
        assertEquals(4, 2 + 2)


    }
}