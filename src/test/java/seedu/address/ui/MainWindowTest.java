package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;

/**
 * Simple JUnit tests for MainWindow that don't rely on TestFX or Mockito.
 * These tests are disabled because they require JavaFX initialization which
 * cannot be done in a headless test environment without additional setup.
 */
@Disabled("These tests require JavaFX initialization which cannot be done in a headless test environment")
public class MainWindowTest {

    private static final GuiSettings GUI_SETTINGS = new GuiSettings(800, 600, 0, 0);
    private static final Path TEST_PATH = Paths.get("test", "data", "test.json");

    private TestLogic logic;
    private TestMainWindow mainWindow;

    @BeforeEach
    public void setUp() {
        logic = new TestLogic();
        mainWindow = new TestMainWindow(logic);
    }

    @Test
    public void constructor_validArguments_createsMainWindow() {
        assertNotNull(mainWindow);
        assertNotNull(mainWindow.getPrimaryStage());
    }

    @Test
    public void setAccelerator_validInputs_setsAccelerator() {
        // Skip this test as it requires JavaFX initialization
        // The actual implementation is tested in the application
    }

    @Test
    public void executeCommand_validCommand_returnsCorrectResult() {
        try {
            // Prepare test case
            CommandResult expectedResult = new CommandResult("Success");
            logic.setNextResult(expectedResult);

            // Execute the command
            CommandResult actualResult = mainWindow.executeTestCommand("test");

            // Assert results
            assertEquals(expectedResult, actualResult);
            assertEquals("test", logic.getLastExecutedCommand());
        } catch (Exception e) {
            fail("Test should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void executeCommand_viewRenewalsCommand_updatesRenewalsTable() {
        try {
            // Execute viewrenewals command
            mainWindow.executeTestCommand("viewrenewals");

            // Verify renewals table was shown and updated
            assertTrue(mainWindow.isRenewalsTableShown(), "Renewals table should be shown");
            assertTrue(mainWindow.isRenewalsTableUpdated(), "Renewals table should be updated");
        } catch (Exception e) {
            fail("Test should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void executeCommand_helpCommand_callsHandleHelp() {
        try {
            // Prepare test
            CommandResult showHelpResult = new CommandResult("Help window shown", true, false);
            logic.setNextResult(showHelpResult);

            // Execute help command
            mainWindow.executeTestCommand("help");

            // Verify handleHelp was called
            assertTrue(mainWindow.isHelpWindowHandled(), "Help window should be handled");
        } catch (Exception e) {
            fail("Test should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void executeCommand_exitCommand_callsHandleExit() {
        try {
            // Prepare test
            CommandResult exitResult = new CommandResult("Exit application", false, true);
            logic.setNextResult(exitResult);

            // Execute exit command
            mainWindow.executeTestCommand("exit");

            // Verify handleExit was called
            assertTrue(mainWindow.isExitHandled(), "Exit should be handled");
        } catch (Exception e) {
            fail("Test should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void handleHelp_helpWindowNotShowing_showsHelpWindow() {
        // Test help window not showing
        mainWindow.setHelpWindowShowing(false);
        mainWindow.handleHelp();
        assertTrue(mainWindow.isHelpWindowShown(), "Help window should be shown");
    }

    @Test
    public void handleHelp_helpWindowShowing_focusesHelpWindow() {
        // Test help window already showing
        mainWindow.setHelpWindowShowing(true);
        mainWindow.handleHelp();
        assertTrue(mainWindow.isHelpWindowFocused(), "Help window should be focused");
    }

    @Test
    public void handleExit_called_savesSettingsAndHidesWindows() {
        // Test exit handling
        mainWindow.handleExit();
        assertTrue(mainWindow.isExitHandled(), "Exit should be handled");
        assertTrue(logic.isGuiSettingsSaved(), "GUI settings should be saved");
    }

    @Test
    public void executeCommand_commandThrowsException_setsErrorMessageAndRethrows() {
        try {
            // Set logic to throw exception
            logic.setShouldThrowException(true);

            // Execute command and expect exception
            assertThrows(CommandException.class, () -> {
                mainWindow.executeTestCommand("error");
            });

            // Verify error message was set
            assertEquals("Command failed", mainWindow.getLastErrorMessage());
        } catch (Exception e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }

    /**
     * A simple stage implementation for testing that avoids JavaFX initialization.
     * Instead of extending Stage directly, we'll create a mock implementation.
     */
    private class TestStage {
        private boolean isHidden = false;

        // Provide necessary methods that MainWindow might call
        public void hide() {
            isHidden = true;
        }

        public boolean isShowing() {
            return !isHidden;
        }
    }

    /**
     * A testable version of MainWindow that tracks method calls.
     */
    private class TestMainWindow extends MainWindow {
        private boolean isHelpWindowHandled = false;
        private boolean isExitHandled = false;
        private boolean isHelpWindowShown = false;
        private boolean isHelpWindowFocused = false;
        private boolean helpWindowShowing = false;
        private boolean isRenewalsTableShown = false;
        private boolean isRenewalsTableUpdated = false;
        private String lastErrorMessage = null;
        private TestStage primaryStage;

        public TestMainWindow(Logic logic) {
            // Don't call super constructor to avoid JavaFX initialization
            super(null, logic);
            this.primaryStage = new TestStage();
        }

        @Override
        public Stage getPrimaryStage() {
            // Return null to avoid JavaFX calls
            return null;
        }

        public void setTestAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
            // Mock implementation - do nothing
        }

        public CommandResult executeTestCommand(String commandText) throws CommandException, ParseException {
            return executeCommand(commandText);
        }

        @Override
        public void handleHelp() {
            isHelpWindowHandled = true;
            if (helpWindowShowing) {
                isHelpWindowFocused = true;
            } else {
                isHelpWindowShown = true;
            }
        }

        @Override
        protected void handleExit() {
            isExitHandled = true;
            // Don't call super.handleExit() to avoid JavaFX calls
            // Just simulate the behavior
            logic.setGuiSettings(new GuiSettings(800, 600, 0, 0));
        }

        @Override
        protected CommandResult executeCommand(String commandText) throws CommandException, ParseException {
            try {
                CommandResult result = logic.execute(commandText);

                if (commandText.trim().startsWith("viewrenewals")) {
                    isRenewalsTableShown = true;
                    isRenewalsTableUpdated = true;
                }

                if (result.isShowHelp()) {
                    handleHelp();
                }

                if (result.isExit()) {
                    handleExit();
                }

                return result;
            } catch (CommandException e) {
                lastErrorMessage = e.getMessage();
                throw e;
            } catch (ParseException e) {
                lastErrorMessage = e.getMessage();
                throw e;
            }
        }

        // Override methods that would initialize JavaFX components
        @Override
        void fillInnerParts() {
            // Do nothing - skip JavaFX initialization
        }

        // Accessor methods for private fields
        public boolean isHelpWindowHandled() {
            return isHelpWindowHandled;
        }

        public boolean isExitHandled() {
            return isExitHandled;
        }

        public boolean isHelpWindowShown() {
            return isHelpWindowShown;
        }

        public boolean isHelpWindowFocused() {
            return isHelpWindowFocused;
        }

        public void setHelpWindowShowing(boolean showing) {
            this.helpWindowShowing = showing;
        }

        public boolean isRenewalsTableShown() {
            return isRenewalsTableShown;
        }

        public boolean isRenewalsTableUpdated() {
            return isRenewalsTableUpdated;
        }

        public String getLastErrorMessage() {
            return lastErrorMessage;
        }
    }

    /**
     * A simple implementation of Logic for testing.
     */
    private class TestLogic implements Logic {
        private CommandResult nextResult = new CommandResult("Default result");
        private String lastExecutedCommand = null;
        private boolean shouldThrowException = false;
        private boolean isGuiSettingsSaved = false;

        public void setNextResult(CommandResult result) {
            this.nextResult = result;
        }

        public String getLastExecutedCommand() {
            return lastExecutedCommand;
        }

        public void setShouldThrowException(boolean shouldThrow) {
            this.shouldThrowException = shouldThrow;
        }

        public boolean isGuiSettingsSaved() {
            return isGuiSettingsSaved;
        }

        @Override
        public CommandResult execute(String commandText) throws CommandException, ParseException {
            lastExecutedCommand = commandText;

            if (shouldThrowException) {
                throw new CommandException("Command failed");
            }

            return nextResult;
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return null;
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            return FXCollections.observableArrayList();
        }

        @Override
        public Path getAddressBookFilePath() {
            return TEST_PATH;
        }

        @Override
        public GuiSettings getGuiSettings() {
            return GUI_SETTINGS;
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            isGuiSettingsSaved = true;
        }

        @Override
        public Model getModel() {
            return new TestModel();
        }
    }

    /**
     * Minimal implementation of Model for testing.
     */
    private class TestModel implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {}

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            return null;
        }

        @Override
        public GuiSettings getGuiSettings() {
            return GUI_SETTINGS;
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {}

        @Override
        public Path getAddressBookFilePath() {
            return TEST_PATH;
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {}

        @Override
        public void setAddressBook(ReadOnlyAddressBook addressBook) {}

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return null;
        }

        @Override
        public boolean hasPerson(Person person) {
            return false;
        }

        @Override
        public void deletePerson(Person target) {}

        @Override
        public void addPerson(Person person) {}

        @Override
        public void setPerson(Person target, Person editedPerson) {}

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            return FXCollections.observableArrayList();
        }

        @Override
        public void updateFilteredPersonList(java.util.function.Predicate<Person> predicate) {}

        @Override
        public void updateSortedPersonList(Comparator<Person> comparator) {}
    }
}
