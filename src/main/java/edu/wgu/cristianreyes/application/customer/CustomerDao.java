package edu.wgu.cristianreyes.application.customer;

import edu.wgu.cristianreyes.application.appointment.Appointment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Interface for data access of Customer objects.
 */
public interface CustomerDao {
    /**
     * Retrieves an ArrayList of all customers in database.
     * @return ArrayList of Customer objects.
     */
    List<Customer> getAllCustomers();

    /**
     * Retrieves a customer from the database with the given ID.
     * @param customerID an integer representing the desired customer's ID.
     * @return Customer with the given ID.
     */
    Customer getCustomer(int customerID);

    /**
     * Updates the database for the given customer if this customer is already within the database.
     * @param updatedCustomer a Customer object representing the customer to be updated. All fields in the database will
     * be updated to match the Customer object's fields.
     */
    void updateCustomer(Customer updatedCustomer);

    /**
     * Deletes the customer from the database with the given ID.
     * @param customerID The ID with which to match the customer being removed from database.
     * @return true if delete is successful, false otherwise.
     */
    boolean deleteCustomer(int customerID);

    /**
     * Inserts a new customer into the database.
     * @param customer The Customer object representing the new customer being added to database.
     */
    void addCustomer(Customer customer);

    /**
     * Queries the database for countries of business and returns a map with the Country ID as key and Country Name as value.
     * @return Map with countries of business.
     */
    Map<Integer, String> getCountriesOfBusiness();

    /**
     * Queries the database for first level divisions and returns a map with the first level division ID as key and first level division name as value.
     * @return Map with first level divisions corresponding to the countries of business.
     */
    Map<Integer, String> getFirstLevelDivisions();

    /**
     * Queries the database for first level divisions corresponding to a particular country with the given ID
     * and returns a map with the first level divisions' IDs as key and first level divisions' names as value.
     * @param countryId ID of country whose first level divisions we are querying for.
     * @return Map with first level divisions corresponding to the countries of business.
     */
    Map<Integer, String> getFirstLevelDivisionsByCountry(int countryId);

    /**
     * Queries the database for the ID of the country to which the given first level division belongs.
     * @param firstDivId The ID of the first level division whose country we are querying for.
     * @return the ID of the country to which the first level division belongs.
     */
    Integer getCountryIdFromFirstDivisionId(int firstDivId);


    ArrayList<Appointment> getCustomerAppointments(int customerId);
}
