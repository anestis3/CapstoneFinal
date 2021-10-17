package com.acme.eshop.model;

import com.acme.eshop.database.DBConnection;
import com.acme.eshop.database.DBServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(DBServer.class);

    public ReportService() {

    }


    public void getReportPerCust() {

        try (Statement statement = DBConnection.getDBConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(DBConnection.getSQL("select.report.001"))) {
            logger.info("Report of the total number and cost of purchases per customer");
            while (resultSet.next()) {

                //@formatter:off
                logger.info("customer:{} has made {} order(s) of {} total amount.",
                        resultSet.getString("CUSTOMER_ID"),
                        resultSet.getLong("TOTAL_NUMBER"),
                        resultSet.getString("SUM_AMOUNT"));
                //@formatter:on
            }
        } catch (SQLException throwables) {
            logger.error("Error occurred while retrieving data", throwables);
        }
    }

    public void getReportAverageCost() {
        try (Statement statement = DBConnection.getDBConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(DBConnection.getSQL("select.report.002"))) {
            logger.info("Report with the average order cost");
            while (resultSet.next()) {

                //@formatter:off
                logger.info("total average cost is {} (for {} total orders)",
                        resultSet.getDouble(1),
                        resultSet.getLong("TOTAL_NUMBER"));

                //@formatter:on
            }
        } catch (SQLException throwables) {
            logger.error("Error occurred while retrieving data", throwables);
        }
    }

    public void getReportAveragePerCust() {
        try (Statement statement = DBConnection.getDBConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(DBConnection.getSQL("select.report.003"))) {
            logger.info("Report with the average order cost per customer");
            while (resultSet.next()) {

                //@formatter:off
                logger.info("average cost for customer {} is:{}.",
                        resultSet.getDouble(1),
                        resultSet.getInt(2));

                //@formatter:on
            }
        } catch (SQLException throwables) {
            logger.error("Error occurred while retrieving data", throwables);
        }
    }
}