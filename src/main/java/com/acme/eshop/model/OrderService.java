package com.acme.eshop.model;

import com.acme.eshop.app.Main;
import com.acme.eshop.database.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.acme.eshop.app.Main.customerService;
import static com.acme.eshop.app.Main.product;
import static com.acme.eshop.app.Main.orderItem;

public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final Order order = new Order();
    public OrderService(){

}    public Integer makeOrder(Integer product_id, int quantity, Integer cust_id, String method){

        Integer orderId = order.selectSingleOrder(cust_id);
        Integer neworderId = 0;

        if (orderId == 0) {
            if (product.selectProductAvailability(product_id) < quantity) {
                logger.info("The product is out of stock");
                logger.debug(String.valueOf(product.selectProductAvailability(product_id)));
               }
            else {
                String category = customerService.selectCustomerCategory(cust_id);
                Double amount = product.selectProductPrice(product_id);
                Double price = calculatePrice(method, amount, quantity, category);
                neworderId = order.insertOrder(orderId, cust_id, method, price);
                orderItem.insertOrderItem(neworderId, product_id, quantity);
                 }
        }
        else{
             logger.info("!!!the order id" + orderId + " is pending for customer " + cust_id);
             logger.info("!!!no new orders can be placed until it is completed");
             }
         return neworderId;
        }

    private Double calculatePrice(String method, Double amount, Integer quantity, String custCategory) {

        double initialAmount;
        double finalAmount;
        double discount1 = 0.00;
        double discount2 = 0.00;
        double totalDiscount;
        double discountPercentage;

        switch (method) {
            case "card":
                discount1 = 0.15;
                break;
            case "wire":
                discount1 = 0.10;
                break;
            case "cash":
                discount1 = 0.00;
                break;
        }

        switch (custCategory) {
            case "B2C":
                discount2 = 0.00;
                break;
            case "B2B":
                discount2 = 0.2;
                break;
            case "B2G":
                discount2 = 0.5;
                break;
        }

        initialAmount = (amount * quantity);
        totalDiscount = discount1 + discount2 ;
        discountPercentage = totalDiscount*100;
        finalAmount = (initialAmount) - (initialAmount*totalDiscount);
        logger.info("Initial cost is " + initialAmount);
        if (totalDiscount == 0.00){
            logger.info("There is no discount for this payment method and customer category");
        }
        else {
            logger.info("You are entitled to " + discountPercentage + "% discount, so the final cost is "+ finalAmount);
        }
        return finalAmount;

    }

    public boolean executeOrder(Integer newOrderId) {

        String sql = "UPDATE ORDERS SET ORDER_STATUS = 'COMPLETED' WHERE ORDER_ID = ?";

        try (PreparedStatement statement = DBConnection.getDBConnection().prepareStatement(sql)){
            statement.setInt(1,newOrderId);
            int resultRows = statement.executeUpdate();
            if(resultRows==1){
                return true;
            }
            else {
                return false;
            }
        } catch (SQLException ex) {
           logger.error("unable to update order status",ex);
        }
        return false;

    }

}
