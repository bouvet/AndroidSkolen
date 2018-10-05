package no.bouvet.androidskolen

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class CalculatorActivity : AppCompatActivity() {

    val firstInput by lazy { findViewById<EditText>(R.id.first_input) }
    val secondInput by lazy { findViewById<EditText>(R.id.second_input) }
    val addButton by lazy { findViewById<Button>(R.id.add_button) }
    val subtractButton by lazy { findViewById<Button>(R.id.subtract_button) }
    val multiplyButton by lazy { findViewById<Button>(R.id.multiply_button) }
    val divideButton by lazy { findViewById<Button>(R.id.divide_button) }
    val calculatorOutput by lazy { findViewById<TextView>(R.id.calculator_output) }

    val calculatorViewModel = CalculatorViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        val name = intent.extras.getString("NAME", "")
        val output = findViewById<TextView>(R.id.hello_output)
        if (name.length > 0) {
            output.setText("Hello $name!")
        }

        addButton.setOnClickListener {
            calculatorOutput.text = calculatorViewModel.processAddition(firstInput.text.toString(), secondInput.text.toString())
        }
        subtractButton.setOnClickListener {
            calculatorOutput.text = calculatorViewModel.processSubtraction(firstInput.text.toString(), secondInput.text.toString())
        }
        multiplyButton.setOnClickListener {
            calculatorOutput.text = calculatorViewModel.processMultiplication(firstInput.text.toString(), secondInput.text.toString())
        }
        divideButton.setOnClickListener {
            calculatorOutput.text = calculatorViewModel.processDivision(firstInput.text.toString(), secondInput.text.toString())
        }

    }
}
