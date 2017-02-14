package com.applepluot.todoapp;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;

@RunWith(AndroidJUnit4.class)
public class EspressoSmokeTest {
    Logger logger = Logger.getLogger(this.getClass().getName());

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void testAddAndDeleteItem() {
        onView(withId(R.id.etAddItemText))
                .check(matches(withHint(containsString("Add a new item"))));

        onView(withId(R.id.etAddItemText))
                .perform(ViewActions.typeText("Hello"));

        onView(withId(R.id.btnAdd))
                .perform(ViewActions.click());

        onData(allOf(is(instanceOf(String.class)), is("Hello")))
                .inAdapterView(withId(R.id.lvItems))
                .atPosition(0)
                .check(ViewAssertions.matches(ViewMatchers.withText("Hello")));

        // Delete
        onData(allOf(is(instanceOf(String.class)), is("Hello"))) // Use Hamcrest matchers to match item
                .inAdapterView(withId(R.id.lvItems)) // Specify the explicit id of the ListView
                .atPosition(0)
                .perform(longClick()); // Standard ViewAction
    }

    @Test
    public void testEditAndDeleteItem() {
        onView(withId(R.id.etAddItemText))
                .perform(ViewActions.typeText("Edit"));

        onView(withId(R.id.btnAdd))
                .perform(ViewActions.click());

        // Edit and Delete
        onData(startsWith("Edit"))
                .inAdapterView(withId(R.id.lvItems))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.etEditItem))
                .perform(clearText())
                .perform(ViewActions.typeText("Edit 2"));
        onView(withId(R.id.btnSave))
                .perform(click());
        onData(startsWith("Edit 2"))
                .inAdapterView(withId(R.id.lvItems))
                .atPosition(0)
                .perform(longClick());
    }

}