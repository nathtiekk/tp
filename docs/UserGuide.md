---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# InsureBook User Guide

InsureBook is a desktop application for insurance agents to manage their client portfolio and track policy renewals. The application is optimized for use via a Command Line Interface (CLI) while still having the benefits of a Graphical User Interface (GUI).

<!-- * Table of Contents -->
<page-nav-print />

---

## Quick start

1. Ensure you have Java `11` or above installed in your Computer.

1. Download the latest `insurebook.jar` from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the home folder for your InsureBook.

1. Double-click the file to start the app. The GUI should appear in a few seconds.

1. Type the command in the command box and press Enter to execute it. e.g. typing `help` and pressing Enter will open the help window.

Some example commands you can try:

-   `list` : Lists all clients
-   `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 pol/POL-123 rd/2024-12-31` : Adds a client named `John Doe` with policy number POL-123
-   `delete 3` : Deletes the 3rd client shown in the current list
-   `clear` : Deletes all clients
-   `exit` : Exits the app

1. Refer to the [Features](#features) below for details of each command.

---

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

-   Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
    e.g. in `add n/NAME`, `NAME` is a parameter which can be used as `add n/John Doe`.

-   Items in square brackets are optional.<br>
    e.g `n/NAME [t/TAG]` can be used as `n/John Doe t/vip` or as `n/John Doe`.

-   Items with `…`​ after them can be used multiple times including zero times.<br>
    e.g. `[t/TAG]…​` can be used as `t/friend`, `t/friend t/family`, etc.

-   Parameters can be in any order.<br>
    e.g. if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

-   Extraneous parameters for commands that do not take in parameters (such as `help`, `list`, `exit` and `clear`) will be ignored.<br>
    e.g. if the command specifies `help 123`, it will be interpreted as `help`.

-   If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
    </box>

### Viewing help : `help`

Shows a message explaining how to access the help page.

Format: `help`

### Adding a client: `add`

Adds a client to InsureBook.

Format: `add n/NAME p/PHONE e/EMAIL a/ADDRESS pol/POLICY_NUMBER rd/RENEWAL_DATE [t/TAG]...`

-   The policy number must be in the format POL-XXX where XXX is a number
-   The renewal date must be in the format YYYY-MM-DD
-   Tags are optional

Examples:

-   `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 pol/POL-123 rd/2024-12-31`
-   `add n/Betsy Crowe t/vip e/betsyc@example.com a/Newgate Prison p/1234567 pol/POL-456 rd/2025-06-30 t/high-value`

### Listing all clients : `list`

Shows a list of all clients in InsureBook.

Format: `list`

### Viewing renewals : `viewrenewals`

Shows a list of clients whose policies are due for renewal within the specified number of days.

Format: `viewrenewals n/DAYS [s/SORT_ORDER]`

-   `DAYS` must be a positive integer between 1 and 365
-   `SORT_ORDER` is optional and can be either `name` or `date` (defaults to `date` if not specified)

Examples:

-   `viewrenewals n/30` shows policies due for renewal in the next 30 days, sorted by renewal date
-   `viewrenewals n/60 s/name` shows policies due for renewal in the next 60 days, sorted by client name

### Editing a client : `edit`

Edits an existing client in InsureBook.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [pol/POLICY] [rd/RENEWAL_DATE] [t/TAG]...`

-   Edits the client at the specified `INDEX`. The index refers to the index number shown in the displayed client list. The index **must be a positive integer** 1, 2, 3, …​
-   At least one of the optional fields must be provided.
-   Existing values will be updated to the input values.
-   When editing tags, the existing tags of the client will be removed i.e adding of tags is not cumulative.
-   You can remove all the client's tags by typing `t/` without specifying any tags after it.

Examples:

-   `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st client to be `91234567` and `johndoe@example.com` respectively.
-   `edit 2 n/Betsy Crower t/` Edits the name of the 2nd client to be `Betsy Crower` and clears all existing tags.

### Locating clients by name: `find`

Finds clients whose names contain any of the given keywords.
Finds persons whose names contain any of the given values.

Format: `find [n/NAME]… [p/PHONE]…`

* At least one of the optional fields must be provided.
* Each field may be provided more than once.
* Each field may contain more than one word.
* The search is case-insensitive. e.g `hans` will match `Hans`
* The order of the values matter for a field but not for different fields. e.g. `n/Hans Bo` will not match `Bo Hans` but `n/Hans n/Bo` will match `Bo Hans`
* Partial words will also be matched e.g. `n/Han` will match `Hans`
* Persons matching at least one field will be returned (i.e. `OR` search).
  e.g. `n/Hans n/Bo` will return `Hans Gruber`, `Bo Yang`
-   The search is case-insensitive. e.g `hans` will match `Hans`
-   The order of the keywords does not matter. e.g. `Hans Bo` will match `Bo Hans`
-   Only the name is searched.
-   Only full words will be matched e.g. `Han` will not match `Hans`
-   Clients matching at least one keyword will be returned (i.e. `OR` search).
    e.g. `Hans Bo` will return `Hans Gruber`, `Bo Yang`

Examples:
* `find n/John` returns `john` and `John Doe`
* `find n/Amy p/999` returns `Amy Goh (96372716)` and `Local Police (999)`
* `find n/alex n/david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Deleting a client : `delete`

Deletes the specified client from InsureBook.

Format: `delete INDEX`

-   Deletes the client at the specified `INDEX`.
-   The index refers to the index number shown in the displayed client list.
-   The index **must be a positive integer** 1, 2, 3, …​

Examples:

-   `list` followed by `delete 2` deletes the 2nd client in the InsureBook.
-   `find Betsy` followed by `delete 1` deletes the 1st client in the results of the `find` command.

### Clearing all entries : `clear`

Clears all entries from InsureBook.

Format: `clear`

### Exiting the program : `exit`

Exits the program.

Format: `exit`

### Saving the data

InsureBook data are saved in the hard disk automatically after any command that changes the data. There is no need to save manually.

### Editing the data file

InsureBook data are saved automatically as a JSON file `[JAR file location]/data/insurebook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes to the data file makes its format invalid, InsureBook will discard all data and start with an empty data file at the next run. Hence, it is recommended to take a backup of the file before editing it.<br>
Furthermore, certain edits can cause the InsureBook to behave in unexpected ways (e.g., if a value entered is outside the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

---

## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous InsureBook home folder.

---

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

---

## Command summary

| Action     | Format, Examples                                                                                                                                                                                                            |
| ---------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Add**    | `add n/NAME p/PHONE e/EMAIL a/ADDRESS pol/POLICY_NUMBER rd/RENEWAL_DATE [t/TAG]...` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 pol/POL-123 rd/2024-12-31 t/friend t/colleague` |
| **Clear**  | `clear`                                                                                                                                                                                                                     |
| **Delete** | `delete INDEX`<br> e.g., `delete 3`                                                                                                                                                                                         |
| **Edit**   | `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [pol/POLICY] [rd/RENEWAL_DATE] [t/TAG]...`<br> e.g.,`edit 2 n/James Lee e/jameslee@example.com`                                                                        |
| **Find**   | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`                                                                                                                                                                  |
| **List**   | `list`                                                                                                                                                                                                                      |
| **Help**   | `help`                                                                                                                                                                                                                      |
