package org.example.application.users;

import javafx.application.Application;
import org.example.App;
import org.example.application.db.ConnectionPool;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Concrete implementation of the UserDao interface. Handles database access of User objects.
 */
public class UserDaoImpl implements UserDAO{

    private final String dbUrl = "jdbc:mysql://wgudb.ucertify.com/WJ07mxm";
    private final String dbUsername = "U07mxm";
    private final String dbPassword = "53689073251";
    private final ConnectionPool dataSource;

    /**
     * Constructor.
     * @throws ConnectionPool.ConnectionsUnavailableException If the connection pool object
     * passed as an argument is null (usually from a misconfigured ConnectionPool initialization) this exception is thrown.
     */
    public UserDaoImpl() throws ConnectionPool.ConnectionsUnavailableException {
        dataSource = ConnectionPool.create(dbUrl, dbUsername, dbPassword, 1);
        if (dataSource == null) {
            throw new ConnectionPool.ConnectionsUnavailableException(App.resources.getString("databaseConnectionsError"));
        }
    }

    /**
     * Searches for the user in database with the passed in username and returns a User object with this user's data.
     * @param username username to search for
     * @return User object if user is found, null if otherwise.
     */
    @Override
    public User getUser(String username) {
        Connection conn = null;
        User user = null;

        try {
            conn = dataSource.getConnection();

            if (conn != null) {
                try (PreparedStatement stmnt = conn.prepareStatement("SELECT User_Name FROM users WHERE User_Name=?");) {
                    stmnt.setString(1, username);

                    ResultSet users = stmnt.executeQuery();
                    if (users.next()) {
                        user = new User(users.getString("User_Name"), false);
                    }
                }
            }
        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException throwables) {
            throwables.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return user;
    }

    /**
     * Performs login verification, recieves a user object and a password attempt String as parameters and returns true if
     * there is a match for a user with such username and password. If match is found, logs in user by setting the user object's
     * loggedIn field to true and returning true; otherwise, returns false.
     * @param user The user object from which
     */
    public boolean verifyUser(User user, char[] password) {
        Connection conn = null;

        try {
            conn = dataSource.getConnection();

            if (conn != null) {
                try (PreparedStatement stmnt = conn.prepareStatement("SELECT User_Name, Password FROM users WHERE User_Name=? AND Password=?");) {
                    stmnt.setString(1, user.getUsername());
                    stmnt.setString(2, new String(password));

                    ResultSet users = stmnt.executeQuery();

                    if(users.next() && users.getString("Password").equals(new String(password))) {
                        user.setLoggedIn(true);
                        return true;
                    }
                }
            }
        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException throwables) {
            throwables.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return false;
    }
}
