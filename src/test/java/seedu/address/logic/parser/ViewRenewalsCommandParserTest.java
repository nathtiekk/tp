package seedu.address.logic.parser;

import static seedu.address.logic.commands.ViewRenewalsCommand.DEFAULT_DAYS;
import static seedu.address.logic.commands.ViewRenewalsCommand.DEFAULT_SORT;
import static seedu.address.logic.commands.ViewRenewalsCommand.SORT_BY_DATE;
import static seedu.address.logic.commands.ViewRenewalsCommand.SORT_BY_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ViewRenewalsCommand;

public class ViewRenewalsCommandParserTest {

    private ViewRenewalsCommandParser parser = new ViewRenewalsCommandParser();

    @Test
    public void parse_emptyArg_returnsViewRenewalsCommand() {
        // empty string
        ViewRenewalsCommand expectedCommand = new ViewRenewalsCommand(DEFAULT_DAYS, DEFAULT_SORT);
        assertParseSuccess(parser, "", expectedCommand);

        // whitespace only
        assertParseSuccess(parser, "   ", expectedCommand);
    }

    @Test
    public void parse_validArgs_returnsViewRenewalsCommand() {
        // only days specified
        ViewRenewalsCommand expectedCommand = new ViewRenewalsCommand(60, DEFAULT_SORT);
        assertParseSuccess(parser, " n/60", expectedCommand);

        // only sort specified - date
        expectedCommand = new ViewRenewalsCommand(DEFAULT_DAYS, SORT_BY_DATE);
        assertParseSuccess(parser, " s/date", expectedCommand);

        // only sort specified - name
        expectedCommand = new ViewRenewalsCommand(DEFAULT_DAYS, SORT_BY_NAME);
        assertParseSuccess(parser, " s/name", expectedCommand);

        // both parameters specified - days first
        expectedCommand = new ViewRenewalsCommand(60, SORT_BY_NAME);
        assertParseSuccess(parser, " n/60 s/name", expectedCommand);

        // both parameters specified - sort first
        expectedCommand = new ViewRenewalsCommand(60, SORT_BY_NAME);
        assertParseSuccess(parser, " s/name n/60", expectedCommand);

        // edge case - minimum days
        expectedCommand = new ViewRenewalsCommand(1, DEFAULT_SORT);
        assertParseSuccess(parser, " n/1", expectedCommand);

        // edge case - maximum days
        expectedCommand = new ViewRenewalsCommand(365, DEFAULT_SORT);
        assertParseSuccess(parser, " n/365", expectedCommand);
    }

    @Test
    public void parse_invalidDays_throwsParseException() {
        // negative days
        assertParseFailure(parser, " n/-1", ViewRenewalsCommandParser.MESSAGE_INVALID_DAYS);

        // zero days
        assertParseFailure(parser, " n/0", ViewRenewalsCommandParser.MESSAGE_INVALID_DAYS);

        // days > 365
        assertParseFailure(parser, " n/366", ViewRenewalsCommandParser.MESSAGE_INVALID_DAYS);

        // non-numeric days
        assertParseFailure(parser, " n/abc", ViewRenewalsCommandParser.MESSAGE_INVALID_DAYS);
        assertParseFailure(parser, " n/1.5", ViewRenewalsCommandParser.MESSAGE_INVALID_DAYS);
        assertParseFailure(parser, " n/1a", ViewRenewalsCommandParser.MESSAGE_INVALID_DAYS);

        // empty days value
        assertParseFailure(parser, " n/", ViewRenewalsCommandParser.MESSAGE_INVALID_DAYS);
    }

    @Test
    public void parse_invalidSort_throwsParseException() {
        // invalid sort order
        assertParseFailure(parser, " s/invalid", ViewRenewalsCommandParser.MESSAGE_INVALID_SORT);
        assertParseFailure(parser, " s/dates", ViewRenewalsCommandParser.MESSAGE_INVALID_SORT);
        assertParseFailure(parser, " s/names", ViewRenewalsCommandParser.MESSAGE_INVALID_SORT);

        // empty sort value
        assertParseFailure(parser, " s/", ViewRenewalsCommandParser.MESSAGE_INVALID_SORT);
    }

    @Test
    public void parse_validSortCaseInsensitive_returnsViewRenewalsCommand() {
        // uppercase
        ViewRenewalsCommand expectedCommand = new ViewRenewalsCommand(DEFAULT_DAYS, SORT_BY_DATE);
        assertParseSuccess(parser, " s/DATE", expectedCommand);
        expectedCommand = new ViewRenewalsCommand(DEFAULT_DAYS, SORT_BY_NAME);
        assertParseSuccess(parser, " s/NAME", expectedCommand);

        // mixed case
        expectedCommand = new ViewRenewalsCommand(DEFAULT_DAYS, SORT_BY_DATE);
        assertParseSuccess(parser, " s/DaTe", expectedCommand);
        expectedCommand = new ViewRenewalsCommand(DEFAULT_DAYS, SORT_BY_NAME);
        assertParseSuccess(parser, " s/NaMe", expectedCommand);
    }

    @Test
    public void parse_duplicateParameters_returnsLastValue() {
        // duplicate days - last value used
        ViewRenewalsCommand expectedCommand = new ViewRenewalsCommand(60, DEFAULT_SORT);
        assertParseSuccess(parser, " n/30 n/60", expectedCommand);

        // duplicate sort - last value used
        expectedCommand = new ViewRenewalsCommand(DEFAULT_DAYS, SORT_BY_NAME);
        assertParseSuccess(parser, " s/date s/name", expectedCommand);

        // duplicate mixed parameters - last values used
        expectedCommand = new ViewRenewalsCommand(60, SORT_BY_NAME);
        assertParseSuccess(parser, " n/30 s/date n/60 s/name", expectedCommand);
    }

    @Test
    public void parse_invalidCombinations_throwsParseException() {
        // invalid days with valid sort
        assertParseFailure(parser, " n/-1 s/date", ViewRenewalsCommandParser.MESSAGE_INVALID_DAYS);
        // valid days with invalid sort
        assertParseFailure(parser, " n/60 s/invalid", ViewRenewalsCommandParser.MESSAGE_INVALID_SORT);
        // both invalid
        assertParseFailure(parser, " n/-1 s/invalid", ViewRenewalsCommandParser.MESSAGE_INVALID_DAYS);
    }
}
