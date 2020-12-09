package org.example.application.appointment;

import org.example.App;
import org.example.application.customer.Customer;
import org.example.application.db.ConnectionPool;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDaoImpl implements AppointmentDao{
    private final ConnectionPool dataSource;

    public AppointmentDaoImpl(ConnectionPool dataSource) throws ConnectionPool.ConnectionsUnavailableException {
        if (dataSource != null) {
            this.dataSource = dataSource;
        } else {
            throw new ConnectionPool.ConnectionsUnavailableException("Cannot establish database connections. If problem persists contact IT.");
        }
    }

    @Override
    public List<Appointment> getAllAppointments() {
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
            e.printStackTrace();
        } finally{
            dataSource.releaseConnection(conn);
        }

        return appointments;
    }

    /**
     * Takes a query result set and returns a List of  Customer objects in the result set.
     * @param queryResults result set from an executed query.
     */
    private ArrayList<Appointment> appointmentsFromQueryResult(ResultSet queryResults) {
        ArrayList<Appointment> appointments = new ArrayList<>();

//         public Appointment(int appointmentId, int customerId, int userId, int contactId, String title, String description, String location, String type,
//                       ZonedDateTime start, ZonedDateTime end)

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
                        ((Timestamp) queryResults.getObject("Start")).toLocalDateTime(),
                        ((Timestamp) queryResults.getObject("End")).toLocalDateTime()
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return appointments;
    }


    @Override
    public Appointment getAppointment(int appointmentId) {
        return null;
    }

    @Override
    public void updateAppointment(Appointment updatedAppointment) {

    }

    @Override
    public void deleteAppointment(int appointmentId) {

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
                stmt.setObject(5, appointment.getStart());
                stmt.setObject(6, appointment.getEnd());
                stmt.setInt(7, appointment.getCustomerId());
                stmt.setInt(8, appointment.getUserId());
                stmt.setInt(9, appointment.getContactId());
            }
        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException throwables) {
            throwables.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }
    }

    @Override
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
            throwables.printStackTrace();
        } finally {
            if (conn != null)
                dataSource.releaseConnection(conn);
        }

        return contactName;
    }

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
            e.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return false;
    }

    public boolean validContact(int contactId) {
        String queryString = "SELECT Contact_ID FROM contacts WHERE Contact_ID=?";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(queryString)) {
                stmt.setInt(1, contactId);

                ResultSet results = stmt.executeQuery();

                return results.next();
            }
        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException e) {
            e.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return false;
    }

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
            e.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return false;
    }
}
