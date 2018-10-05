package no.bouvet.androidskolen

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.hamcrest.core.Is.`is`
import org.junit.Assert

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule



/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class HelloInstrumentedTest {

    @get:Rule
    var _rule: ActivityTestRule<HelloActivity> = ActivityTestRule(HelloActivity::class.java)

    @Test
    fun testMainInteractionRegularInstrumented() {

        val activity = _rule.activity
        val output = activity.findViewById<TextView>(R.id.hello_output)
        val input = activity.findViewById<EditText>(R.id.hello_input)
        val button = activity.findViewById<Button>(R.id.hello_button)

        Assert.assertThat(output.text.toString(), `is`("Hello World!"))

        button.post {
            // TODO: Oppgave 2 - Test at å legge inn data og trykke på knapp gir resultat
        }
    }

}
