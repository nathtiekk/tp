package seedu.address.ui;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of upcoming policy renewals.
 */
public class RenewalsTable extends UiPart<VBox> {

    private static final String FXML = "RenewalsTable.fxml";

    @FXML
    private TableView<RenewalEntry> renewalsTable;

    @FXML
    private TableColumn<RenewalEntry, String> clientColumn;

    @FXML
    private TableColumn<RenewalEntry, String> policyColumn;

    @FXML
    private TableColumn<RenewalEntry, LocalDate> renewalDateColumn;

    @FXML
    private TableColumn<RenewalEntry, Long> daysLeftColumn;

    @FXML
    private TableColumn<RenewalEntry, String> typeColumn;

    @FXML
    private TableColumn<RenewalEntry, String> contactColumn;

    /**
     * Creates a new RenewalsTable with the given Model.
     *
     * @param model The model containing the person data
     */
    public RenewalsTable(Model model) {
        super(FXML);
        setupColumns();
        updateRenewals(model);
    }

    /**
     * Sets up the table columns with their respective cell value factories.
     */
    private void setupColumns() {
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("client"));
        policyColumn.setCellValueFactory(new PropertyValueFactory<>("policy"));
        renewalDateColumn.setCellValueFactory(new PropertyValueFactory<>("renewalDate"));
        daysLeftColumn.setCellValueFactory(new PropertyValueFactory<>("daysLeft"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));

        // Format renewal date
        renewalDateColumn.setCellFactory(column -> new TableCell<RenewalEntry, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(seedu.address.model.person.Policy.DATE_FORMATTER));
                }
            }
        });
    }

    /**
     * Updates the table with renewal entries from the model.
     *
     * @param model The model containing the updated person data
     */
    public void updateRenewals(Model model) {
        List<RenewalEntry> renewals = model.getFilteredPersonList().stream()
                .map(RenewalEntry::new)
                .sorted(Comparator.comparing(RenewalEntry::getDaysLeft))
                .collect(Collectors.toList());

        renewalsTable.setItems(FXCollections.observableArrayList(renewals));
    }

    /**
     * Shows the renewals table.
     */
    public void show() {
        getRoot().setVisible(true);
        getRoot().setManaged(true);
    }

    /**
     * Hides the renewals table.
     */
    public void hide() {
        getRoot().setVisible(false);
        getRoot().setManaged(false);
    }

    /**
     * Gets the renewals table for testing.
     *
     * @return The TableView containing the renewal entries
     */
    protected TableView<RenewalEntry> getRenewalsTable() {
        return renewalsTable;
    }

    /**
     * Represents a row in the renewals table.
     */
    public static class RenewalEntry {
        private final String client;
        private final String policy;
        private final LocalDate renewalDate;
        private final long daysLeft;
        private final String type;
        private final String contact;

        /**
         * Creates a new RenewalEntry from a Person.
         *
         * @param person The person whose data will be displayed in this entry
         */
        public RenewalEntry(Person person) {
            this.client = person.getName().toString();
            this.policy = person.getPolicy().policyNumber;
            this.renewalDate = person.getPolicy().renewalDate;
            this.daysLeft = person.getPolicy().getDaysUntilRenewal();
            // TODO: Add policy type to Policy class
            this.type = "Life"; // placeholder
            this.contact = person.getPhone().toString();
        }

        public String getClient() {
            return client;
        }

        public String getPolicy() {
            return policy;
        }

        public LocalDate getRenewalDate() {
            return renewalDate;
        }

        public long getDaysLeft() {
            return daysLeft;
        }

        public String getType() {
            return type;
        }

        public String getContact() {
            return contact;
        }
    }
}
