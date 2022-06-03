package com.udacity.project4

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText


const val remindertitle = "Reminder 1"
const val reminderDesc = "Description for Reminder 1"

fun viewWithId(id:Int):ViewInteraction = Espresso.onView(withId(id))
fun viewWithText(textId:Int):ViewInteraction = Espresso.onView(withText(textId))

fun ViewInteraction.type(text:String):ViewInteraction = perform(ViewActions.typeText(text),closeSoftKeyboard())

fun ViewInteraction.click() : ViewInteraction = perform(ViewActions.click())

private const val EDGE_FUZZ_FACTOR = 0.083f


