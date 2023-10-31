package controllers;

import exceptions.IncorrectPassword;
import exceptions.NotExistentUser;
import exceptions.UsernameAlreadyTaken;
import model.User;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.Map;

import static defines.Errors.INCORRECT_PASSWORD;
import static defines.Errors.NOT_EXISTENT_USER;
import static defines.Errors.USERNAME_ALREADY_TAKEN;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {
    private AuthenticationController authController;
    private Baloot baloot;

    @BeforeEach
    public void setUp() {
        baloot = mock(Baloot.class);
        authController = new AuthenticationController();
        authController.setBaloot(baloot);
    }

    @Test
    @DisplayName("Test correct login credentials")
    public void testLogin() throws NotExistentUser, IncorrectPassword {
        Map<String, String> input = Map.of("username", "person", "password", "123");
        doNothing().when(baloot).login("person", "123");
        ResponseEntity<String> response = authController.login(input);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("login successfully!", response.getBody());
    }

    @Test
    @DisplayName("Test login with nonexistent user")
    public void testLoginNonexistentUser() throws NotExistentUser, IncorrectPassword {
        Map<String, String> input = Map.of("username", "person", "password", "123");
        doThrow(new NotExistentUser()).when(baloot).login(eq("person"), anyString());
        ResponseEntity<String> response = authController.login(input);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals(NOT_EXISTENT_USER, response.getBody());
    }

    @Test
    @DisplayName("Test login with incorrect password")
    public void testLoginIncorrectPassword() throws NotExistentUser, IncorrectPassword {
        Map<String, String> input = Map.of("username", "person", "password", "wrong");
        doThrow(new IncorrectPassword()).when(baloot).login(eq("person"), not(eq("123")));
        ResponseEntity<String> response = authController.login(input);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertEquals(INCORRECT_PASSWORD, response.getBody());
    }

    private static Map<String, String> getSignupInput() {
        return Map.of("username", "person",
                "password", "123",
                "email", "email@mail.com",
                "address", "address",
                "birthDate", "2023");
    }

    @Test
    @DisplayName("Test correct signup attempt")
    public void testSignup() throws UsernameAlreadyTaken {
        Map<String, String> input = getSignupInput();
        doNothing().when(baloot).addUser(any());
        ResponseEntity<String> response = authController.signup(input);
        verify(baloot, times(1)).addUser(any());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("signup successfully!", response.getBody());
    }

    @Test
    @DisplayName("Test correct signup user credentials")
    public void testSignupUserCorrect() throws UsernameAlreadyTaken {
        Map<String, String> input = getSignupInput();
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        doNothing().when(baloot).addUser(userCaptor.capture());
        authController.signup(input);
        User capturedUser = userCaptor.getValue();
        Assertions.assertEquals(input.get("username"), capturedUser.getUsername());
        Assertions.assertEquals(input.get("password"), capturedUser.getPassword());
        Assertions.assertEquals(input.get("email"), capturedUser.getEmail());
        Assertions.assertEquals(input.get("birthDate"), capturedUser.getBirthDate());
        Assertions.assertEquals(input.get("address"), capturedUser.getAddress());
    }

    @Test
    @DisplayName("Test signup with already taken username")
    public void testSignupUsernameAlreadyTaken() throws UsernameAlreadyTaken {
        Map<String, String> input = getSignupInput();
        doThrow(new UsernameAlreadyTaken()).when(baloot).addUser(argThat(user -> user.getUsername().equals(input.get("username"))));
        ResponseEntity<String> response = authController.signup(input);
        verify(baloot, times(1)).addUser(argThat(user -> user.getUsername().equals(input.get("username"))));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals(USERNAME_ALREADY_TAKEN, response.getBody());
    }
}
