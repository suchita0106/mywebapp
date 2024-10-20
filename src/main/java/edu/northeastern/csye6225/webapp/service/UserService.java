package edu.northeastern.csye6225.webapp.service;

import edu.northeastern.csye6225.webapp.Dao.UserDao;
import edu.northeastern.csye6225.webapp.controller.AppHealthCheckController;
import edu.northeastern.csye6225.webapp.dto.UserDTO;
import edu.northeastern.csye6225.webapp.exception.ResourceNotFoundException;
import edu.northeastern.csye6225.webapp.model.User;
import edu.northeastern.csye6225.webapp.model.UserPrincipal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Service
public class UserService {

    @PersistenceContext
    private EntityManager entityManager;  // EntityManager inject

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDao userDao;

    private static final Logger logger = LoggerFactory.getLogger(AppHealthCheckController.class);

    @Transactional
    public User createUser(User user) {

        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        logger.info("User does not exist");

        // Password BCrypt
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setAccountCreated(new Date());
        user.setAccountUpdated(new Date());


        entityManager.persist(user);
        return user;
    }

    // Custom method using EntityManager to check if an email exists
    public boolean existsByEmail(String email) {
        logger.info("Check if user already exists in the system.");

        Long count = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    // Method to find User by email using EntityManager
    public User findByEmail(String email) {
        try {
            return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // User not found
        }
    }

    @Transactional
    public User updateUser(User user) {
        // Find the existing user by email
        User existingUser = userDao.findByEmail(user.getEmail());
        if (existingUser == null) {
            throw new ResourceNotFoundException("User not found with email: " + user.getEmail());
        }

         if (!existingUser.getEmail().equals(user.getEmail())) {

            User userWithSameEmail = userDao.findByEmail(user.getEmail());
            if (userWithSameEmail != null) {

                throw new IllegalArgumentException("Email already exists in the system");
            }
        }

        // Update user details
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Update the accountUpdated field or other fields if needed
        existingUser.setAccountUpdated(new Date());

        // Merge the changes
        return entityManager.merge(existingUser);
    }



    // Method to delete User by email using EntityManager
    @Transactional
    public void deleteUserByEmail(String email) {
        User user = findByEmail(email);
        if (user != null) {
            entityManager.remove(user);  // User entity delete
        }
    }


    public UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAccountCreated(),
                user.getAccountUpdated()
        );
    }
}
