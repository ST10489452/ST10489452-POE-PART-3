package com.mycompany.main;

/**
 * Login class for the Chat Application - Part 1: Registration and Login.
 *
 * Responsibilities:
 * - Validate the username, password and cell phone number
 * - Register the user (store the credentials)
 * - Log the user in and return the login status message
 *
 * All user-facing messages are constants so that Main and the unit tests
 * always use exactly the same wording.
 *
 * @author Mc Chopper
 * @version 3.0
 */
public class Login {

    // ---- Messages shown to the user ----
    public static final String USERNAME_CAPTURED = "Username successfully captured.";
    public static final String PASSWORD_CAPTURED = "Password successfully captured.";
    public static final String CELL_CAPTURED = "Cell phone number successfully added.";

    public static final String USERNAME_ERROR =
            "Username is not correctly formatted; please ensure that your username "
            + "contains an underscore and is no more than five characters in length.";
    public static final String PASSWORD_ERROR =
            "Password is not correctly formatted; please ensure that the password "
            + "contains at least eight characters, a capital letter, a number, and a special character.";
    public static final String CELL_ERROR =
            "Cell phone number incorrectly formatted or does not contain international code; "
            + "please correct the number and try again.";

    public static final String LOGIN_FAILED = "Username or password incorrect, please try again.";

    /**
     * Cell phone pattern: '+' followed by 9 to 11 digits (e.g. +27838968976).
     * ATTRIBUTION: regex syntax from the Oracle Java documentation for
     * java.util.regex.Pattern:
     * https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html
     */
    private static final String CELL_PHONE_REGEX = "^\\+[0-9]{9,11}$";

    private static final int MAX_USERNAME_LENGTH = 5;
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final String firstName;
    private final String lastName;
    private String registeredUsername;
    private String registeredPassword;
    private String registeredCellPhone;

    /**
     * Creates a Login object for a user.
     *
     * @param firstName The user's first name.
     * @param lastName  The user's last name.
     */
    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Checks that a username contains an underscore and is no more than five characters.
     *
     * @param username The username to validate.
     * @return true if valid, false otherwise.
     */
    public boolean checkUserName(String username) {
        return username != null
                && username.contains("_")
                && username.length() <= MAX_USERNAME_LENGTH;
    }

    /**
     * Checks that a password has at least eight characters, a capital letter,
     * a number and a special character.
     *
     * @param password The password to validate.
     * @return true if the password meets every rule, false otherwise.
     */
    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }

        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasCapital = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (!Character.isLetter(c)) {
                hasSpecial = true;
            }
        }
        return hasCapital && hasNumber && hasSpecial;
    }

    /**
     * Checks that a cell phone number starts with '+' (international code)
     * followed by 9 to 11 digits.
     *
     * @param cellPhoneNumber The number to validate.
     * @return true if correctly formatted, false otherwise.
     */
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        return isValidCellNumber(cellPhoneNumber);
    }

    /**
     * Shared cell number check, also used by Message to validate recipients
     * so the regex lives in one place only.
     *
     * @param cellPhoneNumber The number to validate.
     * @return true if correctly formatted, false otherwise.
     */
    public static boolean isValidCellNumber(String cellPhoneNumber) {
        return cellPhoneNumber != null && cellPhoneNumber.matches(CELL_PHONE_REGEX);
    }

    /**
     * Registers the user if the username, password and cell number are all valid.
     *
     * @param username        The desired username.
     * @param password        The desired password.
     * @param cellPhoneNumber The user's cell phone number.
     * @return The three success messages, or the first validation error found.
     */
    public String registerUser(String username, String password, String cellPhoneNumber) {
        if (!checkUserName(username)) {
            return USERNAME_ERROR;
        }
        if (!checkPasswordComplexity(password)) {
            return PASSWORD_ERROR;
        }
        if (!checkCellPhoneNumber(cellPhoneNumber)) {
            return CELL_ERROR;
        }

        this.registeredUsername = username;
        this.registeredPassword = password;
        this.registeredCellPhone = cellPhoneNumber;

        return USERNAME_CAPTURED + "\n" + PASSWORD_CAPTURED + "\n" + CELL_CAPTURED;
    }

    /**
     * Checks the entered credentials against the registered ones.
     *
     * @param username The username entered at login.
     * @param password The password entered at login.
     * @return true if both match, false otherwise (or if nobody is registered).
     */
    public boolean loginUser(String username, String password) {
        return registeredUsername != null
                && registeredUsername.equals(username)
                && registeredPassword.equals(password);
    }

    /**
     * Returns the login status message.
     *
     * @param username The username entered at login.
     * @param password The password entered at login.
     * @return A personalised welcome on success, otherwise a retry message.
     */
    public String returnLoginStatus(String username, String password) {
        if (loginUser(username, password)) {
            return "Welcome " + firstName + " " + lastName + " it is great to see you.";
        }
        return LOGIN_FAILED;
    }

    /** @return The registered cell phone number (used as the message sender). */
    public String getRegisteredCellPhone() {
        return registeredCellPhone;
    }
}
