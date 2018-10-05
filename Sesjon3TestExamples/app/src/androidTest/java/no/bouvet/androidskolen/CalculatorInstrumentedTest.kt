package no.bouvet.androidskolen

import android.content.Intent
import android.support.test.InstrumentationRegistry.*
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.intent.Intents.*
import android.support.test.espresso.intent.matcher.ComponentNameMatchers.*
import android.support.test.espresso.intent.matcher.IntentMatchers.*
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.widget.TextView
import org.hamcrest.core.AllOf.*
import org.hamcrest.core.Is.`is`

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
class CalculatorInstrumentedTest {

    @get:Rule
    var _rule: ActivityTestRule<CalculatorActivity> = object : ActivityTestRule<CalculatorActivity>(CalculatorActivity::class.java) {
        override fun getActivityIntent(): Intent {
            val intent = Intent(Intent.ACTION_MAIN)

            // TODO: Oppgave 2 - legg ved extras som om det kommer fra HelloActivity

            return intent
        }
    }

    @Test
    fun testSecondaryActivityInjectedIntent() {

        val activity = _rule.activity
        val output = activity.findViewById<TextView>(R.id.hello_output)

        // TODO: Oppgave 2 - Test at extras fra intent gir resultat i GUI

    }

}
