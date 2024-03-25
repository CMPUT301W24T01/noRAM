package com.example.benchmark;

import androidx.benchmark.macro.CompilationMode;
import androidx.benchmark.macro.StartupMode;
import androidx.benchmark.macro.StartupTimingMetric;
import androidx.benchmark.macro.junit4.MacrobenchmarkRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

/**
 * This is an example startup benchmark.
 * <p>
 * It navigates to the device's home screen, and launches the default activity.
 * <p>
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 * <p>
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@RunWith(AndroidJUnit4.class)
public class ExampleStartupBenchmark {

    @Rule
    public MacrobenchmarkRule mBenchmarkRule = new MacrobenchmarkRule();

    @Test
    public void startup() {
        mBenchmarkRule.measureRepeated(
                "com.example.noram",
                Collections.singletonList(new StartupTimingMetric()),
                CompilationMode.DEFAULT,
                StartupMode.COLD,
                5,
                scope -> {
                    scope.pressHome();
                    scope.startActivityAndWait();
                    return null;
                });
    }
}