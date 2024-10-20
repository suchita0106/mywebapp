package edu.northeastern.csye6225.webapp.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import edu.northeastern.csye6225.webapp.service.AppHealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Component
public class GetRequestAuthFilter implements Filter {
    @Autowired
    private AppHealthCheckService appHealthCheckService;

    // Define the allowed HTTP methods
    private static final List<String> ALLOWED_METHODS = Arrays.asList("GET", "POST", "PUT");

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request,
                         jakarta.servlet.ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        // Convert the request and response objects to Http versions
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // Check if the method is unsupported
        if (!ALLOWED_METHODS.contains(method)) {
            httpResponse.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED); // 405
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            String jsonMessage = "{\"message\": \"HTTP method " + method + " is not supported.\"}";
            httpResponse.getWriter().write(jsonMessage);
            return; // Stop further processing
        }


        if ("HEAD".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            // Return 405 Method Not Allowed
            httpResponse.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        if ("/healthz".equals(requestURI) && ("GET".equalsIgnoreCase(httpRequest.getMethod()))) {
            // Check if the Authorization header is present
            String authorizationHeader = httpRequest.getHeader("Authorization");

            if (authorizationHeader != null) {
                // If Authorization header is present, return 400 Bad Request or 405 Method Not Allowed
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST); // You can use SC_BAD_REQUEST (400) instead

                httpResponse.setContentType("application/json");
                httpResponse.setCharacterEncoding("UTF-8");
                String jsonMessage = "{\"message\": \"healthz request with Authorization header is not required.\"}";
                httpResponse.getWriter().write(jsonMessage);
                return; // Stop further processing
            }else{
                chain.doFilter(request, response);
                return;
            }
        }

        if ("/healthz".equals(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        // Check if the request is a POST request
        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
            // Check if the Authorization header is present
            String authorizationHeader = httpRequest.getHeader("Authorization");

            if (authorizationHeader != null) {
                // If Authorization header is present, return 400 Bad Request or 405 Method Not Allowed
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST); // You can use SC_BAD_REQUEST (400) instead

                httpResponse.setContentType("application/json");
                httpResponse.setCharacterEncoding("UTF-8");
                String jsonMessage = "{\"message\": \"POST request with Authorization header is not required.\"}";


                httpResponse.getWriter().write(jsonMessage);

                return; // Stop further processing
            }
        }

        if ("GET".equalsIgnoreCase(httpRequest.getMethod())) {
            // Check if the Authorization header is present
            String authorizationHeader = httpRequest.getHeader("Authorization");

            if (authorizationHeader == null) {
                // If Authorization header is present, return 400 Bad Request or 405 Method Not Allowed
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST); // You can use SC_BAD_REQUEST (400) instead

                httpResponse.setContentType("application/json");
                httpResponse.setCharacterEncoding("UTF-8");
                String jsonMessage = "{\"message\": \"GET request requires Authorization header.\"}";


                httpResponse.getWriter().write(jsonMessage);

                return; // Stop further processing
            }
        }

        if ("PUT".equalsIgnoreCase(httpRequest.getMethod())) {
            // Check if the Authorization header is present
            String authorizationHeader = httpRequest.getHeader("Authorization");

            if (authorizationHeader == null) {
                // If Authorization header is present, return 400 Bad Request or 405 Method Not Allowed
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST); // You can use SC_BAD_REQUEST (400) instead

                httpResponse.setContentType("application/json");
                httpResponse.setCharacterEncoding("UTF-8");
                String jsonMessage = "{\"message\": \"PUT request requires Authorization header.\"}";


                httpResponse.getWriter().write(jsonMessage);

                return; // Stop further processing
            }
        }

        // Lazy loading of the AppHealthCheckService
        if (appHealthCheckService == null) {
            WebApplicationContext webApplicationContext = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(httpRequest.getServletContext());
            appHealthCheckService = webApplicationContext.getBean(AppHealthCheckService.class);
        }

        // Check if the database connection is available
        if (!appHealthCheckService.checkDatabaseConnection()) {
            httpResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            String jsonMessage = "{\"message\": \"Service unavailable due to database connection issue.\"}";
            httpResponse.getWriter().write(jsonMessage);
            return;
        }


        // Continue with the rest of the filter chain if conditions are not met
        chain.doFilter(request, response);
    }

//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        // Initialization code if needed
//    }
//
//    @Override
//    public void destroy() {
//        // Cleanup code if needed
//    }
}

