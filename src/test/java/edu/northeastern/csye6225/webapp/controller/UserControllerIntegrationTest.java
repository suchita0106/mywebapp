package edu.northeastern.csye6225.webapp.controller;

import edu.northeastern.csye6225.webapp.dto.UserDTO;
import edu.northeastern.csye6225.webapp.model.User;
import edu.northeastern.csye6225.webapp.service.MyUserDetailsService;
import edu.northeastern.csye6225.webapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@domain.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        String uniqueEmail = "test" + System.currentTimeMillis() + "@domain.com";  // Unique email for each test run

        String userJson = String.format(
                "{\"email\":\"%s\", \"firstName\":\"John\", \"lastName\":\"Doe\", \"password\":\"password\"}", uniqueEmail);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());
    }


    @Test
    public void testRegisterUser_EmailAlreadyExists() throws Exception {
        // Create a user object
        User user = new User();
        user.setEmail("test2@domain.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");

         when(userService.createUser(any(User.class))).thenThrow(new IllegalArgumentException("Email already exists"));

         mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test2@domain.com\", \"firstName\":\"John\", \"lastName\":\"Doe\", \"password\":\"password\"}"))
                .andExpect(status().isBadRequest()) // 400 Bad Request
                .andExpect(content().string("{\"error\":\"Email already exists\"}"));  // Check the error message
    }


    @Test
    public void testGetUser_Unauthorized() throws Exception {
         mockMvc.perform(get("/api/v1/users/self")
                        .principal(() -> "nonexistent@domain.com")) // Simulate a principal with no matching user
                .andExpect(status().isBadRequest());
    }
}
