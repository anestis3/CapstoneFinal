package com.acme.eshop;

import com.acme.eshop.app.Main;
import com.acme.eshop.database.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public String selectCustomerName(Integer cust_id) {

        String Name = "unknown";
        String sql = "SELECT FIRSTNAME FROM CUSTOMER WHERE CUSTOMER_ID = ? " ;

        try(PreparedStatement statement = DBConnection.getDBConnection().prepareStatement(sql)){
            statement.setInt(1,cust_id);

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Name = resultSet.getString("FIRSTNAME");}

            return Name;
        } catch (SQLException ex) {
            logger.error("Error occurred while retrieving data from CUSTOMER", ex);
        }
        return "unknown";
    }

    public String selectCustomerCategory(Integer cust_id) {

        String category = null;
        String sql = "SELECT CATEGORY FROM CUSTOMER WHERE CUSTOMER_ID = ? " ;

        try(PreparedStatement statement = DBConnection.getDBConnection().prepareStatement(sql)){
            statement.setInt(1,cust_id);

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                category = resultSet.getString("CATEGORY");}
            return category;
        } catch (SQLException ex) {
            logger.error("Error occurred while retrieving data from customer", ex);
        }
        return category;

    }

}
