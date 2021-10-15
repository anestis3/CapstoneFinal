package com.acme.eshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import static com.acme.eshop.Eshop.customer;
import static com.acme.eshop.Eshop.product;
import static com.acme.eshop.Eshop.orderItem;

public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(Eshop.class);
    private final Order order = new Order();
    public OrderService(){

}    public Integer placeOrder(Integer pro_id, int qnty, Integer cust_id, String method){

        Integer orderId = order.selectSingleOrder(cust_id);
        Integer neworderId = 0;

        logger.debug("order id: ", String.valueOf(order.selectSingleOrder(cust_id)), String.valueOf(orderId));
 //   System.out.println("order id: "+ String.valueOf(order.selectSingleOrder(cust_id))+ String.valueOf(orderId));

   if (orderId == 0) {
        if (product.selectProductQnty(pro_id) < qnty) {
            logger.info("The product is out of stock");
            logger.debug(String.valueOf(product.selectProductQnty(pro_id)));
        }
            else{
                String category = customer.selectSingeCustomer(cust_id);
                Double amount = product.selectProductPrice(pro_id);
                Double price = calculatePrice(method, amount, qnty, category);
                neworderId = order.insertOrder(orderId, cust_id, method, price);

              orderItem.insertOrderItem(neworderId, pro_id, qnty);
        }
    }
    else{
        logger.info("the customer has an order pending");
    }
    return neworderId;
}

    private Double calculatePrice(String method, Double amount, Integer quantity, String custCategory) {

        Double total_amount = 0.00;
        if (method=="CASH") {
            total_amount = (amount * quantity) - (amount*quantity)*0.1;

        }
        else {
            total_amount = (amount * quantity) - (amount*quantity)*0.15;

        }
        if (custCategory=="B2B") {
            total_amount = total_amount - (total_amount)*0.2;

        }
        else if (custCategory == "B2G"){
            total_amount = total_amount - total_amount*0.5;

        }
        return total_amount;

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
