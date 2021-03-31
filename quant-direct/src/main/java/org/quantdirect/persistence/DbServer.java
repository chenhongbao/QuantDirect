package org.quantdirect.persistence;

import org.h2.tools.Server;
import org.quantdirect.tools.LOG;

import java.sql.SQLException;

public class DbServer {
    private static Server tcp;
    private static Server web;

    private DbServer() {}

    public static synchronized void start() {
        if (tcp != null && tcp.isRunning(true)) {
            return;
        }
        try {
            startTcp();
            startWeb();
        } catch (SQLException throwable) {
            throw new Error("Can't start database TCP server.", throwable);
        }
    }

    private static void startWeb() throws SQLException {
        web = Server.createWebServer("-trace").start();
        LOG.write("Database WEB server is at: " + web.getURL() + ".", web);
    }

    public static synchronized void stop() {
        web.stop();
        tcp.stop();
    }

    private static void startTcp() throws SQLException {
        tcp = Server.createTcpServer("-tcpPassword", "123456").start();
        LOG.write("Database TCP server is at: " + tcp.getURL() + ".", tcp);
    }
}
