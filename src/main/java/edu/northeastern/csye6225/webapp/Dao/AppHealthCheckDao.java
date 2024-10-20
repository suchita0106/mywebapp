package edu.northeastern.csye6225.webapp.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;

@Repository
public class AppHealthCheckDao {
    @Autowired
    private DataSource dataSource;
    private static final Logger logger = LoggerFactory.getLogger(AppHealthCheckDao.class);

    public void checkDBConnection() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            logger.info("DB connection successful.");
        }
    }
}
