package edu.northeastern.csye6225.webapp.controller;

import edu.northeastern.csye6225.webapp.service.AppHealthCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/healthz")
public class AppHealthCheckController {

    private static final Logger logger = LoggerFactory.getLogger(AppHealthCheckController.class);

    @Autowired
    private AppHealthCheckService appHealthCheckService;

    //  GET requests
    @GetMapping
    public ResponseEntity<Void> healthCheck(HttpServletRequest request) {
        logger.info("GET request on /healthz");

        // payload in the request body
        if (request.getContentLength() > 0) {
            logger.error("Request contains payload, returning 400 Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("X-Content-Type-Options", "nosniff")
                    .build();
        }

        if(request.getQueryString() != null){
            logger.error("Request contains query string");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("X-Content-Type-Options", "nosniff")
                    .build();
        }

        // database connection
        if (appHealthCheckService.checkDatabaseConnection()) {
            logger.info("Database connection successful, returning 200 OK");
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("X-Content-Type-Options", "nosniff")
                    .build();
        } else {
            logger.error("Database connection failed, returning 503 Service Unavailable");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("X-Content-Type-Options", "nosniff")
                    .build();
        }
    }

    // HTTP methods
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.OPTIONS, RequestMethod.HEAD, RequestMethod.TRACE})
    public ResponseEntity<Void> handleUnsupportedMethods(HttpServletRequest request) {
        logger.warn("Received unsupported {} request", request.getMethod());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff")
                .build();
    }
}
