package org.example.application.customer;

import java.util.List;
import java.util.Map;

/**
 * Interface for data access of Customer objects.
 */
public interface CustomerDao {
    List<Customer> getAllCustomers();
    Customer getCustomer(int customerID);
    void updateCustomer(Customer updatedCustomer);
    void deleteCustomer(int customerID);
    void addCustomer(Customer customer);
    Map<Integer, String> getCountriesOfBusiness();
    Map<Integer, String> getFirstLevelDivisions(int countryId);
}
