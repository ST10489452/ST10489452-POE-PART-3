package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the Login class (Part 1 features). */
class LoginTest {

    private Login login;

    @BeforeEach
    void setUp() {
        login = new Login("Kyle", "Smith");
    }

    @Test
    void usernameWithUnderscoreAndFiveCharactersIsValid() {
        assertEquals(true, login.checkUserName("kyl_1"));
    }

    @Test
    void usernameShorterThanFiveCharactersIsValid() {
        assertEquals(true, login.checkUserName("ab_"));
    }

    @Test
    void usernameWithoutUnderscoreIsInvalid() {
        assertEquals(false, login.checkUserName("kyle1"));
    }

    @Test
    void usernameLongerThanFiveCharactersIsInvalid() {
        assertEquals(false, login.checkUserName("kyle_smith"));
    }

    @Test
    void complexPasswordIsValid() {
        assertEquals(true, login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    void passwordWithoutCapitalNumberOrSpecialIsInvalid() {
        assertEquals(false, login.checkPasswordComplexity("password"));
    }

    @Test
    void passwordShorterThanEightCharactersIsInvalid() {
        assertEquals(false, login.checkPasswordComplexity("Short1!"));
    }

    @Test
    void cellNumberWithInternationalCodeIsValid() {
        assertEquals(true, login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    void cellNumberWithoutInternationalCodeIsInvalid() {
        assertEquals(false, login.checkCellPhoneNumber("08966553"));
    }

    @Test
    void successfulRegistrationReturnsAllThreeMessages() {
        assertEquals("Username successfully captured.\nPassword successfully captured.\n"
                        + "Cell phone number successfully added.",
                login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976"));
    }

    @Test
    void registrationWithBadUsernameReturnsTheUsernameError() {
        assertEquals(Login.USERNAME_ERROR, login.registerUser("kyle", "Ch&&sec@ke99!", "+27838968976"));
    }

    @Test
    void correctLoginWelcomesTheUserByName() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Welcome Kyle Smith it is great to see you.",
                login.returnLoginStatus("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    void wrongPasswordFailsLogin() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals(Login.LOGIN_FAILED, login.returnLoginStatus("kyl_1", "wrong"));
    }

    @Test
    void loginBeforeRegistrationFails() {
        assertEquals(false, login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }
}
