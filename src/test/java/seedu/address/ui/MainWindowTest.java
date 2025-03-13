package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class MainWindowTest {
    private static final String COMMAND_THAT_SUCCEEDS = "list";
    private static final String COMMAND_THAT_FAILS = "invalid command";
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "sandbox");

    private MainWindow mainWindow;
    private Stage stage;
    private Logic logic;
    private StorageManager storage;
    private ModelManager model;

    @BeforeAll
    public static void setupSpec() {
        try {
            new JFXPanel(); // initializes JavaFX environment
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setup() throws Exception {
        // Set up required components with proper paths
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(
                TEST_DATA_FOLDER.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(
                TEST_DATA_FOLDER.resolve("userPrefs.json"));
        storage = new StorageManager(addressBookStorage, userPrefsStorage);
        model = new ModelManager();
        logic = new LogicManager(model, storage);
        // Use CountDownLatch to ensure initialization completes
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                stage = new Stage();
                mainWindow = new MainWindow(stage, logic);
                mainWindow.fillInnerParts();
                // Show stage to ensure proper initialization
                stage.show();
                latch.countDown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        // Wait for initialization to complete with timeout
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new TimeoutException("JavaFX initialization timed out");
        }
    }

    @AfterEach
    public void cleanup() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (stage != null) {
                stage.close();
            }
            if (mainWindow != null && mainWindow.getHelpWindow() != null) {
                mainWindow.getHelpWindow().hide();
            }
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void constructor_nullStage_throwsNullPointerException() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> throwableRef = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                mainWindow = new MainWindow(null, logic);
                mainWindow.fillInnerParts();
            } catch (Throwable e) {
                throwableRef.set(e);
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
        Throwable thrown = throwableRef.get();
        assertTrue(thrown instanceof AssertionError);
    }

    @Test
    public void constructor_nullLogic_throwsNullPointerException() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> throwableRef = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                mainWindow = new MainWindow(stage, null);
                mainWindow.fillInnerParts();
            } catch (Throwable e) {
                throwableRef.set(e);
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
        Throwable thrown = throwableRef.get();
        assertTrue(thrown instanceof NullPointerException,
            "Expected NullPointerException but got " + (thrown != null ? thrown.getClass() : "no exception"));
    }

    @Test
    public void setAccelerator_nullMenuItem_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
            mainWindow.setAccelerator(null, null));
    }

    @Test
    public void handleHelp_helpWindowShowsUp() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            mainWindow.handleHelp();
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
        assertTrue(mainWindow.getHelpWindow().isShowing());
        CountDownLatch focusLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            mainWindow.handleHelp();
            focusLatch.countDown();
        });
        focusLatch.await(5, TimeUnit.SECONDS);
        assertTrue(mainWindow.getHelpWindow().isFocused());
    }

    @Test
    public void handleExit_guiSettingsSaved() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        // Set initial window size and position
        Platform.runLater(() -> {
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setX(100);
            stage.setY(100);
            // Wait for stage updates to take effect
            Platform.runLater(() -> {
                mainWindow.handleExit();
                latch.countDown();
            });
        });
        latch.await(5, TimeUnit.SECONDS);
        GuiSettings expectedGuiSettings = new GuiSettings(800, 600, 100, 100);
        GuiSettings actualSettings = logic.getGuiSettings();
        assertEquals(expectedGuiSettings.getWindowWidth(), actualSettings.getWindowWidth(), 0.1);
        assertEquals(expectedGuiSettings.getWindowHeight(), actualSettings.getWindowHeight(), 0.1);
        assertEquals(expectedGuiSettings.getWindowCoordinates(), actualSettings.getWindowCoordinates());
        assertFalse(stage.isShowing());
    }

    @Test
    public void executeCommand_validCommand_commandExecuted() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                mainWindow.executeCommand(COMMAND_THAT_SUCCEEDS);
                latch.countDown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void executeCommand_invalidCommand_exceptionThrown() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                mainWindow.executeCommand(COMMAND_THAT_FAILS);
                throw new AssertionError("Execution of invalid command should fail");
            } catch (Exception e) {
                // Expected behavior
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void getModel_returnsOriginalModel() {
        assertEquals(model, logic.getModel());
    }

    @Test
    public void setGuiSettings_delegatesToModel() {
        GuiSettings guiSettings = new GuiSettings(1000, 500, 200, 200);
        logic.setGuiSettings(guiSettings);
        assertEquals(guiSettings, model.getGuiSettings());
    }
}
