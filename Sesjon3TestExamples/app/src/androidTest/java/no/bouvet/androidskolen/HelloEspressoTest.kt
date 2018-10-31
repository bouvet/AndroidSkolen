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
        onView(withId(R.id.hello_output)).check(matches(withText("Hello World!")))

        onView(withId(R.id.hello_input)).perform(typeText("Pingvinen"), closeSoftKeyboard())

        onView(withId(R.id.hello_button)).perform(click())

        onView(withId(R.id.hello_output)).check(matches(withText("Hello Pingvinen!")))
    }

}
