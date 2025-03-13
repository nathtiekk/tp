package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Policy;
import seedu.address.model.tag.Tag;
import seedu.address.ui.RenewalsTable.RenewalEntry;

/**
 * Tests for the RenewalsTable class using only JUnit.
 * These tests are disabled because they require JavaFX initialization which
 * cannot be done in a headless test environment without additional setup.
 */
@Disabled("These tests require JavaFX initialization which cannot be done in a headless test environment")
public class RenewalsTableTest {

    private TestModel model;
    private TestRenewalsTable renewalsTable;

    @BeforeEach
    public void setUp() {
        // Create test persons with different renewal dates
        List<Person> testPersons = createTestPersons();

        // Set up model with test persons
        model = new TestModel(testPersons);

        // Create the renewals table with our test model
        renewalsTable = new TestRenewalsTable(model);
    }

    @Test
    public void constructor_validModel_createsRenewalsTable() {
        assertNotNull(renewalsTable);
        assertNotNull(renewalsTable.getRenewalsTable());
    }

    @Test
    public void updateRenewals_withValidModel_populatesTable() {
        // Table should have been populated during construction
        TableView<RenewalEntry> table = renewalsTable.getRenewalsTable();

        // Check if the table has the correct number of entries
        assertEquals(3, table.getItems().size());

        // Check if entries are sorted by days left (ascending)
        List<RenewalEntry> entries = table.getItems();
        assertTrue(entries.get(0).getDaysLeft() <= entries.get(1).getDaysLeft());
        assertTrue(entries.get(1).getDaysLeft() <= entries.get(2).getDaysLeft());

        // Verify content of first entry
        RenewalEntry firstEntry = entries.get(0);
        assertEquals("Alice Pauline", firstEntry.getClient());
        assertEquals("P-001", firstEntry.getPolicy());
        assertEquals("98765432", firstEntry.getContact());
    }

    @Test
    public void show_makeVisibleTrue() {
        // Initial state should be hidden
        renewalsTable.getRoot().setVisible(false);
        renewalsTable.getRoot().setManaged(false);

        // Call show
        renewalsTable.show();

        // Verify that the table is now visible
        assertTrue(renewalsTable.getRoot().isVisible());
        assertTrue(renewalsTable.getRoot().isManaged());
    }

    @Test
    public void hide_makeVisibleFalse() {
        // Initial state should be visible
        renewalsTable.getRoot().setVisible(true);
        renewalsTable.getRoot().setManaged(true);

        // Call hide
        renewalsTable.hide();

        // Verify that the table is now hidden
        assertFalse(renewalsTable.getRoot().isVisible());
        assertFalse(renewalsTable.getRoot().isManaged());
    }

    @Test
    public void renewalEntry_fromPerson_extractsCorrectData() {
        // Create a test person
        Person person = createTestPersons().get(0);

        // Create a renewal entry from the person
        RenewalEntry entry = new RenewalEntry(person);

        // Verify that the entry has the correct data
        assertEquals(person.getName().toString(), entry.getClient());
        assertEquals(person.getPolicy().policyNumber, entry.getPolicy());
        assertEquals(person.getPolicy().renewalDate, entry.getRenewalDate());
        assertEquals(person.getPolicy().getDaysUntilRenewal(), entry.getDaysLeft());
        assertEquals("Life", entry.getType()); // Currently hardcoded in RenewalEntry
        assertEquals(person.getPhone().toString(), entry.getContact());
    }

    /**
     * Creates a list of test persons with different renewal dates.
     */
    private List<Person> createTestPersons() {
        // Create some test dates relative to current date
        LocalDate today = LocalDate.now();
        LocalDate nearFuture = today.plusDays(30);
        LocalDate farFuture = today.plusDays(90);
        LocalDate veryFarFuture = today.plusDays(180);

        // Format dates for Policy constructor
        String nearFutureStr = nearFuture.format(Policy.DATE_FORMATTER);
        String farFutureStr = farFuture.format(Policy.DATE_FORMATTER);
        String veryFarFutureStr = veryFarFuture.format(Policy.DATE_FORMATTER);

        // Create test persons with different renewal dates
        Person alice = new Person(
                new Name("Alice Pauline"),
                new Phone("98765432"),
                new Email("alice@example.com"),
                new Address("123, Jurong West Ave 6, #08-111"),
                new Policy("P-001", nearFutureStr),
                new HashSet<>(Arrays.asList(new Tag("friends"))));

        Person bob = new Person(
                new Name("Bob Choo"),
                new Phone("87654321"),
                new Email("bob@example.com"),
                new Address("Block 123, Bobby Street 3"),
                new Policy("P-002", farFutureStr),
                new HashSet<>(Arrays.asList(new Tag("friends"), new Tag("colleagues"))));

        Person charlie = new Person(
                new Name("Charlie Brown"),
                new Phone("98765433"),
                new Email("charlie@example.com"),
                new Address("111, Lorong 1 Toa Payoh, #01-111"),
                new Policy("P-003", veryFarFutureStr),
                new HashSet<>(Arrays.asList(new Tag("family"))));

        return Arrays.asList(alice, bob, charlie);
    }

    /**
     * A testable version of RenewalsTable that allows access to protected members.
     */
    private class TestRenewalsTable extends RenewalsTable {

        public TestRenewalsTable(Model model) {
            super(model);
        }

        @Override
        protected TableView<RenewalEntry> getRenewalsTable() {
            return super.getRenewalsTable();
        }
    }

    /**
     * An implementation of Model for testing.
     */
    private class TestModel implements Model {
        private final ObservableList<Person> persons;

        public TestModel(List<Person> persons) {
            this.persons = FXCollections.observableArrayList(persons);
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            return persons;
        }

        @Override
        public void updateSortedPersonList(Comparator<Person> comparator) {
            // Not needed here
        }

        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {}

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            return null;
        }

        @Override
        public void setGuiSettings(seedu.address.commons.core.GuiSettings guiSettings) {}

        @Override
        public seedu.address.commons.core.GuiSettings getGuiSettings() {
            return null;
        }

        @Override
        public void setAddressBookFilePath(java.nio.file.Path addressBookFilePath) {}

        @Override
        public java.nio.file.Path getAddressBookFilePath() {
            return null;
        }

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
        public void updateFilteredPersonList(java.util.function.Predicate<Person> predicate) {}
    }
}
