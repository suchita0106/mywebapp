package edu.northeastern.csye6225.webapp.controller;
import edu.northeastern.csye6225.webapp.model.User;
import edu.northeastern.csye6225.webapp.service.UserService;
import edu.northeastern.csye6225.webapp.dto.UserDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/register",  produces = "application/json")
    public ResponseEntity<Map<String, String>>  registerUser(@RequestBody @Valid User user, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request)  {

        Map<String, String> response = new HashMap<>();
        if ( request.getQueryString() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);  // Return 400 Bad Request
        }

        if (user.getAccountCreated() != null || user.getAccountUpdated() != null) {
            response.put("error", "Fields 'accountCreated' and 'accountUpdated' cannot be provided in the request.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
        try {

            userService.createUser(user);
            //return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
            response.put("message", "User created successfully");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }

    @GetMapping("/self")
    public ResponseEntity<UserDTO> getUser(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {

        // Check if there's any query parameter or request body
        if (request.getContentLength() > 0 || request.getQueryString() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);  // Return 400 Bad Request
        }

        User user = userService.findByEmail(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        // Convert the User entity to a UserDTO and return it
        UserDTO userDTO = userService.convertToDTO(user);
        return ResponseEntity.ok()
                .body(userDTO);
    }


    @PutMapping(value = "/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDTO> updateUser(@RequestBody User user, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {

        if ( request.getQueryString() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);  // Return 400 Bad Request
        }

        if (user.getAccountCreated() != null || user.getAccountUpdated() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        // Ensure the user is authenticated
        if (!userDetails.getUsername().equals(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);  // User can only update their own information
        }

        User updatedUser = userService.updateUser(user);
        UserDTO updatedUserDTO = userService.convertToDTO(updatedUser);

        return ResponseEntity.ok()
                .body(updatedUserDTO);
    }

    @RequestMapping(value = {"/", "/**","/self"}, method = {RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.OPTIONS, RequestMethod.HEAD, RequestMethod.TRACE})
    public ResponseEntity<Void> handleUnsupportedMethods(HttpServletRequest request) {
        // logger.warn("Received unsupported {} request", request.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .build();
    }


}
