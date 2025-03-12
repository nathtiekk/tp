package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEXT_N_DAYS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SORT_ORDER;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Lists all persons whose policies are due for renewal within the specified number of days.
 */
public class ViewRenewalsCommand extends Command {

    public static final String COMMAND_WORD = "viewrenewals";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows all policies due for renewal within specified days. "
            + "Parameters: [n/NEXT_N_DAYS] [s/SORT_ORDER]\n"
            + "NEXT_N_DAYS: Number of days to look ahead (1-365). Defaults to 30.\n"
            + "SORT_ORDER: How to sort the results ('date' or 'name'). Defaults to 'date'.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NEXT_N_DAYS + "60" + " " + PREFIX_SORT_ORDER + "name";

    public static final String MESSAGE_SUCCESS = "Found %d policies due for renewal";
    public static final String MESSAGE_NO_RENEWALS = "No upcoming renewals within the specified time period";

    public static final int DEFAULT_DAYS = 30;
    public static final String DEFAULT_SORT = "date";
    public static final String SORT_BY_DATE = "date";
    public static final String SORT_BY_NAME = "name";

    private final int days;
    private final String sortOrder;

    /**
     * Creates a ViewRenewalsCommand with the specified parameters.
     *
     * @param days Number of days to look ahead for renewals
     * @param sortOrder How to sort the results
     */
    public ViewRenewalsCommand(int days, String sortOrder) {
        this.days = days;
        this.sortOrder = sortOrder.toLowerCase();
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        List<Person> allPersons = model.getFilteredPersonList();
        List<Person> renewalsDue = allPersons.stream()
                .filter(person -> {
                    long daysUntil = person.getPolicy().getDaysUntilRenewal();
                    return daysUntil >= 0 && daysUntil <= days;
                })
                .sorted(getComparator())
                .collect(Collectors.toList());

        if (renewalsDue.isEmpty()) {
            return new CommandResult(MESSAGE_NO_RENEWALS);
        }

        model.updateFilteredPersonList(person -> {
            long daysUntil = person.getPolicy().getDaysUntilRenewal();
            return daysUntil >= 0 && daysUntil <= days;
        });
        return new CommandResult(String.format(MESSAGE_SUCCESS, renewalsDue.size()));
    }

    private Comparator<Person> getComparator() {
        if (SORT_BY_NAME.equals(sortOrder)) {
            return Comparator.comparing(person -> person.getName().fullName);
        } else {
            return Comparator.comparing(person -> person.getPolicy().renewalDate);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ViewRenewalsCommand)) {
            return false;
        }

        ViewRenewalsCommand otherCommand = (ViewRenewalsCommand) other;
        return days == otherCommand.days
                && sortOrder.equals(otherCommand.sortOrder);
    }
}
