package edu.wgu.cristianreyes.application.appointment;

import edu.wgu.cristianreyes.App;
import edu.wgu.cristianreyes.application.contact.Contact;
import edu.wgu.cristianreyes.application.db.ConnectionPool;
import javafx.application.Application;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;

/**
 * Concrete implementation of AppointmentDao class used to interact with a database containing Appointment object data.
 */
public class AppointmentDaoImpl implements AppointmentDao{
    private final ConnectionPool dataSource;

    /**
     * Constructor initializes dataSource object to the given ConnectionPool.
     * @param dataSource ConnectionPool from which database connections can be retrieved.
     * @throws ConnectionPool.ConnectionsUnavailableException If the connection pool object
     * passed as an argument is null (usually from a misconfigured ConnectionPool initialization) this exception is thrown.
     */
    public AppointmentDaoImpl(ConnectionPool dataSource) throws ConnectionPool.ConnectionsUnavailableException {
        if (dataSource != null) {
            this.dataSource = dataSource;
        } else {
            throw new ConnectionPool.ConnectionsUnavailableException(App.resources.getString("databaseConnectionsError"));
        }
    }

    @Override
    public ArrayList<Appointment> getAllAppointments() {
        String queryString = "SELECT * FROM appointments";
        Connection conn = null;
        ArrayList<Appointment> appointments = new ArrayList<>();

        try {
            conn = dataSource.getConnection();

            try(PreparedStatement stmt = conn.prepareStatement(queryString)) {
                ResultSet results = stmt.executeQuery();

                appointments =  appointmentsFromQueryResult(results);
            }
        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException e) {
            new Alert(Alert.AlertType.ERROR, App.resources.getString("databaseUnreachable")).show();
            e.printStackTrace();
        } finally{
            dataSource.releaseConnection(conn);
        }

        return appointments;
    }

