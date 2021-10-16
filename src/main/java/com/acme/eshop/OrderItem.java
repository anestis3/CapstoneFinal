package com.acme.eshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderItem {

    private static final Logger logger = LoggerFactory.getLogger(DBServer.class);

    public void createOrderItemTable() {

        Statement ps;
        try {
            ps = DBConnection.getDBConnection().createStatement();

            int resultSet = ps.executeUpdate(DBConnection.getSQL("create.orderitem.001"));
            logger.debug("table product created " + resultSet);

        } catch (SQLException ex) {
            logger.error("Unable to create table order_item", ex);
        }
    }

    public void loadOrderItemTable(){
        Statement stm;
        int resultSet;
        try {
            stm = DBConnection.getDBConnection().createStatement();
            resultSet = stm.executeUpdate(DBConnection.getSQL("insert.orderitem.001"));
      //    logger.debug("Result for 1st insert order item"+ resultSet);
            resultSet = stm.executeUpdate(DBConnection.getSQL("update.orderitem.001"));
      //    logger.debug("Result for 1st update order item"+ resultSet);
            resultSet = stm.executeUpdate(DBConnection.getSQL("insert.orderitem.002"));
      //    logger.debug("Result for 2st insert order item"+resultSet);
            resultSet = stm.executeUpdate(DBConnection.getSQL("update.orderitem.002"));
      //    logger.debug("Result for 2nd update order item"+ resultSet);
        } catch (SQLException ex) {
            logger.error("unable to load table order_item",ex);
        }
    }

    public void insertOrderItem(Integer order_id, Integer pro_id, int qnty) {

        String sql =
                "INSERT INTO ORDER_ITEM(ORDER_ID, PRODUCT_ID, PRODUCT_QNTY) "
                        + "VALUES(?,?, ?)";
        try (PreparedStatement statement = DBConnection.getDBConnection().prepareStatement(sql)){
            statement.setLong(1,order_id);
            statement.setLong(2,pro_id);
            statement.setLong(3,qnty);

            int rowAffected = statement.executeUpdate();
            if (rowAffected == 0) {
                logger.error("Insert order_item failed.");
            }

        } catch (SQLException ex) {
            logger.error("unable to insert row to order_item",ex);
        }

    }
    public void selectOrderitem(){
        Statement statement = null;
        try {
            statement = DBConnection.getDBConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(DBConnection.getSQL("select.orderitem.001"));
            while(resultSet.next()){
                logger.info("order id = "+resultSet.getInt("ORDER_ID")+
                        " ,product id= "+resultSet.getInt("PRODUCT_ID")
                        +" ,product_qnty= "+resultSet.getInt("PRODUCT_QNTY"));
                               }
        } catch (SQLException ex) {
            logger.error("unable to perform selection to orders",ex);
        }

    }

    public void selectSingleOrderItemWithId(Integer newOrderId) {

        String sql = "SELECT * FROM ORDER_ITEM WHERE ORDER_ID = ? " ;

        try(PreparedStatement statement = DBConnection.getDBConnection().prepareStatement(sql)){
            statement.setInt(1,newOrderId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                logger.info("orderid:{}, productid:{}, productqnty:{}.",
                resultSet.getInt("ORDER_ID"),
                resultSet.getInt("PRODUCT_ID"),
                resultSet.getString("PRODUCT_QNTY"));
                            }
        } catch (SQLException throwable) {
            logger.error("Error occurred while retrieving data from Orders", throwable);
        }

    }
}
