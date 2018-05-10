package com.layer.xdk.test.performance.benchmark;

import com.layer.xdk.test.performance.PerfTestingUtils;
import com.layer.xdk.ui.util.Log;

import junit.framework.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BenchmarkTestUtils {
    private static final String BENCHMARK_RESULTS_FILE = "benchmarkResults.txt";

    public static void appendBenchmarkResult(String description, long durationMillis) {
        File file = getFile();
        String formattedDuration = String.format(Locale.getDefault(), "%s: %d.%d\n",
                description,
                TimeUnit.MILLISECONDS.toSeconds(durationMillis),
                durationMillis % 1000);

        try {
            Files.write(file.toPath(),
                    formattedDuration.getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            Log.e("Failed to write benchmark results", e);
            Assert.fail("Failed to write benchmark results due to " + e.getCause() + " " + e.getMessage());
        }
    }

    public static void deleteBenchmarkResultFile() {
        File file = getFile();
        if (file.exists() && !file.delete()) {
            Log.w("Failed to delete benchmark results file");
        }
    }

    private static File getFile() {
        return PerfTestingUtils.getTestFile(BenchmarkTests.class.getName(),
                "all", BENCHMARK_RESULTS_FILE);
    }
}