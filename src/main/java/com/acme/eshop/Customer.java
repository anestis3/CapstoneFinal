package com.acme.eshop;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Arrays;


import static java.lang.System.exit;

public class Customer {

    private static final Logger logger = LoggerFactory.getLogger(DBServer.class);
    private final Lorem generator = LoremIpsum.getInstance();

    public void createCustomerTable() {

        Statement ps;
        try {
            ps = DBConnection.getDBConnection().createStatement();

            int resultSet = ps.executeUpdate(DBConnection.getSQL("create.customer.001"));
            logger.debug("Customer table creation was successful with result " + resultSet);

        } catch (SQLException ex) {
            logger.error("Unable to create table customer", ex);
            exit(-1);
        }

    }

    public void insertCustomerTable() {

        PreparedStatement preparedStatement;
        try {
            preparedStatement = DBConnection.getDBConnection().prepareStatement(DBConnection.getSQL("insert.customer.000"));
            generateData(preparedStatement, 10);
            int[] affectedRows = preparedStatement.executeBatch();
            logger.debug("Insert commands into customer table were successful with {} row(s) affected.",
                    Arrays.stream(affectedRows).sum());
            selectCustomerTable();
        } catch (SQLException ex) {
            logger.error("unable to perform insert on batch mode for customer " + ex);
        }

    }

    private void generateData(PreparedStatement preparedStatement, int howMany) {

        for (int i = 1; i <= howMany; i++) {
            try {
                preparedStatement.setLong(1, 300 + i);
                preparedStatement.setString(2, generator.getFirstName());
                preparedStatement.setString(3, generator.getLastName());
                preparedStatement.setString(4,generator.getEmail() );
                preparedStatement.setString(5,generator.getPhone() );
                if (i < 3) {
                    preparedStatement.setString(6, "B2C");
                } else if (i < 7) {
                    preparedStatement.setString(6, "B2B");
                } else {
                    preparedStatement.setString(6, "B2G");
                }

                preparedStatement.addBatch();
            } catch (SQLException throwables) {
                logger.error("Unable to generate data for customer " + throwables);
            }
        }
    }

    public void selectCustomerTable(){

        Statement statement;
        try {
            statement = DBConnection.getDBConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(DBConnection.getSQL("select.customer.001"));
            while(resultSet.next()){
                logger.info("customer id = "+resultSet.getLong("CUSTOMER_ID")+
                        " ,first name= "+resultSet.getString("FIRSTNAME")
                        +" ,last name= "+resultSet.getString("LASTNAME")
                        +" ,email= "+resultSet.getString("EMAIL")
                        +" ,phone= "+resultSet.getString("PHONE")
                        +" ,category= "+resultSet.getString("CATEGORY"));
            }
        } catch (SQLException throwables) {
            logger.error("unable to perform selection for customer"+throwables);
        }
    }

    public String selectSingeCustomer(Integer cust_id) {

        String category = null;
        String sql = "SELECT CATEGORY FROM CUSTOMER WHERE CUSTOMER_ID = ? " ;

        try(PreparedStatement statement = DBConnection.getDBConnection().prepareStatement(sql)){
            statement.setInt(1,cust_id);

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                category = resultSet.getString("CATEGORY");}
            return category;
        } catch (SQLException throwable) {
            logger.error("Error occurred while retrieving data from customer", throwable);
        }
        return category;

    }

}
