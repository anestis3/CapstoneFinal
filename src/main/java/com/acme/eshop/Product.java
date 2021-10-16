package com.acme.eshop;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Product {

    private static final Logger logger = LoggerFactory.getLogger(DBServer.class);
    private final Lorem generator = LoremIpsum.getInstance();

    public void createProductTable() {
        Statement ps;
        try {
            ps = DBConnection.getDBConnection().createStatement();

            int resultSet = ps.executeUpdate(DBConnection.getSQL("create.product.001"));
            logger.debug("table product created " + resultSet);

        } catch (SQLException ex) {
            logger.error("Unable to load table product",ex);
        }
    }

    public void insertProductTable() {

        PreparedStatement prepStat;

        try {
            prepStat = DBConnection.getDBConnection().prepareStatement(DBConnection.getSQL("insert.product.000"));
            generateData(prepStat, 10);
            int[] affectedRows = prepStat.executeBatch();
            logger.debug("Insert commands into product table were successful with {} row(s) affected.",
                    Arrays.stream(affectedRows).sum());
            selectProductTable();
        } catch (SQLException ex) {
            logger.error("unable to perform insert on batch mode for product " + ex);
        }

    }
    private void generateData(PreparedStatement prepStat, int howMany) {

        for (int i = 1; i <= howMany; i++) {
            try {
                prepStat.setInt(1, ThreadLocalRandom.current().nextInt(3505, 7170));
                prepStat.setInt(2, ThreadLocalRandom.current().nextInt(1, 30));
                if (i < 4) {
                    prepStat.setString(3, "COFFEE");
                    prepStat.setString(4, generator.getWords(2));
                    prepStat.setDouble(5, 10);
                } else if (i < 7) {
                    prepStat.setString(3, "BEER");
                    prepStat.setString(4, generator.getWords(2));
                    prepStat.setDouble(5, 20);
                } else {
                    prepStat.setString(3, "REFRESHMENT");
                    prepStat.setString(4, generator.getWords(2));
                    prepStat.setDouble(5, 15);
                }

                prepStat.addBatch();
            } catch (SQLException ex) {
                logger.error("Unable to generate data for product " + ex);
            }
        }
    }

    public void selectProductTable() {

        Statement statement;
        try {
            statement = DBConnection.getDBConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(DBConnection.getSQL("select.product.000"));
            while(resultSet.next()){
                logger.info("product id = "+resultSet.getInt("PRODUCT_ID")
                        +" ,product available items= "+resultSet.getInt("PRODUCT_AVAIL")
                        +" ,product category= "+resultSet.getString("PRODUCT_CATEGORY")
                        +" ,product description= "+resultSet.getString("PRODUCT_DESCRIPTION")
                        +" ,price= "+resultSet.getDouble("PRODUCT_PRICE"));

            }
        } catch (SQLException ex) {
            logger.error("unable to perform selection"+ex);
        }

    }

    public Double selectProductPrice(Integer pro_id) {

        Double price = 0.00;
        String sql = "SELECT PRODUCT_PRICE FROM PRODUCT WHERE PRODUCT_ID = ?";

        try (PreparedStatement statement = DBConnection.getDBConnection().prepareStatement(sql)){
            statement.setInt(1,pro_id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                logger.info("product price is " + String.valueOf(resultSet.getInt("PRODUCT_PRICE")));
                return resultSet.getDouble( "PRODUCT_PRICE");
            }
            else {
                return price;
            }
        } catch (SQLException ex) {
            logger.error("unable to perform selection to product price",ex);
        }
        return price;
    }

    public String selectProductCategory(Integer product_id) {

        String Name = "unknown";
        String sql = "SELECT PRODUCT_CATEGORY FROM PRODUCT WHERE PRODUCT_ID = ? " ;

        try(PreparedStatement statement = DBConnection.getDBConnection().prepareStatement(sql)){
            statement.setInt(1,product_id);

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Name = resultSet.getString("PRODUCT_CATEGORY");}

            return Name;
        } catch (SQLException ex) {
            logger.error("Error occurred while retrieving category from PRODUCT", ex);
        }
        return "unknown";
    }

    public Integer selectProductAvailability(Integer product_id) {

        Integer Avail = 0;
        String sql = "SELECT PRODUCT_AVAIL FROM PRODUCT WHERE PRODUCT_ID = ? " ;

        try(PreparedStatement statement = DBConnection.getDBConnection().prepareStatement(sql)){
            statement.setInt(1,product_id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Avail = resultSet.getInt("PRODUCT_AVAIL");}

            return Avail;
        } catch (SQLException ex) {
            logger.error("Error occurred while retrieving availability from PRODUCT", ex);
        }
        return 0;
    }

}