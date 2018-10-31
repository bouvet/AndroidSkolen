package no.bouvet.androidskolen

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class CalculatorViewModelTest {

    @Test
    fun processAddition_isCorrect() {

        val viewModel = CalculatorViewModel()

        val correctResult = viewModel.processAddition("29", "13");

        assertThat(correctResult, `is`("29 + 13 = 42"))

        val invalidInput = viewModel.processAddition("Not number", "Zero")

        assertThat(invalidInput, `is`("Invalid input"))
    }

    @Test
    fun processSubstraction_isCorrect() {

        val viewModel = CalculatorViewModel()

        val correctResult = viewModel.processSubtraction("71", "29");

        assertThat(correctResult, `is`("71 - 29 = 42"))

        val invalidInput = viewModel.processSubtraction("Not number", "Zero")

        assertThat(invalidInput, `is`("Invalid input"))
    }


    @Test
    fun processMultiplication_isCorrect() {

        val viewModel = CalculatorViewModel()

        val correctResult = viewModel.processMultiplication("6", "7");

        assertThat(correctResult, `is`("6 * 7 = 42"))

        val invalidInput = viewModel.processMultiplication("Not number", "Zero")

        assertThat(invalidInput, `is`("Invalid input"))
    }

    @Test
    fun processDivision_isCorrect() {

        val viewModel = CalculatorViewModel()

        val correctResult = viewModel.processDivision("126", "3");

        assertThat(correctResult, `is`("126 / 3 = 42"))

        val invalidInput = viewModel.processDivision("Not number", "Zero")

        assertThat(invalidInput, `is`("Invalid input"))
    }

}
