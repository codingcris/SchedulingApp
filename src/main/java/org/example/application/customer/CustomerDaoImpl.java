package org.example.application.customer;

 import org.example.App;
 import org.example.application.appointment.Appointment;
 import org.example.application.db.ConnectionPool;

 import java.sql.*;
 import java.time.ZoneId;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

/**
  * Concrete implementation of the CustomerDao interface. Handles database access of Customer objects.
  */
public final class CustomerDaoImpl implements CustomerDao {
    private final ConnectionPool dataSource;

    /**
     * Constructor initializes dataSource object to the given ConnectionPool.
     * @param dataSource ConnectionPool from which database connections can be retrieved.
     * @throws ConnectionPool.ConnectionsUnavailableException If the connection pool object
     * passed as an argument is null (usually from a misconfigured ConnectionPool initialization) this exception is thrown.
     */
    public CustomerDaoImpl(ConnectionPool dataSource) throws ConnectionPool.ConnectionsUnavailableException {
        if (dataSource != null) {
            this.dataSource = dataSource;
        } else {
            throw new ConnectionPool.ConnectionsUnavailableException(App.resources.getString("databaseConnectionsError"));
        }
    }

    /**
     * Retrieves the appointments for a customer with the given customerId and returns a boolean indicating whether or not
     * the result is empty
     * @return true if customer has no appointments in database, false otherwise.
     */
    public boolean hasNoAppointments(int customerId) {
        return getCustomerAppointments(customerId).isEmpty();
    }

    /**
     * Retrieves an ArrayList of all customers in database.
     * @return ArrayList of Customer objects.
     */
    @Override
    public ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        Connection conn = null ;

