package com.comp3074.foodquest

import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.comp3074.foodquest.ui.MemberFormFragment
import com.comp3074.foodquest.ui.QuestViewModel
import org.junit.Test

class MemberFormTest {
    @Test fun draftSurvivesRecreationAndInvalidEmailCanBeCorrected() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        val idle = MemberIdleResource()
        scenario.onActivity { activity ->
            ViewModelProvider(activity)[QuestViewModel::class.java].busy.observe(activity) { idle.busy = it }
            activity.openRoot(MemberFormFragment())
        }
        IdlingRegistry.getInstance().register(idle)
        try {
            onView(withId(R.id.member_name)).perform(scrollTo(), replaceText("Draft example"), closeSoftKeyboard())
            onView(withId(R.id.member_email)).perform(scrollTo(), replaceText("not-an-email"), closeSoftKeyboard())
            scenario.recreate()
            scenario.onActivity { activity ->
                ViewModelProvider(activity)[QuestViewModel::class.java].busy.observe(activity) { idle.busy = it }
            }
            onView(withId(R.id.member_name)).check(matches(withText("Draft example")))
            onView(withId(R.id.member_email)).check(matches(withText("not-an-email")))
            onView(withText("Save member")).perform(scrollTo(), click())
            onView(withId(R.id.member_email)).check(matches(hasErrorText("Enter a valid email")))
            onView(withId(R.id.member_email)).perform(scrollTo(), replaceText("student@example.com"), closeSoftKeyboard())
            onView(withId(R.id.member_name)).check(matches(withText("Draft example")))
            // Do not save into the user's normal database: repository persistence
            // is covered separately with member-validation-test.db.
        } finally {
            IdlingRegistry.getInstance().unregister(idle)
            scenario.close()
        }
    }

    @Test fun missingMemberShowsUnavailableState() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        val idle = MemberIdleResource()
        scenario.onActivity { activity ->
            ViewModelProvider(activity)[QuestViewModel::class.java].busy.observe(activity) { idle.busy = it }
            activity.openRoot(MemberFormFragment.newInstance(Long.MAX_VALUE))
        }
        IdlingRegistry.getInstance().register(idle)
        try {
            onView(withText("This team member is no longer available. Return to the team list.")).check(matches(isDisplayed()))
        } finally {
            IdlingRegistry.getInstance().unregister(idle)
            scenario.close()
        }
    }

    private class MemberIdleResource : IdlingResource {
        private var callback: IdlingResource.ResourceCallback? = null
        @Volatile var busy = true
            set(value) { field = value; if (!value) callback?.onTransitionToIdle() }
        override fun getName() = "Team member database"
        override fun isIdleNow() = !busy
        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) { this.callback = callback }
    }
}
