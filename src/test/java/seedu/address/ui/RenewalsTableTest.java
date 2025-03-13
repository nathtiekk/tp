package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.person.Policy;
import seedu.address.testutil.PersonBuilder;

public class RenewalsTableTest {
    private static final int TIMEOUT_MS = 2000;
    private static Stage stage;
    private Model model;
    private RenewalsTable renewalsTable;

    @BeforeAll
    public static void setupSpec() throws InterruptedException {
        // Initialize JavaFX Toolkit
        try {
            System.setProperty("java.awt.headless", "true");
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            new JFXPanel();
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                stage = new Stage();
                stage.setScene(new Scene(new VBox()));
                latch.countDown();
            });
            if (!latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("JavaFX initialization timed out");
            }
        } catch (Exception e) {
            // If toolkit is already initialized, ignore the error
            if (!e.getMessage().contains("Toolkit already initialized")) {
                throw e;
            }
        }
    }

    @BeforeEach
    public void setUp() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                model = new ModelManager();
                renewalsTable = new RenewalsTable(model);
                ((VBox) stage.getScene().getRoot()).getChildren().setAll(renewalsTable.getRoot());
            } finally {
                latch.countDown();
            }
        });
        if (!latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
            throw new RuntimeException("Setup timed out");
        }
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                ((VBox) stage.getScene().getRoot()).getChildren().clear();
            } finally {
                latch.countDown();
            }
        });
        if (!latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
            throw new RuntimeException("Teardown timed out");
        }
    }

    private void runOnFxThreadAndWait(Runnable action) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        if (!latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
            throw new RuntimeException("JavaFX operation timed out");
        }
        // Add a small delay to ensure UI updates are processed
        Thread.sleep(100);
    }

    @Test
    public void updateRenewals_withPeople_populatesTable() throws InterruptedException {
        Person person1 = new PersonBuilder().withName("Alice").
                withPolicy("12345", LocalDate.now().plusDays(60).format(Policy.DATE_FORMATTER)).build();
        Person person2 = new PersonBuilder().withName("Bob").
                withPolicy("67890", LocalDate.now().plusDays(30).format(Policy.DATE_FORMATTER)).build();
        model.addPerson(person1);
        model.addPerson(person2);

        runOnFxThreadAndWait(() -> renewalsTable.updateRenewals(model));

        assertEquals(2, renewalsTable.getRenewalsTable().getItems().size());
        RenewalsTable.RenewalEntry entry1 = renewalsTable.getRenewalsTable().getItems().get(0);
        assertEquals("Bob", entry1.getClient());
        assertEquals("67890", entry1.getPolicy());
        assertEquals(30, entry1.getDaysLeft());

        RenewalsTable.RenewalEntry entry2 = renewalsTable.getRenewalsTable().getItems().get(1);
        assertEquals("Alice", entry2.getClient());
        assertEquals("12345", entry2.getPolicy());
        assertEquals(60, entry2.getDaysLeft());
    }

    @Test
    public void constructor_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RenewalsTable(null));
    }

    @Test
    public void updateRenewals_emptyModel_emptyTable() throws InterruptedException {
        runOnFxThreadAndWait(() -> renewalsTable.updateRenewals(model));
        assertEquals(0, renewalsTable.getRenewalsTable().getItems().size());
    }

    @Test
    public void visibility_showHide_correctVisibility() throws InterruptedException {
        runOnFxThreadAndWait(() -> {
            assertTrue(renewalsTable.getRoot().isVisible());
            assertTrue(renewalsTable.getRoot().isManaged());

            renewalsTable.hide();
            assertFalse(renewalsTable.getRoot().isVisible());
            assertFalse(renewalsTable.getRoot().isManaged());

            renewalsTable.show();
            assertTrue(renewalsTable.getRoot().isVisible());
            assertTrue(renewalsTable.getRoot().isManaged());
        });
    }

    @Test
    public void renewalEntry_creation_correctValues() {
        Person person = new PersonBuilder().withName("Charlie").
                withPolicy("11111", LocalDate.now().plusDays(15).format(Policy.DATE_FORMATTER)).
                withPhone("12345678").build();

        RenewalsTable.RenewalEntry entry = new RenewalsTable.RenewalEntry(person);

        assertEquals("Charlie", entry.getClient());
        assertEquals("11111", entry.getPolicy());
        assertEquals(15, entry.getDaysLeft());
        assertEquals("Life", entry.getType());
        assertEquals("12345678", entry.getContact());
    }
}
