# QuickChat - PROG5121 Part 3 (Store Data and Display Task Report)

Console chat app: registration and login (Part 1), sending / storing / disregarding
messages (Part 2), and stored-message arrays, JSON storage, search, delete and
report features (Part 3).

## Project layout

| File | Purpose |
|---|---|
| `Login.java` | Part 1 - validation, registration, login |
| `Message.java` | Part 2 - one message: ID, hash, validation |
| `MessageManager.java` | Part 3 - the five arrays + all "Stored Messages" features |
| `JsonMessageStore.java` | Part 3 - reads / writes `messages.json` with Gson |
| `Main.java` | Console menus only |
| `src/test/java/...` | JUnit 5 tests (46 tests) |
| `.github/workflows/TestJava.yml` | Runs the tests on GitHub |

## Run

    mvn test                                # run the unit tests
    mvn compile exec:java -Dexec.mainClass=com.mycompany.main.Main   # run the app
    # or run Main.java from NetBeans / IntelliJ

## The arrays (no hard-coding)

`MessageManager` holds `sentMessages`, `disregardedMessages`, `storedMessages`,
`messageHashes` and `messageIDs`, plus parallel arrays for sender, recipient, text
and flag. Index *i* of every master array is the same message. Arrays are filled
only while the app runs (Send / Disregard / Store) and by reading `messages.json`
at start-up. Deleting removes the message from every array at once.

## Assumptions about the test data

The brief does not show Messages 4 and 5 in its test-data table, so they were taken
from the unit-test table:

* Message 4 - recipient `0838884567`, "It is dinner time!", Sent
* Message 5 - recipient `+27838884567`, "Ok, I am leaving without you.", Stored

The delete confirmation drops the final full stop of the message so the output
matches the wording in the brief exactly.

## References (attribution)

* Google. *Gson User Guide* (Array Examples, Object Examples, Using Gson with Maven).
  https://google.github.io/gson/UserGuide.html - used for `JsonMessageStore`
  (`toJson`, `fromJson(reader, Message[].class)`).
* Oracle. *java.util.regex.Pattern* (Java SE 17 API).
  https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html
  - regex syntax for the cell number check.
* Oracle. *java.nio.file.Files* (Java SE 17 API).
  https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/nio/file/Files.html
  - `newBufferedReader` / `newBufferedWriter` for the JSON file.

> Replace or add to this list with every source you personally used.
