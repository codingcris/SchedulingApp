package edu.wgu.cristianreyes.application.users;

import edu.wgu.cristianreyes.application.db.ConnectionPool;
import edu.wgu.cristianreyes.App;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            new Alert(Alert.AlertType.ERROR, App.resources.getString("databaseUnreachable")).show();
            throwables.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return user;
    }

    @Override
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
            new Alert(Alert.AlertType.ERROR, App.resources.getString("databaseUnreachable")).show();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return false;
    }
}
