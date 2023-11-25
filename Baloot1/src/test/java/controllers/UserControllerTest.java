package controllers;

import application.BalootApplication;
import model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import service.Baloot;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = BalootApplication.class)
public class UserControllerTest {
    @MockBean
    private Baloot baloot;
    @Autowired
    private UserController userController;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        userController.setBaloot(baloot);
    }

    @Test
    @DisplayName("Test")
    public void test() throws Exception {
        User user = new User("username", "password", "email@mail.com", "2023-01-01", "address");
        when(baloot.getUserById("username")).thenReturn(user);
        mockMvc.perform(get("/users/{id}", "username")).andExpect(status().isOk());
    }
}
