package no.bouvet.androidskolen

import org.hamcrest.CoreMatchers.`is`
import org.junit.Test

import org.junit.Assert.*

/**
 * Local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class IntegerCalculatorTest {

    @Test
    fun addition_isCorrect() {

        val sum = IntegerCalculator.add(29, 13)

        assertEquals(42, sum)

    }

    @Test
    fun substraction_isCorrect() {

        val difference = IntegerCalculator.subtract(71, 29)

        assertThat(difference, `is`(42))

    }

    @Test
    fun multiplication_isCorrect() {

        val product = IntegerCalculator.multiply(6, 7)

        assertEquals("The magic number should be 42", 42, product)

    }

    @Test
    fun division_isAtleastSomewhatCorrect() {

        val quotient = IntegerCalculator.divide(126, 3)

        assertEquals("The magic number should be atleast somewhat near 42", 42.0, quotient.toDouble(), 1.0)

    }

}
