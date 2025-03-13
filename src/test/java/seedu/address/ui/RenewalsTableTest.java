package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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
                VBox root = (VBox) stage.getScene().getRoot();
                root.getChildren().setAll(renewalsTable.getRoot());
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
                VBox root = (VBox) stage.getScene().getRoot();
                root.getChildren().clear();
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
        Person person1 = new PersonBuilder().withName("Alice")
                .withPolicy("12345", LocalDate.now().plusDays(60).format(Policy.DATE_FORMATTER)).build();
        Person person2 = new PersonBuilder().withName("Bob")
                .withPolicy("67890", LocalDate.now().plusDays(30).format(Policy.DATE_FORMATTER)).build();
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
        Person person = new PersonBuilder().withName("Charlie")
                .withPolicy("11111", LocalDate.now().plusDays(15).format(Policy.DATE_FORMATTER))
                .withPhone("12345678").build();

        RenewalsTable.RenewalEntry entry = new RenewalsTable.RenewalEntry(person);

        assertEquals("Charlie", entry.getClient());
        assertEquals("11111", entry.getPolicy());
        assertEquals(LocalDate.now().plusDays(15).format(Policy.DATE_FORMATTER), entry.getRenewalDate());
        assertEquals(15, entry.getDaysLeft());
        assertEquals("Life", entry.getType());
        assertEquals("12345678", entry.getContact());

        // Test person without policy
        Person personNoPolicy = new PersonBuilder().withName("David")
                .withPhone("87654321").build();
        RenewalsTable.RenewalEntry entryNoPolicy = new RenewalsTable.RenewalEntry(personNoPolicy);
        assertEquals("David", entryNoPolicy.getClient());
        assertEquals(null, entryNoPolicy.getPolicy());
        assertEquals(null, entryNoPolicy.getRenewalDate());
        assertEquals(0, entryNoPolicy.getDaysLeft());
        assertEquals("Life", entryNoPolicy.getType());
        assertEquals("87654321", entryNoPolicy.getContact());
    }

    @Test
    public void setupColumns_correctColumnFactories() throws InterruptedException {
        runOnFxThreadAndWait(() -> {
            Person person = new PersonBuilder().withName("David")
                    .withPolicy("22222", LocalDate.now().plusDays(5).format(Policy.DATE_FORMATTER))
                    .withPhone("87654321").build();
            model.addPerson(person);
            renewalsTable.updateRenewals(model);

            RenewalsTable.RenewalEntry entry = renewalsTable.getRenewalsTable().getItems().get(0);
            assertEquals("David", entry.getClient());
            assertEquals("22222", entry.getPolicy());
            assertEquals(LocalDate.now().plusDays(5).format(Policy.DATE_FORMATTER), entry.getRenewalDate());
            assertEquals(5, entry.getDaysLeft());
            assertEquals("Life", entry.getType());
            assertEquals("87654321", entry.getContact());
        });
    }

    @Test
    public void renewalDateColumn_formatting() throws InterruptedException {
        runOnFxThreadAndWait(() -> {
            // Test with null policy date
            Person personNoPolicy = new PersonBuilder().withName("Eve").build();
            model.addPerson(personNoPolicy);
            renewalsTable.updateRenewals(model);
            var items = renewalsTable.getRenewalsTable().getItems();
            assertEquals(1, items.size());
            assertEquals(null, items.get(0).getRenewalDate());
            // Clear and test with valid policy date
            model = new ModelManager(); // Create fresh model
            Person personWithPolicy = new PersonBuilder().withName("Eve")
                    .withPolicy("12345", LocalDate.now().plusDays(5).format(Policy.DATE_FORMATTER))
                    .build();
            model.addPerson(personWithPolicy);
            renewalsTable.updateRenewals(model);
            items = renewalsTable.getRenewalsTable().getItems();
            assertEquals(1, items.size());
            assertEquals(LocalDate.now().plusDays(5).format(Policy.DATE_FORMATTER),
                    items.get(0).getRenewalDate());
        });
    }

    @Test
    public void updateRenewals_sortingAndFiltering() throws InterruptedException {
        Person person1 = new PersonBuilder().withName("Frank")
                .withPolicy("33333", LocalDate.now().plusDays(10).format(Policy.DATE_FORMATTER)).build();
        Person person2 = new PersonBuilder().withName("George")
                .withPolicy("44444", LocalDate.now().plusDays(5).format(Policy.DATE_FORMATTER)).build();
        Person person3 = new PersonBuilder().withName("Henry")
                .withPolicy("55555", LocalDate.now().plusDays(15).format(Policy.DATE_FORMATTER)).build();
        model.addPerson(person1);
        model.addPerson(person2);
        model.addPerson(person3);

        runOnFxThreadAndWait(() -> renewalsTable.updateRenewals(model));

        var items = renewalsTable.getRenewalsTable().getItems();
        assertEquals(3, items.size());
        // Verify sorting by days left
        assertEquals("George", items.get(0).getClient()); // 5 days
        assertEquals("Frank", items.get(1).getClient()); // 10 days
        assertEquals("Henry", items.get(2).getClient()); // 15 days
    }

    @Test
    public void columnFormatting_correctFormatting() throws InterruptedException {
        Person person = new PersonBuilder().withName("Alice")
                .withPolicy("12345", LocalDate.now().plusDays(15).format(Policy.DATE_FORMATTER))
                .withPhone("98765432").build();
        model.addPerson(person);

        runOnFxThreadAndWait(() -> {
            renewalsTable.updateRenewals(model);
            var items = renewalsTable.getRenewalsTable().getItems();
            assertEquals(1, items.size());

            // Check client column
            assertEquals("Alice", items.get(0).getClient());

            // Check policy column
            assertEquals("12345", items.get(0).getPolicy());

            // Check renewal date column
            assertEquals(LocalDate.now().plusDays(15).format(Policy.DATE_FORMATTER),
                    items.get(0).getRenewalDate().format(Policy.DATE_FORMATTER));

            // Check days left column
            assertEquals(15, items.get(0).getDaysLeft());

            // Check type column
            assertEquals("Life", items.get(0).getType());

            // Check contact column
            assertEquals("98765432", items.get(0).getContact());
        });
    }

    @Test
    public void renewalDates_edgeCases() throws InterruptedException {
        // Test past renewal date
        Person pastRenewal = new PersonBuilder().withName("Past")
                .withPolicy("11111", LocalDate.now().minusDays(1).format(Policy.DATE_FORMATTER))
                .build();
        // Test today's renewal
        Person todayRenewal = new PersonBuilder().withName("Today")
                .withPolicy("22222", LocalDate.now().format(Policy.DATE_FORMATTER))
                .build();
        // Test far future renewal
        Person futureRenewal = new PersonBuilder().withName("Future")
                .withPolicy("33333", LocalDate.now().plusYears(2).format(Policy.DATE_FORMATTER))
                .build();

        model.addPerson(pastRenewal);
        model.addPerson(todayRenewal);
        model.addPerson(futureRenewal);

        runOnFxThreadAndWait(() -> {
            renewalsTable.updateRenewals(model);
            var items = renewalsTable.getRenewalsTable().getItems();
            assertEquals(3, items.size());

            // Check sorting order (by days left)
            assertEquals("Past", items.get(0).getClient()); // -1 days
            assertEquals("Today", items.get(1).getClient()); // 0 days
            assertEquals("Future", items.get(2).getClient()); // 730 days (approx)

            // Verify days left calculations
            assertTrue(items.get(0).getDaysLeft() < 0); // Past
            assertEquals(0, items.get(1).getDaysLeft()); // Today
            assertTrue(items.get(2).getDaysLeft() > 365); // Future
        });
    }

    @Test
    public void nullHandling_optionalFields() throws InterruptedException {
        // Create a person with minimal data
        Person minimalPerson = new PersonBuilder().withName("Minimal")
                .build(); // No policy, phone, etc.

        model.addPerson(minimalPerson);

        runOnFxThreadAndWait(() -> {
            renewalsTable.updateRenewals(model);
            var items = renewalsTable.getRenewalsTable().getItems();
            assertEquals(1, items.size());

            RenewalsTable.RenewalEntry entry = items.get(0);
            assertEquals("Minimal", entry.getClient());
            assertNull(entry.getPolicy());
            assertNull(entry.getRenewalDate());
            assertEquals(0, entry.getDaysLeft());
            assertEquals("Life", entry.getType());
            assertNull(entry.getContact());
        });
    }

    @Test
    public void tableSorting_maintainsOrder() throws InterruptedException {
        // Add persons in random order
        Person person1 = new PersonBuilder().withName("Charlie")
                .withPolicy("33333", LocalDate.now().plusDays(30).format(Policy.DATE_FORMATTER))
                .build();
        Person person2 = new PersonBuilder().withName("Alice")
                .withPolicy("11111", LocalDate.now().plusDays(10).format(Policy.DATE_FORMATTER))
                .build();
        Person person3 = new PersonBuilder().withName("Bob")
                .withPolicy("22222", LocalDate.now().plusDays(20).format(Policy.DATE_FORMATTER))
                .build();

        model.addPerson(person1);
        model.addPerson(person2);
        model.addPerson(person3);

        runOnFxThreadAndWait(() -> {
            renewalsTable.updateRenewals(model);
            var items = renewalsTable.getRenewalsTable().getItems();
            assertEquals(3, items.size());

            // Verify sorting by days left
            assertEquals("Alice", items.get(0).getClient()); // 10 days
            assertEquals("Bob", items.get(1).getClient()); // 20 days
            assertEquals("Charlie", items.get(2).getClient()); // 30 days

            // Verify days left values
            assertEquals(10, items.get(0).getDaysLeft());
            assertEquals(20, items.get(1).getDaysLeft());
            assertEquals(30, items.get(2).getDaysLeft());
        });
    }
}
