# TestFX Setup Guide

This guide explains how to set up and run TestFX tests for the UI components of the application.

## Prerequisites

-   Java 17 or higher
-   Gradle 7.0 or higher

## Dependencies

The following dependencies have been added to the `build.gradle` file:

```gradle
testImplementation 'org.testfx:testfx-core:4.0.17'
testImplementation 'org.testfx:testfx-junit5:4.0.17'
testImplementation 'org.testfx:openjfx-monocle:jdk-12.0.1+2' // Headless testing
```

## Running TestFX Tests

### Running in Headless Mode (CI/CD)

To run TestFX tests in headless mode (without displaying UI), use the following command:

```bash
./gradlew test -Dheadless=true
```

This will use the Monocle headless driver to simulate UI interactions without displaying windows.

### Running with UI Visible (Local Development)

To run TestFX tests with the UI visible (for debugging), use:

```bash
./gradlew test
```

## VM Arguments for TestFX

When running TestFX tests, the following VM arguments are required:

```
--add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
--add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED
```

These are automatically set in the test configuration.

## Writing TestFX Tests

1. Extend the `TestFxBase` class which provides common setup and teardown methods:

```java
public class YourUiTest extends TestFxBase {
    // Your test methods
}
```

2. Use the `@Start` annotation to initialize your UI components:

```java
@Start
public void start(Stage stage) {
    // Initialize your UI components
}
```

3. Use `FxRobot` to interact with UI elements:

```java
@Test
public void yourTest(FxRobot robot) {
    robot.clickOn("#buttonId");
    robot.write("text");
    robot.press(KeyCode.ENTER).release(KeyCode.ENTER);

    // Assert results
    Node node = robot.lookup("#resultId").query();
    assertTrue(node.isVisible());
}
```

## Troubleshooting

### Test Fails with "Unable to load graphics driver"

This error occurs when running in headless mode without proper configuration. Make sure:

1. The Monocle dependency is correctly added to build.gradle
2. The VM arguments are correctly set
3. The headless property is set to true: `-Dheadless=true`

### Test Fails with "Cannot find UI element"

This usually means the ID of the UI element is incorrect or the element is not yet loaded. Try:

1. Adding `WaitForAsyncUtils.waitForFxEvents()` after UI operations
2. Using `robot.lookup("#id").queryAll()` to check if multiple elements match
3. Verifying the ID in your FXML files

## References

-   [TestFX GitHub Repository](https://github.com/TestFX/TestFX)
-   [TestFX Documentation](https://github.com/TestFX/TestFX/wiki)
-   [Monocle Headless Testing](https://github.com/TestFX/Monocle)
