package controllers;

import application.BalootApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.NotExistentUser;
import model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import service.Baloot;

import java.util.Map;

import static defines.Errors.INVALID_CREDIT_RANGE;
import static defines.Errors.NOT_EXISTENT_USER;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = BalootApplication.class)
public class UserControllerApiTest {
    @MockBean
    private Baloot baloot;
    @Autowired
    private UserController userController;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static User getAnonymouseUser() {
        return new User("username", "password", "email@mail.com", "2023-01-01", "address");
    }

    @BeforeEach
    public void setUp() {
        userController.setBaloot(baloot);
    }

    @Test
    @DisplayName("Test getUser() with a valid id")
    public void testGetUserApiWithValidId() throws Exception {
        User user = getAnonymouseUser();
        when(baloot.getUserById(user.getUsername())).thenReturn(user);
        mockMvc.perform(get("/users/{id}", user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.password").value(user.getPassword()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.birthDate").value(user.getBirthDate()))
                .andExpect(jsonPath("$.address").value(user.getAddress()));
    }

    @Test
    @DisplayName("Test getUser() with an invalid id")
    public void testGetUserApiWithInvalidId() throws Exception {
        when(baloot.getUserById(anyString())).thenThrow(new NotExistentUser());
        mockMvc.perform(get("/users/{id}", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("Test addCredit() with a valid id and credit")
    public void testAddCreditApiWithValidIdAndCredit() throws Exception {
        User user = getAnonymouseUser();
        Map<String, String> input = Map.of("credit", "1000");
        when(baloot.getUserById(user.getUsername())).thenReturn(user);
        mockMvc.perform(post("/users/{id}/credit", user.getUsername())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().string("credit added successfully!"));
    }

    @Test
    @DisplayName("Test addCredit() with an invalid id")
    public void testAddCreditApiWithInvalidId() throws Exception {
        Map<String, String> input = Map.of("credit", "1000");
        when(baloot.getUserById("1")).thenThrow(new NotExistentUser());
        mockMvc.perform(post("/users/{id}/credit", "1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_EXISTENT_USER));
    }

    @Test
    @DisplayName("Test addCredit() with an invalid credit")
    public void testAddCreditApiWithInvalidCredit() throws Exception {
        User user = getAnonymouseUser();
        Map<String, String> input = Map.of("credit", "invalidCredit");
        when(baloot.getUserById(user.getUsername())).thenReturn(user);
        mockMvc.perform(post("/users/{id}/credit", user.getUsername())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please enter a valid number for the credit amount."));
    }

    @Test
    @DisplayName("Test addCredit() with a negative credit")
    public void testAddCreditApiWithNegativeCredit() throws Exception {
        User user = getAnonymouseUser();
        Map<String, String> input = Map.of("credit", "-1000");
        when(baloot.getUserById(user.getUsername())).thenReturn(user);
        mockMvc.perform(post("/users/{id}/credit", user.getUsername())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(INVALID_CREDIT_RANGE));
    }

    @Test
    @DisplayName("Test addCredit() with no credit")
    public void testAddCreditApiWithNoCredit() throws Exception {
        User user = getAnonymouseUser();
        Map<String, String> input = Map.of();
        when(baloot.getUserById(user.getUsername())).thenReturn(user);
        mockMvc.perform(post("/users/{id}/credit", user.getUsername())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please enter a valid number for the credit amount."));
    }
}
