package edu.northeastern.csye6225.webapp.service;
import edu.northeastern.csye6225.webapp.Dao.UserDao;
import edu.northeastern.csye6225.webapp.model.User;
import edu.northeastern.csye6225.webapp.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username);
        if (user == null) {
            System.out.println("User Not Found");
            throw new UsernameNotFoundException("user not found");
        }
        System.out.println("User Found: " + user.getEmail());
        return new UserPrincipal(user);
    }
}