    @Override
    public ArrayList<Appointment> appointmentsFromQueryResult(ResultSet queryResults) {
        ArrayList<Appointment> appointments = new ArrayList<>();

        try {
            while (queryResults.next()) {
                appointments.add( new Appointment(
                        queryResults.getInt("Appointment_ID"),
                        queryResults.getInt("Customer_ID"),
                        queryResults.getInt("User_ID"),
                        queryResults.getInt("Contact_ID"),
                        queryResults.getString("Title"),
                        queryResults.getString("Description"),
                        queryResults.getString("Location"),
                        queryResults.getString("Type"),
                        ((Timestamp) queryResults.getObject("Start")).toLocalDateTime().atZone(ZoneId.systemDefault()),
                        ((Timestamp) queryResults.getObject("End")).toLocalDateTime().atZone(ZoneId.systemDefault()))
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return appointments;
    }

    @Override
    public Appointment getAppointment(int appointmentId) {
        String queryString = "SELECT * FROM appointments WHERE Appointment_ID=?";
        Appointment appointment = null;
        Connection conn = null;

        try {
            conn = dataSource.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(queryString)) {
                stmt.setInt(1, appointmentId);

                ResultSet r = stmt.executeQuery();

                appointment = appointmentsFromQueryResult(r).get(0);
            }
        } catch (ConnectionPool.ConnectionsUnavailableException | SQLException e) {
            new Alert(Alert.AlertType.ERROR, App.resources.getString("databaseUnreachable")).show();
            e.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return appointment;
    }

    @Override
    public void updateAppointment(Appointment updatedAppointment) {
        final String queryString = "UPDATE appointments SET " +
                                    String.join("",
                                            "User_ID=?, ",
                                               "Customer_ID=?, ",
                                               "Title=?, ",
                                               "Description=?, ",
                                               "Location=?, ",
                                               "Type=?, ",
                                               "Contact_ID=?, ",
                                               "Start=?, ",
                                               "End=?",
                                               "WHERE Appointment_ID=?;");

        Connection conn = null;

        try {
            conn = dataSource.getConnection();

            try(PreparedStatement stmnt = conn.prepareStatement(queryString)) {
                stmnt.setInt(1, updatedAppointment.getUserId());
                stmnt.setInt(2, updatedAppointment.getCustomerId());
                stmnt.setString(3, updatedAppointment.getTitle());
                stmnt.setString(4, updatedAppointment.getDescription());
                stmnt.setString(5, updatedAppointment.getLocation());
                stmnt.setString(6, updatedAppointment.getType());
                stmnt.setInt(7, updatedAppointment.getContactId());
                stmnt.setObject(8, updatedAppointment.getStart().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
                stmnt.setObject(9, updatedAppointment.getEnd().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
                stmnt.setInt(10, updatedAppointment.getAppointmentId());

                stmnt.executeUpdate();
            }
        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException throwables) {
            new Alert(Alert.AlertType.ERROR, App.resources.getString("databaseUnreachable")).show();
            throwables.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

    }

    @Override
    public boolean deleteAppointment(int appointmentId) {
        String queryString = "DELETE FROM appointments WHERE Appointment_ID=?";
        boolean status = false;

        Connection conn = null;

        try {
            conn = dataSource.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(queryString)) {
                stmt.setInt(1, appointmentId);

                status = stmt.executeUpdate() == 1;
            }
        } catch (ConnectionPool.ConnectionsUnavailableException | SQLException e) {
            new Alert(Alert.AlertType.ERROR, App.resources.getString("databaseUnreachable")).show();
            e.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return status;
    }

    @Override
    public void addAppointment(Appointment appointment) {
        String queryString = "INSERT INTO appointments(Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID)" +
                             "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;

        try {
            conn = dataSource.getConnection();

            try(PreparedStatement stmt = conn.prepareStatement(queryString)) {
                stmt.setString(1, appointment.getTitle());
                stmt.setString(2, appointment.getDescription());
                stmt.setString(3, appointment.getLocation());
                stmt.setString(4, appointment.getType());
                stmt.setObject(5, appointment.getStart().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
                stmt.setObject(6, appointment.getEnd().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
                stmt.setInt(7, appointment.getCustomerId());
                stmt.setInt(8, appointment.getUserId());
                stmt.setInt(9, appointment.getContactId());

                stmt.executeUpdate();
            }
        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException throwables) {
            new Alert(Alert.AlertType.ERROR, App.resources.getString("databaseUnreachable")).show();
            throwables.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }
    }

    /**
     * Queries the database for company contacts and returns an ArrayList of Contact objects.
     * @return Arraylist of Contact objects.
     */
    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        String queryString = "SELECT * FROM contacts";

        Connection conn = null;

        try {
            conn = dataSource.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(queryString)) {

                ResultSet r = stmt.executeQuery();

                while(r.next()) {
                    contacts.add(new Contact(r.getInt("Contact_ID"), r.getString("Contact_Name"), r.getString("Email")));
                }
            }
        } catch (ConnectionPool.ConnectionsUnavailableException | SQLException e) {
            new Alert(Alert.AlertType.ERROR, App.resources.getString("databaseUnreachable")).show();
            e.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return contacts;
    }

    /**
     * Queries the database for the name of a contact with the given ID and returns the name String.
     * @param contactId contact id of contact being queried for.
     * @return contact name
     */
    public String getContactName(Integer contactId) {
        String queryString = "SELECT Contact_Name FROM contacts WHERE Contact_ID=?";
        String contactName = null;
        Connection conn = null;

        try {
            conn = dataSource.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(queryString);) {
                stmt.setInt(1, contactId);

                ResultSet r = stmt.executeQuery();
                if (r.next())
                    contactName =  r.getString("Contact_Name");
            }
        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException throwables) {
            new Alert(Alert.AlertType.ERROR, App.resources.getString("databaseUnreachable")).show();
            throwables.printStackTrace();
        } finally {
                dataSource.releaseConnection(conn);
        }

        return contactName;
    }

    /**
     * Queries the database for the user with the given ID and returns a boolean indicating whether the database has a match.
     * @param userId the ID of the user being queried for.
     * @return true if user with userId exists, false otherwise.
     */
    public boolean validUser(int userId) {
        String queryString = "SELECT User_ID FROM users WHERE User_ID=?";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(queryString)) {
                stmt.setInt(1, userId);

                ResultSet results = stmt.executeQuery();

                return results.next();
            }
        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException e) {
            new Alert(Alert.AlertType.ERROR, App.resources.getString("databaseUnreachable")).show();
            e.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return false;
    }

    /**
     * Queries the database for the customer with the given ID and returns a boolean indicating whether the database has a match.
     * @param customerId the ID of the customer being queried for.
     * @return true if customer with customerId exists, false otherwise.
     */
    public boolean validCustomer(int customerId) {
        String queryString = "SELECT Customer_ID FROM customers WHERE Customer_ID=?";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(queryString)) {
                stmt.setInt(1, customerId);

                ResultSet results = stmt.executeQuery();

                return results.next();
            }
        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException e) {
            new Alert(Alert.AlertType.ERROR, App.resources.getString("databaseUnreachable")).show();
            e.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return false;
    }

    /**
     * Queries the database for the appointments that correspond to a particular customer with the given customerId.
     * @param customerId the id of the customer whose appointments are being queried for.
     * @return ArrayList of Appointment objects.
     */
    public ArrayList<Appointment> getCustomerAppointments(int customerId) {
        ArrayList<Appointment> appointments = new ArrayList<>();

        Connection conn = null;
        final String queryString = "SELECT * FROM appointments WHERE Customer_ID=?";

        try {
            conn = dataSource.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(queryString)) {
                stmt.setInt(1, customerId);

                ResultSet r = stmt.executeQuery();

                appointments = appointmentsFromQueryResult(r);
            }
        } catch (ConnectionPool.ConnectionsUnavailableException | SQLException e) {
            new Alert(Alert.AlertType.ERROR, App.resources.getString("databaseUnreachable")).show();
            e.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return appointments;
    }

    /**
     * Queries the database for the appointments that correspond to a particular company contact with the given contactId.
     * @param contactId the id of the contact whose appointments are being queried for.
     * @return ArrayList of Appointment objects.
     */
    public ArrayList<Appointment> getContactAppointments(int contactId) {
        String queryString = "SELECT * FROM appointments WHERE Contact_ID=?";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(queryString)) {
                stmt.setInt(1, contactId);

                ResultSet result = stmt.executeQuery();

                return appointmentsFromQueryResult(result);
            }
        } catch (ConnectionPool.ConnectionsUnavailableException | SQLException e) {
            new Alert(Alert.AlertType.ERROR, App.resources.getString("databaseUnreachable")).show();
            e.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return new ArrayList<>();
    }

}
