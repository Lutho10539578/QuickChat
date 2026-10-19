package quickchat;

import java.util.regex.Pattern;

/**
 * Handles registration and login for the QuickChat application.
 *
 * A Login object holds the details of one user. The check methods validate
 * those stored details, registerUser() reports whether registration worked,
 * and loginUser() compares entered details with the registered ones.
 */
public class Login {

    // ---- Messages (kept as constants so the app and the tests share one source) ----
    public static final String USERNAME_OK = "Username successfully captured.";
    public static final String USERNAME_BAD = "Username is not correctly formatted; "
            + "please ensure that your username contains an underscore and is no more "
            + "than five characters in length.";

    public static final String PASSWORD_OK = "Password successfully captured.";
    public static final String PASSWORD_BAD = "Password is not correctly formatted; "
            + "please ensure that the password contains at least eight characters, "
            + "a capital letter, a number, and a special character.";

    public static final String CELL_OK = "Cell number successfully captured.";
    public static final String CELL_BAD = "Cell number is incorrectly formatted or does not "
            + "contain an international code; please correct the number and try again.";

    public static final String REGISTER_OK = "User has been registered successfully.";
    public static final String LOGIN_BAD = "Username or password incorrect, please try again.";

    private static final int MAX_USERNAME_LENGTH = 5;
    private static final int MIN_PASSWORD_LENGTH = 8;

    /*
     * Cell phone pattern: a "+", an international country code of 1 to 3 digits
     * (the first digit cannot be 0), then the number itself of no more than
     * ten digits.
     *
     * Reference: the pattern is adapted from the E.164 expression
     * "^\+[1-9]\d{1,14}$" given in:
     * Gupta, L. (2026) Regex for International Phone Numbers in Java (E.164).
     * HowToDoInJava. Available at:
     * https://howtodoinjava.com/regex/java-regex-validate-international-phone-numbers
     * [Accessed 18 October 2026].
     * The change: the 15-digit limit was split into a country code of 1-3 digits
     * followed by at most ten digits, to match the assignment brief.
     */
    private static final Pattern CELL_PATTERN = Pattern.compile("^\\+[1-9]\\d{0,2}\\d{1,10}$");

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhone;
    private boolean registered;

    /** Creates an empty Login; fill it using the setters. */
    public Login() {
        this("", "", "", "", "");
    }

    /** Creates a Login holding all of the details a user registers with. */
    public Login(String firstName, String lastName, String username,
                 String password, String cellPhone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhone = cellPhone;
        this.registered = false;
    }

    // ---- Setters (used by the console app to capture details one at a time) ----
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setCellPhone(String cellPhone) { this.cellPhone = cellPhone; }

    /**
     * Checks that the username contains an underscore and is no more than
     * five characters long.
     */
    public boolean checkUserName() {
        if (username == null) {
            return false;
        }
        return username.contains("_") && username.length() <= MAX_USERNAME_LENGTH;
    }

    /**
     * Checks password complexity: at least eight characters, and containing
     * a capital letter, a number and a special character.
     */
    public boolean checkPasswordComplexity() {
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
            } else if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                hasSpecial = true;
            }
        }
        return hasCapital && hasNumber && hasSpecial;
    }

    /**
     * Checks that the cell number starts with an international code and
     * is the correct length, using a regular expression.
     */
    public boolean checkCellPhoneNumber() {
        if (cellPhone == null) {
            return false;
        }
        return CELL_PATTERN.matcher(cellPhone).matches();
    }

    // ---- Message helpers: one per field, true or false wording ----
    public String getUsernameMessage() {
        return checkUserName() ? USERNAME_OK : USERNAME_BAD;
    }

    public String getPasswordMessage() {
        return checkPasswordComplexity() ? PASSWORD_OK : PASSWORD_BAD;
    }

    public String getCellPhoneMessage() {
        return checkCellPhoneNumber() ? CELL_OK : CELL_BAD;
    }

    /**
     * Registers the user. Returns the message for the first problem found
     * (username, then password, then cell number), or a success message
     * when every check passes.
     */
    public String registerUser() {
        registered = false;

        if (!checkUserName()) {
            return USERNAME_BAD;
        }
        if (!checkPasswordComplexity()) {
            return PASSWORD_BAD;
        }
        if (!checkCellPhoneNumber()) {
            return CELL_BAD;
        }

        registered = true;
        return REGISTER_OK;
    }

    /**
     * Verifies that the entered details match the details stored when the
     * user registered. Always false if registration has not succeeded.
     */
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        if (!registered) {
            return false;
        }
        return username.equals(enteredUsername) && password.equals(enteredPassword);
    }

    /** Returns the message for a successful or failed login. */
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return LOGIN_BAD;
    }
}
