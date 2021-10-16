package com.acme.eshop.database;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static java.lang.System.exit;

public class DBServer {

    private Server h2Server;
    private static final Logger logger = LoggerFactory.getLogger(DBServer.class);

    public void startServer() {
        try {
            h2Server = Server.createTcpServer("-tcpAllowOthers", "-tcpDaemon");
            h2Server.start();
            logger.info("Database server is now accepting connections.");
        } catch (SQLException throwables) {
            logger.error("Unable to start database server.", throwables);
            exit(-1);
        }
        logger.info("Server has started with status '{}'.", h2Server.getStatus());
    }

    public void stopServer() {
        if (h2Server == null) {
            return;
        }

        if (h2Server.isRunning(true)) {
            h2Server.stop();
            h2Server.shutdown();
        }
        logger.info("Database server has been shutdown.");

    }


}