        final String queryString = "SELECT * FROM customers;";
        try {
            conn = dataSource.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(queryString)) {
                ResultSet queryResults = stmt.executeQuery();

                customers = customersFromQueryResult(queryResults);
            }

        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException throwables) {
            throwables.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return customers;
    }

    /**
     * Retrieves a customer from the database with the given ID.
     * @param customerID an integer representing the desired customer's ID.
     * @return Customer with the given ID.
     */
    @Override
    public Customer getCustomer(int customerID) {
        final String queryString = "SELECT * FROM customers WHERE Customer_ID=?";
        Connection conn = null;
        Customer c = null;

        try {
            conn = dataSource.getConnection();

            try (PreparedStatement stmnt = conn.prepareStatement(queryString)) {
                stmnt.setInt(1, customerID);

                ResultSet queryResults = stmnt.executeQuery();

                c = customersFromQueryResult(queryResults).get(0);
            }

        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException throwables) {
            throwables.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return c;
    }

    /**
     * Updates the database for the given customer if this customer is already within the database.
     * @param updatedCustomer a Customer object representing the customer to be updated. All fields in the database will
     * be updated to match the Customer object's fields.
     */
    @Override
    public void updateCustomer(Customer updatedCustomer) {
        int customerID = updatedCustomer.getId();

        final String queryString = "UPDATE customers SET " +
                                    String.join("",
                                       "Customer_ID=?, ",
                                                "Customer_Name=?, ",
                                                "Address=?, ",
                                                "Postal_Code=?, ",
                                                "Phone=?, ",
                                                "Division_ID=? ",
                                                "WHERE Customer_ID=?;"
                                              );
        Connection conn = null;

        try {
            conn = dataSource.getConnection();

            try(PreparedStatement stmnt = conn.prepareStatement(queryString)) {
                stmnt.setInt(1, customerID);
                stmnt.setString(2, updatedCustomer.getName());
                stmnt.setString(3, updatedCustomer.getAddress());
                stmnt.setString(4, updatedCustomer.getPostalCode());
                stmnt.setString(5, updatedCustomer.getPhone());
                stmnt.setInt(6, updatedCustomer.getDivisionId());
                stmnt.setInt(7, customerID);

                stmnt.executeUpdate();
            }
        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException throwables) {
            throwables.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }
    }

    /**
     * Deletes the customer from the database with the given ID.
     * @param customerID The ID with which to match the customer being removed from database.
     * @return true if delete is successful, false otherwise.
     */
    @Override
    public boolean deleteCustomer(int customerID) {
        boolean success = false;

        final String queryString = "DELETE FROM customers WHERE Customer_ID=?";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();

            try(PreparedStatement stmnt = conn.prepareStatement(queryString)) {
                stmnt.setInt(1, customerID);

                success = stmnt.executeUpdate() == 1;
            }

        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException throwables) {
            throwables.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return success;
    }

    /**
     * Inserts a new customer into the database.
     * @param customer The Customer object representing the new customer being added to database.
     */
    @Override
    public void addCustomer(Customer customer) {
        final String queryString = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Division_ID)" +
                                    " VALUES (?, ?, ?, ?, ?);";

        Connection conn = null;

        try {
            conn = dataSource.getConnection();

            try(PreparedStatement stmnt = conn.prepareStatement(queryString)) {
                stmnt.setString(1, customer.getName());
                stmnt.setString(2, customer.getAddress());
                stmnt.setString(3, customer.getPostalCode());
                stmnt.setString(4, customer.getPhone());
                stmnt.setInt(5,customer.getDivisionId());

                stmnt.executeUpdate();
            }
        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException throwables) {
            throwables.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }


    }

    /**
     * Queries the database for countries of business and returns a map with the Country ID as key and Country Name as value.
     * @return Map with countries of business.
     */
    @Override
    public Map<Integer, String> getCountriesOfBusiness() {
        final String[] countriesOfBusiness = {"United States", "United Kingdom", "Canada"};

        Connection conn = null;
        HashMap<Integer, String> countries = new HashMap<>();

        StringBuilder queryBuilder = new StringBuilder("SELECT Country, Country_ID FROM countries WHERE Country IN( ");

        for (String country : countriesOfBusiness) {
            queryBuilder.append("?, ");
        }

        queryBuilder.deleteCharAt(queryBuilder.length() - 2);

        queryBuilder.append(");");

        try {
          conn = dataSource.getConnection();

          try(PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {

              for(int i = 1; i <= countriesOfBusiness.length; i++) {
                  stmt.setString(i, countriesOfBusiness[i - 1]);
              }

              ResultSet r = stmt.executeQuery();

              while (r.next()) {
                  countries.put(r.getInt(2), r.getString(1));
              }
          }
        } catch (SQLException | RuntimeException | ConnectionPool.ConnectionsUnavailableException e) {
            e.printStackTrace();
        } finally {
            if (dataSource != null) dataSource.releaseConnection(conn);
        }

        return countries;
    }

    /**
     * Queries the database for first level divisions and returns a map with the first level division ID as key and first level division name as value.
     * @return Map with first level divisions corresponding to the countries of business.
     */
    @Override
    public Map<Integer, String> getFirstLevelDivisions() {
        Connection conn = null;
        HashMap<Integer, String> firstLevelDivs = new HashMap<>();

        try {
            conn = dataSource.getConnection();

            try(PreparedStatement stmnt = conn.prepareStatement("SELECT Division, Division_ID FROM first_level_divisions")) {
                ResultSet r = stmnt.executeQuery();

                while (r.next()) {
                    firstLevelDivs.put(r.getInt(2), r.getString(1));
                }
            }

        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException throwables) {
            throwables.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return firstLevelDivs;
    }

    /**
     * Queries the database for first level divisions corresponding to a particular country with the given ID
     * and returns a map with the first level divisions' IDs as key and first level divisions' names as value.
     * @param countryId ID of country whose first level divisions we are querying for.
     * @return Map with first level divisions corresponding to the countries of business.
     */
    @Override
    public Map<Integer, String> getFirstLevelDivisionsByCountry(int countryId) {
        Connection conn = null;
        HashMap<Integer, String> firstLevelDivs = new HashMap<>();

        try {
            conn = dataSource.getConnection();

            try(PreparedStatement stmnt = conn.prepareStatement("SELECT Division, Division_ID FROM first_level_divisions WHERE Country_ID=?")) {
                stmnt.setInt(1, countryId);

                ResultSet r = stmnt.executeQuery();

                while (r.next()) {
                    firstLevelDivs.put(r.getInt(2), r.getString(1));
                }
            }

        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException throwables) {
            throwables.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return firstLevelDivs;
    }

    /**
     * Queries the database for the ID of the country to which the given first level division belongs.
     * @param firstDivId The ID of the first level division whose country we are querying for.
     * @return the ID of the country to which the first level division belongs.
     */
    @Override
    public Integer getCountryIdFromFirstDivisionId(int firstDivId) {
        Connection conn = null;
        String queryString = "SELECT Country_ID FROM first_level_divisions WHERE Division_ID=?";

        try {
            conn = dataSource.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(queryString)){
                stmt.setInt(1, firstDivId);

                ResultSet r = stmt.executeQuery();

                if (r.next()) {
                    return r.getInt(1);
                }
            }
        } catch (ConnectionPool.ConnectionsUnavailableException | SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                dataSource.releaseConnection(conn);
        }

        return null;
    }

    /**
     * Takes a query result set and returns an ArrayList of Customer objects in the result set.
     * @param queryResults result set from an executed query.
     */
    private ArrayList<Customer> customersFromQueryResult(ResultSet queryResults) {
        ArrayList<Customer> customers = new ArrayList<>();

        try {
            while (queryResults.next()) {
                customers.add( new Customer(
                        queryResults.getInt("Customer_ID"),
                        queryResults.getString("Customer_Name"),
                        queryResults.getString("Address"),
                        queryResults.getString("Postal_Code"),
                        queryResults.getString("Phone"),
                        queryResults.getInt("Division_ID")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return customers;
    }

    /**
     * Queries the database for all appointments belonging to a particular customer and return an ArrayList of Appointment
     * objects.
     * @param customerId the ID of the customer whose appointments we are querying for.
     * @return ArrayList of appointments belonging to the given customer.
     */
    @Override
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
            e.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

        return appointments;
    }

    /**
     * Takes a query result set and returns a List of Appointment objects from the result set.
     * @param queryResults result set from an executed query.
     * @return ArrayList with Appointment objects representing the queryResults contents.
     */
    private ArrayList<Appointment> appointmentsFromQueryResult(ResultSet queryResults) {
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
}
