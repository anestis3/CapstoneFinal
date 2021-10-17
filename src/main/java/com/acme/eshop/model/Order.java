package com.acme.eshop.model;

import com.acme.eshop.database.DBConnection;
import com.acme.eshop.database.DBServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Order {

    private static final Logger logger = LoggerFactory.getLogger(DBServer.class);

    public Order(){

    }

    public void createOrderTable() {

        Statement ps;
        try {
            ps = DBConnection.getDBConnection().createStatement();

            int resultSet = ps.executeUpdate(DBConnection.getSQL("create.order.001"));
            logger.info("table order created " + resultSet);

        } catch (SQLException throwables) {
            logger.error("Unable to create table order", throwables);
        }

    }

    public void insertOrder(){

        Statement stm;
        int resultSet;
        try {
            stm = DBConnection.getDBConnection().createStatement();
            resultSet = stm.executeUpdate(DBConnection.getSQL("insert.order.001"));
        //  logger.debug("Result for insert order "+ resultSet);
            resultSet = stm.executeUpdate(DBConnection.getSQL("update.order.001"));
        //  logger.debug("Result for 1st update order "+ resultSet);
            resultSet = stm.executeUpdate(DBConnection.getSQL("insert.order.002"));
        //  logger.debug("Result for insert order "+ resultSet);
            resultSet = stm.executeUpdate(DBConnection.getSQL("update.order.002"));
        //  logger.debug("Result for 2nd update order "+ resultSet);
        } catch (SQLException throwables) {
            logger.error("unable to load Orders",throwables);
        }

    }

    public Integer insertOrder(Integer ord_id, Integer cust_id, String method, Double price){

        int newOrder =0;
        String sqlSelect = "SELECT MAX(ORDER_ID) FROM ORDERS";
        try(PreparedStatement ps = DBConnection.getDBConnection().prepareStatement(sqlSelect)){
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    logger.debug("max order_id is: "+ rs.getInt(1));
                    newOrder = rs.getInt(1);
                    newOrder = newOrder+1;
                }
            } catch (SQLException ex) {
                logger.error("unable to select max_id", ex);
            }

        String sql =
        "INSERT INTO ORDERS(ORDER_ID, CUSTOMER_ID, ORDER_STATUS, ORDER_METHOD, TOTAL_AMOUNT) "
                + "VALUES(?,?,?,?,?)";

        try (PreparedStatement statement = DBConnection.getDBConnection().prepareStatement(sql)) {

            statement.setInt(1, newOrder);
            statement.setInt(2, cust_id);
            statement.setString(3, "PENDING");
            statement.setString(4, method);
            statement.setDouble(5, price);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Insert orders failed, no rows affected.");
            }
            logger.info("affected rows " + affectedRows);

            return newOrder;
        } catch (SQLException throwables) {
            logger.error("Unable to insert rows to order", throwables);
        }
        return newOrder;
    }


    public Integer selectSingleOrder(Integer cust_id) {

        Integer orderId =0;
        String sql = "SELECT ORDER_ID FROM ORDERS WHERE CUSTOMER_ID = ? AND ORDER_STATUS = 'PENDING'" ;

        try(PreparedStatement statement = DBConnection.getDBConnection().prepareStatement(sql)){
                statement.setInt(1,cust_id);

                ResultSet resultSet = statement.executeQuery();
                if(resultSet.next()){
                orderId = resultSet.getInt("ORDER_ID");}

            return orderId;
        } catch (SQLException throwable) {
            logger.error("Error occurred while retrieving data from Orders", throwable);
        }
        return 0;
    }

    public void selectOrder(){
        Statement statement;
        try {
            statement = DBConnection.getDBConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(DBConnection.getSQL("select.order.001"));
            while(resultSet.next()){
                logger.info("order id = "+resultSet.getInt("ORDER_ID")+
                        " ,customer id= "+resultSet.getInt("CUSTOMER_ID")
                        +" ,order status= "+resultSet.getString("ORDER_STATUS")
                        +" ,order method= "+resultSet.getString("ORDER_METHOD"));

            }
        } catch (SQLException throwables) {
            logger.error("unable to perform selection to orders",throwables);
        }

    }

    public void selectSingleOrderWithId(Integer newOrderId) {

        String sql = "SELECT * FROM ORDERS WHERE ORDER_ID = ? " ;

        try(PreparedStatement statement = DBConnection.getDBConnection().prepareStatement(sql)){
            statement.setInt(1,newOrderId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                    //@formatter:off
                logger.info("orderid:{}, customer:{}, status:{}, method:{}, amount:{}.",
                        resultSet.getInt("ORDER_ID"),
                        resultSet.getInt("CUSTOMER_ID"),
                        resultSet.getString("ORDER_STATUS"),
                        resultSet.getString("ORDER_METHOD"),
                        resultSet.getDouble("TOTAL_AMOUNT"));
                    //@formatter:on

                }
        } catch (SQLException throwable) {
            logger.error("Error occurred while retrieving data from Orders", throwable);
        }

    }



}
