package com.acme.eshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import static java.lang.System.exit;

public class Eshop {

    public static Eshop eshop = new Eshop();
    public static Customer customer =new Customer();
    public static Product product = new Product();
    public static Order order = new Order();
    public static OrderItem orderItem = new OrderItem();
    public static OrderService orderService = new OrderService();
    public static ReportService reportService = new ReportService();

    private static final Logger logger = LoggerFactory.getLogger(Eshop.class);

    public static void main(String[] args) {

        DBServer dbServer = new DBServer();

        dbServer.startServer();

        createTables();

        Integer newOrderId = orderService.placeOrder(1200, 2, 1008, "CASH");
        if (newOrderId>0){
            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                System.out.println("Please press 'Y' to complete your purchase or 'N' to cancel it");
                String answer = br.readLine();
                System.out.println(answer);

                if (answer.equals("y") | answer.equals("Y")) {
                    if(orderService.executeOrder(newOrderId)){
                        order.selectSingleOrderWithId(newOrderId);
                        System.out.println("Your order has been completed");
                        DBConnection.getDBConnection().commit();
                    }
                    else {
                        System.out.println("An error has occurred - order not completed");
                    }
                }
                else if (answer.equals("n") | answer.equals("N")) {
                    System.out.println("We hope to serve you another time");
                }
                else {
                    System.out.println("You have entered an invalid value");
                }
            } catch (IOException | SQLException ioException) {
                ioException.printStackTrace();
            }
        }

        GetReports();


        dbServer.stopServer();

    }

    private static void createTables() {

        logger.info("Customer Table creation");
        customer.createCustomerTable();
        logger.info("Customer Table loading");
        customer.insertCustomerTable();

        logger.info("Product Table creation");
        product.createProductTable();
        logger.info("Product Table loading");
        product.insertProductTable();

        logger.info("Order Table creation");
        order.createOrderTable();
        logger.info("OrderItem Table creation");
        orderItem.createOrderItemTable();

        logger.info("OrderItem Table loading");
        orderItem.loadOrderItemTable();

        logger.info("Order Table loading");
        order.insertOrder();
        order.selectOrder();
        orderItem.selectOrderitem();

    }

    private static void GetReports() {

        logger.info("Reporting...........");
        reportService.getReportPerCust();
        reportService.getReportAvCost();
        reportService.getReportAvPerCust();


    }

}
