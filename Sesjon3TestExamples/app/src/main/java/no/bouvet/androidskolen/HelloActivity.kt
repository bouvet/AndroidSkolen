package no.bouvet.androidskolen

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class HelloActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)

        val output = findViewById<TextView>(R.id.hello_output)
        val input = findViewById<EditText>(R.id.hello_input)
        val button = findViewById<Button>(R.id.hello_button)
        val next = findViewById<Button>(R.id.hello_next)

        button.setOnClickListener {
            output.text = """Hello ${input.text.toString()}!"""
        }
        next.setOnClickListener {
            val intent = Intent(this, CalculatorActivity::class.java)
            intent.putExtra("NAME", input.text.toString())
            startActivity(intent)
        }
    }
}
