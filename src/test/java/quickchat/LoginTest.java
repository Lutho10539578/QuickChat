package quickchat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Login class (Part 1), using the test data from the brief.
 */
class LoginTest {

    private static final String GOOD_PASSWORD = "Ch&&sec@ke99!";
    private static final String GOOD_CELL = "+27838968976";

    /** Builds a Login with valid details except for the ones a test overrides. */
    private Login loginWith(String username, String password, String cell) {
        return new Login("Kyle", "Smith", username, password, cell);
    }

    // ---------------- assertEquals tests ----------------

    @Test
    void usernameCorrectlyFormatted_message() {
        Login login = loginWith("kyl_1", GOOD_PASSWORD, GOOD_CELL);
        assertEquals("Username successfully captured.", login.getUsernameMessage());
    }

    @Test
    void usernameIncorrectlyFormatted_message() {
        Login login = loginWith("kyle!!!!!!!", GOOD_PASSWORD, GOOD_CELL);
        assertEquals("Username is not correctly formatted; please ensure that your "
                + "username contains an underscore and is no more than five characters "
                + "in length.", login.registerUser());
    }

    @Test
    void passwordMeetsComplexity_message() {
        Login login = loginWith("kyl_1", GOOD_PASSWORD, GOOD_CELL);
        assertEquals("Password successfully captured.", login.getPasswordMessage());
    }

    @Test
    void passwordDoesNotMeetComplexity_message() {
        Login login = loginWith("kyl_1", "password", GOOD_CELL);
        assertEquals("Password is not correctly formatted; please ensure that the "
                + "password contains at least eight characters, a capital letter, a "
                + "number, and a special character.", login.registerUser());
    }

    @Test
    void cellCorrectlyFormatted_message() {
        Login login = loginWith("kyl_1", GOOD_PASSWORD, "+27838968976");
        assertEquals("Cell number successfully captured.", login.getCellPhoneMessage());
    }

    @Test
    void cellIncorrectlyFormatted_message() {
        Login login = loginWith("kyl_1", GOOD_PASSWORD, "08966553");
        assertEquals("Cell number is incorrectly formatted or does not contain an "
                + "international code; please correct the number and try again.",
                login.getCellPhoneMessage());
    }

    @Test
    void registerUser_allValid_registersSuccessfully() {
        Login login = loginWith("kyl_1", GOOD_PASSWORD, GOOD_CELL);
        assertEquals("User has been registered successfully.", login.registerUser());
    }

    @Test
    void loginStatus_success_greetsUserByName() {
        Login login = loginWith("kyl_1", GOOD_PASSWORD, GOOD_CELL);
        login.registerUser();
        boolean result = login.loginUser("kyl_1", GOOD_PASSWORD);
        assertEquals("Welcome Kyle, Smith it is great to see you again.",
                login.returnLoginStatus(result));
    }

    @Test
    void loginStatus_failure_showsErrorMessage() {
        Login login = loginWith("kyl_1", GOOD_PASSWORD, GOOD_CELL);
        login.registerUser();
        boolean result = login.loginUser("kyl_1", "wrongPass1!");
        assertEquals("Username or password incorrect, please try again.",
                login.returnLoginStatus(result));
    }

    // ---------------- assertTrue / assertFalse tests ----------------

    @Test
    void loginSuccessful_returnsTrue() {
        Login login = loginWith("kyl_1", GOOD_PASSWORD, GOOD_CELL);
        login.registerUser();
        assertTrue(login.loginUser("kyl_1", GOOD_PASSWORD));
    }

    @Test
    void loginFailed_returnsFalse() {
        Login login = loginWith("kyl_1", GOOD_PASSWORD, GOOD_CELL);
        login.registerUser();
        assertFalse(login.loginUser("kyl_1", "notMyPassword1!"));
        assertFalse(login.loginUser("other", GOOD_PASSWORD));
    }

    @Test
    void loginBeforeRegistration_returnsFalse() {
        Login login = loginWith("kyl_1", GOOD_PASSWORD, GOOD_CELL);
        assertFalse(login.loginUser("kyl_1", GOOD_PASSWORD));
    }

    @Test
    void usernameCorrectlyFormatted_returnsTrue() {
        assertTrue(loginWith("kyl_1", GOOD_PASSWORD, GOOD_CELL).checkUserName());
    }

    @Test
    void usernameIncorrectlyFormatted_returnsFalse() {
        assertFalse(loginWith("kyle!!!!!!!", GOOD_PASSWORD, GOOD_CELL).checkUserName());
        assertFalse(loginWith("kyle", GOOD_PASSWORD, GOOD_CELL).checkUserName());      // no underscore
        assertFalse(loginWith("ky_le1", GOOD_PASSWORD, GOOD_CELL).checkUserName());    // 6 characters
    }

    @Test
    void passwordMeetsComplexity_returnsTrue() {
        assertTrue(loginWith("kyl_1", GOOD_PASSWORD, GOOD_CELL).checkPasswordComplexity());
    }

    @Test
    void passwordDoesNotMeetComplexity_returnsFalse() {
        assertFalse(loginWith("kyl_1", "password", GOOD_CELL).checkPasswordComplexity());
        assertFalse(loginWith("kyl_1", "Pass1!", GOOD_CELL).checkPasswordComplexity());      // too short
        assertFalse(loginWith("kyl_1", "password1!", GOOD_CELL).checkPasswordComplexity());  // no capital
        assertFalse(loginWith("kyl_1", "Password!!", GOOD_CELL).checkPasswordComplexity());  // no number
        assertFalse(loginWith("kyl_1", "Password11", GOOD_CELL).checkPasswordComplexity());  // no special
    }

    @Test
    void cellNumberCorrectlyFormatted_returnsTrue() {
        assertTrue(loginWith("kyl_1", GOOD_PASSWORD, "+27838968976").checkCellPhoneNumber());
    }

    @Test
    void cellNumberIncorrectlyFormatted_returnsFalse() {
        assertFalse(loginWith("kyl_1", GOOD_PASSWORD, "08966553").checkCellPhoneNumber());
        assertFalse(loginWith("kyl_1", GOOD_PASSWORD, "27838968976").checkCellPhoneNumber());      // no plus
        assertFalse(loginWith("kyl_1", GOOD_PASSWORD, "+2783896897612345").checkCellPhoneNumber()); // too long
    }
}
