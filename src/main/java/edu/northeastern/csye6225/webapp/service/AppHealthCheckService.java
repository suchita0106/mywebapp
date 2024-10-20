package edu.northeastern.csye6225.webapp.service;
import edu.northeastern.csye6225.webapp.Dao.AppHealthCheckDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AppHealthCheckService {

    @Autowired
    private AppHealthCheckDao appHealthCheckDAO;
    private static final Logger logger = LoggerFactory.getLogger(AppHealthCheckService.class);

    public boolean checkDatabaseConnection() {
        logger.info("Checking DB connection");
        try {
            appHealthCheckDAO.checkDBConnection();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}


