package com.acme.eshop.app;

import com.acme.eshop.database.DBServer;
import com.acme.eshop.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.System.exit;

public class Main {

    public static Main eshop = new Main();
    public static Customer customer =new Customer();
    public static Product product = new Product();
    public static Order order = new Order();
    public static OrderItem orderItem = new OrderItem();
    public static OrderService orderService = new OrderService();
    public static CustomerService customerService = new CustomerService();
    public static ReportService reportService = new ReportService();

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static String methodos;
    private static int custnoInt;
    private static int prodnoInt;
    private static int quantnoInt;

    public static void main(String[] args) {

        DBServer dbServer = new DBServer();

        dbServer.startServer();

        createTables();
        getInputdata();

        Integer newOrderId = orderService.makeOrder(prodnoInt, quantnoInt, custnoInt, methodos);
        if (newOrderId>0){
                    if(orderService.executeOrder(newOrderId)){
                        order.selectSingleOrderWithId(newOrderId);
                        orderItem.selectSingleOrderItemWithId(newOrderId);
                        int oldAvail = product.selectProductAvailability(prodnoInt);
                        product.updateProductAvailability(prodnoInt, oldAvail - quantnoInt);
                        logger.info("Your order no " + newOrderId + " has been completed");
                        logger.info("Product "+prodnoInt +" new availability is "+ product.selectProductAvailability(prodnoInt));
            //            DBConnection.getDBConnection().commit();
                    }
                    else {
                        System.out.println("An error has occurred - order not completed");
                    }
                }

        GetReports();


        dbServer.stopServer();

    }

    private static void getInputdata() {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            logger.info("Please insert your customer_id ( choose a number between 301 and 310)");
            String custno = br.readLine();
            System.out.println(custno);

            if (custno.equals("301") | custno.equals("302") | custno.equals("303") | custno.equals("304") |
                custno.equals("305") | custno.equals("306") | custno.equals("307") | custno.equals("308") |
                custno.equals("309") | custno.equals("310") )
            {
                custnoInt =Integer.parseInt(custno);
                String currentCustomer = customerService.selectCustomerName(custnoInt);
                logger.info("Hello " + currentCustomer);
                Integer orderId = order.selectSingleOrder(custnoInt);
                if (orderId > 0) {
                    logger.info("Sorry but you have a pending order ");
                    exit(-1);
                }
            }
            else {
                System.out.println("invalid customer");
                exit(-1);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        try {
            logger.info("Please choose the desired product id (choose from the above list)");
            String prodno = br.readLine();
            prodnoInt =Integer.parseInt(prodno);
            String currentProd = product.selectProductCategory(prodnoInt);
            logger.info("You have chosen product id " + prodno + " from category " + currentProd);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        try {
            Integer currentAvail = product.selectProductAvailability(prodnoInt);
            logger.info("Please choose the desired quantity. Currently there are "+currentAvail + " available items");
            String quantno = br.readLine();
            quantnoInt =Integer.parseInt(quantno);
            while (quantnoInt > currentAvail) {
                logger.info("Please choose " + currentAvail + " or less items");
                quantno = br.readLine();
                quantnoInt = Integer.parseInt(quantno);
            }
            System.out.println("You have selected " + quantno + " items");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        try  {
            logger.info("Please type 'cash' or 'card' depending on your preferred payment method");
            methodos = br.readLine();
            System.out.println(methodos);

            if (methodos.equals("card") | methodos.equals("cash")) {
                    logger.info("You have chosen "+ methodos + " payment method");
                }
                else {
                    logger.info("invalid payment method");
                    exit(-1);
                }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

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

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try  {
            logger.info("Please type 'r' to proceed with the reporting or anything else to exit");
            String report = br.readLine();

            if (report.equals("r") ) {
                br.close();
                logger.info("Processing reporting");
                reportService.getReportPerCust();
                reportService.getReportAverageCost();
                reportService.getReportAveragePerCust();
            }
            else {
                exit(-1);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

}
