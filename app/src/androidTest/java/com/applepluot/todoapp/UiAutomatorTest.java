package com.applepluot.todoapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
@LargeTest
public class UiAutomatorTest {
    /**
     * The target app package.
     */
    private static final String TARGET_PACKAGE =
            InstrumentationRegistry.getTargetContext().getPackageName();
    private static final String TODO_APP_PACKAGE
            = "com.applepluot.todoapp";

    private static final int LAUNCH_TIMEOUT = 15000;
    private static final int DEFAULT_TIMEOUT = 3000;
    private static final String TAG = UiAutomatorTest.class.getSimpleName();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private UiDevice mDevice;
    private Context mContext;

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.
     */
    private String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    @Before
    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the app
        Context context = InstrumentationRegistry.getContext();
        System.out.println("TARGET: " + TARGET_PACKAGE);
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(TARGET_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(TARGET_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void checkPreconditions() {
        assertThat(mDevice, notNullValue());
    }

    @Test
    public void testAppLaunch() {
        Log.i(TAG, "testAppLaunch");
        final String addButtonDescription = mContext.getString(R.string.add_button);
        UiObject2 addButton = mDevice.findObject(By.desc(addButtonDescription));
        assertTrue(addButton.isEnabled());
        UiObject2 addItemText = mDevice.wait(Until.findObject(
                By.res(TODO_APP_PACKAGE, "etAddItemText")), DEFAULT_TIMEOUT);
        assertTrue(addItemText.isEnabled());
    }

    @Test
    public void testAddAndRemoveItem() {
        Log.i(TAG, "testAddAndRemoveItem");
        UiObject2 addButton = mDevice.wait(Until.findObject(
                By.res(TODO_APP_PACKAGE, "btnAdd")), DEFAULT_TIMEOUT);
        assertTrue(addButton.isEnabled());
        UiObject2 addItemText = mDevice.wait(Until.findObject(
                By.res(TODO_APP_PACKAGE, "etAddItemText")), DEFAULT_TIMEOUT);
        String newItemText = sdf.format(Calendar.getInstance().getTime());
        Log.i(TAG, "adding " + newItemText);
        addItemText.setText(newItemText);
        addButton.click();
        Log.i(TAG, "removing " + newItemText);
        UiObject2 newItem = mDevice.wait(Until.findObject(By.text(newItemText)), DEFAULT_TIMEOUT);
        assertTrue(newItem.isEnabled());
        longClick(newItem);
    }

    // This is a workaround for item.longClick() which doesn't seem to work.
    private void longClick(UiObject2 uiObject2) {
        Log.i(TAG, "longClick on " + uiObject2.getText());
        Rect rect = uiObject2.getVisibleBounds();
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).swipe(
                rect.centerX(), rect.centerY(), rect.centerX(), rect.centerY(), 100);
    }
}
