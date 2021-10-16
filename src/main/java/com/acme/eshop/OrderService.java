package com.acme.eshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import static com.acme.eshop.Eshop.customer;
import static com.acme.eshop.Eshop.customerService;
import static com.acme.eshop.Eshop.product;
import static com.acme.eshop.Eshop.orderItem;

public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(Eshop.class);
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

        Double initialAmount = 0.00;
        Double finalAmount = 0.00;
        Double discount1 = 0.00;
        Double discount2 = 0.00;
        Double totalDiscount = 0.00;
        Double discountPercentage = 0.00;

        if (method.equals("card")) {
            discount1 = 0.15;
        }
        else if (method.equals("wire")) {
            discount1 = 0.10;
        }
        else if (method.equals("cash")) {
            discount1 = 0.00;
        }

        if (custCategory=="B2C") {
            discount2 = 0.00;
        }
        else if (custCategory == "B2B") {
            discount2 = 0.2;
        }
        else if (custCategory == "B2G") {
            discount2 = 0.5;
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
            logger.info("You are entitled for " + discountPercentage + "% discount, so the final cost is "+ finalAmount);
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
