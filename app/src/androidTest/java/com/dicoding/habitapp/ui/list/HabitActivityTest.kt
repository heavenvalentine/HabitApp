package com.dicoding.habitapp.ui.list

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.dicoding.habitapp.ui.add.AddHabitActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.dicoding.habitapp.R

//TODO 16 : DONE Write UI test to validate when user tap Add Habit (+), the AddHabitActivity displayed
class HabitActivityTest {
    @get:Rule
    val rule: ActivityScenarioRule<HabitListActivity> = ActivityScenarioRule(HabitListActivity::class.java)

    @Before
    fun setUp(){
        Intents.init()
        rule.scenario
    }
    @Test
    fun UserClickAddHabitButton_OpensAddHabitActivity(){
        rule.scenario
        Espresso.onView(withId(R.id.fab)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(AddHabitActivity::class.java.name))
        }
}