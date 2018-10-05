package no.bouvet.androidskolen

import java.lang.Exception

class CalculatorViewModel {

    fun processAddition(firstInput: String, secondInput: String): String {
        try {
            val augend = Integer.parseInt(firstInput)
            val addend = Integer.parseInt(secondInput)
            val sum = IntegerCalculator.add(augend, addend)
            return "$augend + $addend = $sum"
        }
        catch (e : Exception) {
            return "Invalid input"
        }
    }

    fun processSubtraction(firstInput: String, secondInput: String): String {
        try {
            val minuend = Integer.parseInt(firstInput)
            val substrahend = Integer.parseInt(secondInput)
            val difference = IntegerCalculator.subtract(minuend, substrahend)
            return "$minuend - $substrahend = $difference"
        }
        catch (e : Exception) {
            return "Invalid input"
        }
    }

    fun processMultiplication(firstInput: String, secondInput: String): String {
        try {
            val multiplicand = Integer.parseInt(firstInput)
            val multiplier = Integer.parseInt(secondInput)
            val product = IntegerCalculator.multiply(multiplicand, multiplier)
            return "$multiplicand * $multiplier = $product"
        }
        catch (e : Exception) {
            return "Invalid input"
        }
    }

    fun processDivision(firstInput: String, secondInput: String): String {
        try {
            val dividend = Integer.parseInt(firstInput)
            val divisor = Integer.parseInt(secondInput)
            val quotient = IntegerCalculator.divide(dividend, divisor)
            return "$dividend / $divisor = $quotient"
        }
        catch (e : Exception) {
            return "Invalid input"
        }
    }
}
