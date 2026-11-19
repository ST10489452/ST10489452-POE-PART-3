# ST10489452 - PROG5121 Part 3

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

    mvn test                              # run the unit tests
    mvn compile exec:java -Dexec.mainClass=com.mycompany.main.Main   # run the app
    # or run Main.java from NetBeans / IntelliJ

## The arrays (no hard-coding)

MessageManager includes sentMessages, disregardedMessages, storedMessages, messageHashes, and messageIDs together with separate arrays for sender, recipient, text, and flag. Index i of each of these main arrays is the same message. These arrays will be populated only while the program is running (Send/Disregard/Store) or when reading messages.json during start-up. Deletion deletes the message from all arrays simultaneously.

## Assumptions about the test data
The brief does not include Messages 4 and 5 in its test data table, so they had to be extracted from the unit-test table:

Message 4 – recipient 0838884567, "It is dinner time!", Sent
Message 5 – recipient +27838884567, "Ok, I am leaving without you.", Stored
Deletion confirmation deletes the last full stop in the message so that the output is identical to the one given in the brief.

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
