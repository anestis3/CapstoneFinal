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
            logger.info("The list of the total number and cost of purchases per customer");
            while (resultSet.next()) {

                //@formatter:off
                logger.info("total number:{}, total amount:{}, customer:{}.",
                        resultSet.getLong("TOTAL"),
                        resultSet.getString("TSUM"),
                        resultSet.getString("CUSTOMER_ID"));
                //@formatter:on
            }
        } catch (SQLException throwables) {
            logger.error("Error occurred while retrieving data", throwables);
        }
    }

    public void getReportAvCost() {
        try (Statement statement = DBConnection.getDBConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(DBConnection.getSQL("select.report.002"))) {
            logger.info("The average order cost");
            while (resultSet.next()) {

                //@formatter:off
                logger.info("total average cost:{}.",
                        resultSet.getDouble(1));

                //@formatter:on
            }
        } catch (SQLException throwables) {
            logger.error("Error occurred while retrieving data", throwables);
        }
    }

    public void getReportAvPerCust() {
        try (Statement statement = DBConnection.getDBConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(DBConnection.getSQL("select.report.003"))) {
            logger.info("The average order cost per customer");
            while (resultSet.next()) {

                //@formatter:off
                logger.info("total average cost per customer:{}, customer:{}.",
                        resultSet.getDouble(1),
                        resultSet.getInt(2));

                //@formatter:on
            }
        } catch (SQLException throwables) {
            logger.error("Error occurred while retrieving data", throwables);
        }
    }
}