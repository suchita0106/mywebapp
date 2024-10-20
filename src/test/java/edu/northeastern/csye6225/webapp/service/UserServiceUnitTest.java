package edu.northeastern.csye6225.webapp.service;

import edu.northeastern.csye6225.webapp.Dao.UserDao;
import edu.northeastern.csye6225.webapp.exception.ResourceNotFoundException;
import edu.northeastern.csye6225.webapp.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceUnitTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDao userDao;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@domain.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
        user.setAccountCreated(new Date());
        user.setAccountUpdated(new Date());
    }

    @Test
    public void testCreateUser_Success() {
         TypedQuery<Long> mockedQuery = mock(TypedQuery.class);
        when(mockedQuery.setParameter(anyString(), any())).thenReturn(mockedQuery);
        when(mockedQuery.getSingleResult()).thenReturn(0L);  // No user exists with this email

         when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(mockedQuery);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

         userService.createUser(user);

         verify(entityManager, times(1)).persist(user);
    }

    @Test
    public void testCreateUser_EmailAlreadyExists() {
         TypedQuery<Long> mockedQuery = mock(TypedQuery.class);
        when(mockedQuery.setParameter(anyString(), any())).thenReturn(mockedQuery);
        when(mockedQuery.getSingleResult()).thenReturn(1L);  // User already exists

         when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(mockedQuery);

         IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    public void testFindByEmail_Success() {
         TypedQuery<User> mockedQuery = mock(TypedQuery.class);
        when(mockedQuery.setParameter(anyString(), any())).thenReturn(mockedQuery);
        when(mockedQuery.getSingleResult()).thenReturn(user);

         when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(mockedQuery);

        User foundUser = userService.findByEmail("test@domain.com");

        assertNotNull(foundUser);
        assertEquals("test@domain.com", foundUser.getEmail());
    }

    @Test
    public void testUpdateUser_Success() {
         when(userDao.findByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

         User mergedUser = new User();
        mergedUser.setFirstName("Jane");
        mergedUser.setLastName("Smith");
        mergedUser.setEmail("test@domain.com");

         when(entityManager.merge(any(User.class))).thenReturn(mergedUser);

         user.setFirstName("Jane");
        user.setLastName("Smith");

         User updatedUser = userService.updateUser(user);

         verify(entityManager, times(1)).merge(user);

         assertNotNull(updatedUser);
        assertEquals("Jane", updatedUser.getFirstName());
        assertEquals("Smith", updatedUser.getLastName());
    }


    @Test
    public void testUpdateUser_UserNotFound() {
         when(userDao.findByEmail(anyString())).thenReturn(null);

         assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(user);
        });
    }
}
