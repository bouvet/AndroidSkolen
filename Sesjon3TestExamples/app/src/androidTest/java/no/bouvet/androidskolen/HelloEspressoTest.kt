package no.bouvet.androidskolen

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class HelloEspressoTest {

    @get:Rule
    var _rule: ActivityTestRule<HelloActivity> = ActivityTestRule(HelloActivity::class.java)

    @Test
    fun testMainInteractionEspresso() {
        // TODO: Oppgave 3 - Test tilsvarende funksjoner som i oppgave 2

        onView(withId(R.id.hello_output))

        onView(withId(R.id.hello_input))

        onView(withId(R.id.hello_button))

        onView(withId(R.id.hello_output))
    }

}
