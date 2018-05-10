# Performance Tests

## Overview

This module contains performance related tests that test timings and jank.

## Use

### Benchmark Tests

These tests time specific use cases and export the results to a file. To run the benchmark tests 
you must add the following values to this module's `gradle.properties` files:
* benchmarkAppId
* benchmarkEmail
* benchmarkPassword
* benchmarkIdentityProviderUrl

### Performance Tests

These tests measure dropped frames using `dumpsys`. These tests can be run like any test but must 
be run on a device to receive meaningful output.

The first time the app is installed you must manually grant permission to dump the information via
```commandline
adb shell pm grant com.layer.xdk.test.performance android.permission.DUMP
```

This command should be performed after the app is installed but before it is run. 
Since it is not dependent upon the test app (which is uninstalled after completion), you 
can run this command after the first run of the test. This will ensure subsequent runs output the 
correct data.

This must manually be ran because we are not using `monkeyrunner` to run these tests and it's not 
possible to request this permission from the app.

## Output

The results are written to the device's external storage directory. They can be 
accessed in `/sdcard/layer-test-data`.