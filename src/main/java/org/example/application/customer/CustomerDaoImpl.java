package org.example.application.customer;

 import org.example.application.db.ConnectionPool;

 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
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
            throw new ConnectionPool.ConnectionsUnavailableException("Cannot establish database connections. If problem persists contact IT.");
        }
    }

    /**
     * Retrieves a List of all customers in database.
     * @return List of Customer objects.
     */
    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
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
     * Retrieves a customer from the database with the given Customer_ID value.
     * @param customerID an integer representing the desired customer's Customer_ID.
     * @return Customer with the given Customer_ID value.
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
     * Deletes the customer from the database with the given Customer_ID.
     * @param customerID The Customer_ID with which to match the customer being removed from database.
     */
    @Override
    public void deleteCustomer(int customerID) {
        final String queryString = "DELETE FROM customers WHERE Customer_ID=?";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();

            try(PreparedStatement stmnt = conn.prepareStatement(queryString)) {
                stmnt.setInt(1, customerID);

                stmnt.executeUpdate();
            }

        } catch (SQLException | ConnectionPool.ConnectionsUnavailableException throwables) {
            throwables.printStackTrace();
        } finally {
            dataSource.releaseConnection(conn);
        }

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
     * Takes a query result set and returns a List of  Customer objects in the result set.
     * @param queryResults result set from an executed query.
     */
    private List<Customer> customersFromQueryResult(ResultSet queryResults) {
        List<Customer> customers = new ArrayList<>();

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
}
