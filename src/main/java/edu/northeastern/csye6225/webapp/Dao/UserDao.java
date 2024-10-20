package edu.northeastern.csye6225.webapp.Dao;


import edu.northeastern.csye6225.webapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    User findByEmail(String username);
}
