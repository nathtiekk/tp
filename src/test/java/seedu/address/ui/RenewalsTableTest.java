package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import javafx.scene.control.TableView;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.person.Policy;
import seedu.address.testutil.PersonBuilder;
import seedu.address.ui.RenewalsTable.RenewalEntry;

public class RenewalsTableTest extends UiPartTest {
    private static final String FXML = "RenewalsTable.fxml";
    private Model model = new ModelManager();

    @Test
    public void constructor_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RenewalsTable(null));
    }

    @Test
    public void updateRenewals_emptyModel_emptyTable() {
        RenewalsTable renewalsTable = new RenewalsTable(model);
        renewalsTable.updateRenewals(model);
        assertEquals(0, renewalsTable.getRenewalsTable().getItems().size());
    }

    @Test
    public void updateRenewals_withPerson_correctEntry() {
        Person person = new PersonBuilder().build();
        model.addPerson(person);

        RenewalsTable renewalsTable = new RenewalsTable(model);
        renewalsTable.updateRenewals(model);

        assertEquals(1, renewalsTable.getRenewalsTable().getItems().size());
        RenewalEntry entry = renewalsTable.getRenewalsTable().getItems().get(0);
        assertEquals(person.getName().toString(), entry.getClient());
        assertEquals(person.getPolicy().policyNumber, entry.getPolicy());
        assertEquals(person.getPolicy().renewalDate, entry.getRenewalDate());
        assertEquals(person.getPolicy().getDaysUntilRenewal(), entry.getDaysLeft());
        assertEquals(person.getPhone().toString(), entry.getContact());
    }

    @Test
    public void updateRenewals_multiplePersons_sortedByDaysLeft() {
        Person person1 = new PersonBuilder()
                .withName("Alice")
                .withPolicy("123456", LocalDate.now().plusDays(10).format(Policy.DATE_FORMATTER))
                .build();
        Person person2 = new PersonBuilder()
                .withName("Bob")
                .withPolicy("234567", LocalDate.now().plusDays(5).format(Policy.DATE_FORMATTER))
                .build();
        model.addPerson(person1);
        model.addPerson(person2);
        RenewalsTable renewalsTable = new RenewalsTable(model);
        renewalsTable.updateRenewals(model);

        assertEquals(2, renewalsTable.getRenewalsTable().getItems().size());
        assertEquals("Bob", renewalsTable.getRenewalsTable().getItems().get(0).getClient());
        assertEquals("Alice", renewalsTable.getRenewalsTable().getItems().get(1).getClient());
    }

    @Test
    public void updateRenewals_modelChanged_tableUpdates() {
        RenewalsTable renewalsTable = new RenewalsTable(model);
        Person person = new PersonBuilder().build();
        model.addPerson(person);
        renewalsTable.updateRenewals(model);
        assertEquals(1, renewalsTable.getRenewalsTable().getItems().size());

        model.deletePerson(person);
        renewalsTable.updateRenewals(model);
        assertEquals(0, renewalsTable.getRenewalsTable().getItems().size());
    }

    @Test
    public void updateRenewals_pastRenewalDate_includedInTable() {
        Person person = new PersonBuilder()
                .withPolicy("123456", LocalDate.now().minusDays(5).format(Policy.DATE_FORMATTER))
                .build();
        model.addPerson(person);

        RenewalsTable renewalsTable = new RenewalsTable(model);
        renewalsTable.updateRenewals(model);

        assertEquals(1, renewalsTable.getRenewalsTable().getItems().size());
        RenewalEntry entry = renewalsTable.getRenewalsTable().getItems().get(0);
        assertEquals(-5, entry.getDaysLeft());
    }

    @Test
    public void columnFormatting_correctFormat() {
        Person person = new PersonBuilder().build();
        model.addPerson(person);

        RenewalsTable renewalsTable = new RenewalsTable(model);
        renewalsTable.updateRenewals(model);

        TableView<RenewalEntry> table = renewalsTable.getRenewalsTable();
        assertEquals("Client", table.getColumns().get(0).getText());
        assertEquals("Policy Number", table.getColumns().get(1).getText());
        assertEquals("Renewal Date", table.getColumns().get(2).getText());
        assertEquals("Days Left", table.getColumns().get(3).getText());
        assertEquals("Type", table.getColumns().get(4).getText());
        assertEquals("Contact", table.getColumns().get(5).getText());
    }
}
