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
        onView(withId(R.id.hello_input)).perform(typeText("Pingvinen"), closeSoftKeyboard())

        onView(withId(R.id.hello_next)).perform(click())

        intended(allOf(
                toPackage("no.bouvet.androidskolen"),
                hasComponent(hasShortClassName(".CalculatorActivity")),
                hasExtra("NAME", "Pingvinen")
        ))

        onView(withId(R.id.hello_output)).check(matches(withText("Hello Pingvinen!")))

    }

}
