package com.comp3074.foodquest

import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.comp3074.foodquest.data.FoodQuestDatabase
import com.comp3074.foodquest.data.QuestRepository
import com.comp3074.foodquest.ui.QuestListFragment
import com.comp3074.foodquest.ui.QuestViewModel
import org.junit.Assert.*
import org.junit.Test
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.allOf

class QuestWorkflowTest {
    @Test fun createSearchRateCompleteAndReopenQuest() {
        val name = "Workflow test ${System.currentTimeMillis()}"
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        val idle = DatabaseIdleResource()
        scenario.onActivity { activity ->
            val model = ViewModelProvider(activity)[QuestViewModel::class.java]
            model.busy.observe(activity) { idle.busy = it }
            activity.openRoot(QuestListFragment())
        }
        IdlingRegistry.getInstance().register(idle)
        try {
            onView(withId(R.id.add_quest)).perform(click())
            onView(withId(R.id.quest_name)).perform(scrollTo(), replaceText(name), closeSoftKeyboard())
            onView(withId(R.id.quest_place)).perform(scrollTo(), replaceText("Test cafe"), closeSoftKeyboard())
            onView(withId(R.id.quest_address)).perform(scrollTo(), replaceText("200 King Street East, Toronto, ON"), closeSoftKeyboard())
            onView(withId(R.id.quest_task)).perform(scrollTo(), replaceText("Find a budget lunch"), closeSoftKeyboard())
            onView(withId(R.id.quest_tags)).perform(scrollTo(), replaceText("Budget, Lunch"), closeSoftKeyboard())
            scenario.recreate()
            scenario.onActivity { activity ->
                ViewModelProvider(activity)[QuestViewModel::class.java].busy.observe(activity) { idle.busy = it }
            }
            onView(withId(R.id.quest_name)).check(matches(withText(name)))
            onView(withText("Save quest")).perform(scrollTo(), click())
            onView(withId(R.id.search_query)).perform(replaceText(name), closeSoftKeyboard())
            onView(allOf(withId(R.id.row_name), withText(name))).check(matches(isDisplayed())).perform(click())
            onView(withText("Rate quest")).perform(scrollTo(), click())
            onView(withText("5 stars")).perform(click())
            onView(withText("Complete quest")).perform(scrollTo(), click())
            onView(withText("Completed")).check(matches(not(isEnabled())))
            scenario.close()
            val repository = QuestRepository(FoodQuestDatabase.open(InstrumentationRegistry.getInstrumentation().targetContext))
            try {
                val saved = repository.quests().single { it.name == name }
                assertTrue(saved.completed)
                assertEquals(5, saved.rating)
                repository.delete(saved.id)
            } finally { repository.close() }
        } finally {
            IdlingRegistry.getInstance().unregister(idle)
            scenario.close()
        }
    }

    private class DatabaseIdleResource : IdlingResource {
        private var callback: IdlingResource.ResourceCallback? = null
        @Volatile var busy = true
            set(value) { field = value; if (!value) callback?.onTransitionToIdle() }
        override fun getName() = "Quest database"
        override fun isIdleNow() = !busy
        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) { this.callback = callback }
    }
}
