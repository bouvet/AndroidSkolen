package no.bouvet.androidskolen

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName
import android.support.test.espresso.intent.matcher.IntentMatchers.*
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class CalculatorEspressoTest {

    @get:Rule
    var _mIntentsRule: IntentsTestRule<HelloActivity> = IntentsTestRule(HelloActivity::class.java)

    @Test
    fun testSecondaryActivityActualEvent() {
        // Vi er i "HelloActivity" foreløpig
        // TODO: Oppgave 3 - Simuler det som skal til for å komme til neste activity
        onView(withId(R.id.hello_input))

        onView(withId(R.id.hello_next))

        intended(allOf(
                toPackage("no.bouvet.androidskolen")//,
                // TODO: Oppgave 3 - Sjekk at vi er på vei til rett aktivitet med rett Extras
        ))

        // TODO: Oppgave 3 - Sjekk resultatet
        onView(withId(R.id.hello_output))

    }

}
