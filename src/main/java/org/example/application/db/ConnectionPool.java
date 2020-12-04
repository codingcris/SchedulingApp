package org.example.application.db;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for managing database connections in a pool.
 */
public class ConnectionPool {
     public static class ConnectionsUnavailableException extends Exception {
        public ConnectionsUnavailableException(String message) {
            super(message);
        }
    }

    private static final int DEFAULT_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static int poolSize;

    private final String connectionUsername;
    private final String connectionPassword;
    private final List<Connection> connectionPool;
    private final List<Connection> usedConnections = new ArrayList<>();

    /**
     * Private constructor initializes various fields.
     * @param url database url.
     * @param username database username.
     * @param password database password.
     * @param pool database connections in a pool.
     */
    private ConnectionPool(String url, String username, String password, List<Connection> pool) {
        connectionUsername = username;
        connectionPassword = password;
        connectionPool = pool;
    }

    /**
     *  Static factory method returns a new instance of ConnectionPool with connections ready.
     * @param url database url
     * @param username database username.
     * @param password database password.
     * @return ConnectionPool instance with connections ready.
     */
    public static ConnectionPool create(String url, String username, String password) {
        List<Connection> pool = new ArrayList<>(poolSize);
        for(int i = 0; i < poolSize; i++) {
            try {
                pool.add(createConnection(url, username, password));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (pool.size() == 0) {
            return null;
        }

        return new ConnectionPool(url, username, password, pool);

    }

    /**
     *  Static factory method returns a new instance of ConnectionPool with the specified number of connections ready for use.
     *  Falls back on the default number of connections (see DEFAULT_POOL_SIZE final static variable)
     *  if given a number less than 1 or more than the max number of connections (see MAX_POOL_SIZE final static variable).
     * @param url database url.
     * @param username database username.
     * @param password database password.
     * @param poolsize number of connections in pool.
     * @return ConnectionPool instance with connections ready.
     */
    public static ConnectionPool create(String url, String username, String password, int poolsize) {
        if (poolsize > 0 && poolsize < MAX_POOL_SIZE)
            poolSize = poolsize;

        ConnectionPool cp = ConnectionPool.create(url, username, password);
        poolSize = DEFAULT_POOL_SIZE;
        return cp;
    }


    /**
     * Returns a ready to use connection from the connection pool and removes it from the  connection pool.
     * @return ready to use database Connection from the connection pool.
     */
    public Connection getConnection() throws ConnectionsUnavailableException{
        Connection conn = null;

        if (connectionPool.size() > 0) {
            conn = connectionPool.remove(0);
            usedConnections.add(conn);
        } else {
            throw new ConnectionsUnavailableException("No database connections available.");
        }

        return conn;
    }

    /**
     * Recieves a database connection and adds it to the connection pool if it was previously retrieved from the connection pool.
     * @param conn database connection to be returned to the connection pool.
     */
    public void releaseConnection(Connection conn) {
        if (usedConnections.remove(conn)) {
            connectionPool.add(conn);
        }
    }

    /**
     * Closes all database connections in the connection pool as well as the database connections currently in use from the pool.
     */
    public void close() {
        for (Connection c : connectionPool) {
            try {
                c.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        for (Connection c : usedConnections) {
            try {
                c.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /**
     * private static method for creating database connections.
     * @param url database url.
     * @param username database username.
     * @param password database password.
     * @throws SQLException if database connection fails to be established.
     */
    private static Connection createConnection(String url, String username, String password) throws SQLException {
        return DriverManager.getConnection(url,username,password);
    }
}
