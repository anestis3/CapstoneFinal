package com.acme.eshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static java.lang.System.exit;

public class DBConnection {

    private static final String DB_URL = "jdbc:h2:mem:sample" ;
    private static final String DB_USERNAME = "sa";
    private static final String DB_PASSWORD = "";

    private static Logger logger = LoggerFactory.getLogger(DBConnection.class);
    public static Properties sqlCommands = new Properties();

    public DBConnection(){

    }
    private void loadSqlCommands() {
        try (InputStream inputStream = DBConnection.class.getClassLoader()
                .getResourceAsStream("sql.properties")) {
            if (inputStream == null) {
                logger.error("Sorry, unable to find sql.properties, exiting application.");
                // Abnormal exit
                exit(-1);
            }

            //load a properties file from class path, inside static method
            sqlCommands.load(inputStream);
        } catch (IOException ex) {
            logger.error("Sorry, unable to parse sql.properties, exiting application.", ex);
            // Abnormal exit
            exit(-1);
        }
    }

    public static Connection getDBConnection(){

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            logger.info("A connection has been established ");
        } catch (SQLException throwables) {
            logger.error("Unable to set up a connection"+throwables);
        }
        return connection;


    }

     public static String getSQL(String key){

         try (InputStream inputStream = DBServer.class.getClassLoader().getResourceAsStream("sql.properties")) {
             if (inputStream == null) {
                 logger.error("Unable to load SQL commands.");
                 exit(-1);
             }
             sqlCommands.load(inputStream);
         } catch (IOException e) {
             logger.error("Error while loading SQL commands.", e);
         }

        return sqlCommands.getProperty(key);

    }
}
