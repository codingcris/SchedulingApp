package org.example.application.customer;

import org.example.application.appointment.Appointment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Interface for data access of Customer objects.
 */
public interface CustomerDao {
    List<Customer> getAllCustomers();
    Customer getCustomer(int customerID);
    void updateCustomer(Customer updatedCustomer);
    boolean deleteCustomer(int customerID);
    void addCustomer(Customer customer);
    Map<Integer, String> getCountriesOfBusiness();
    Map<Integer, String> getFirstLevelDivisions();
    Map<Integer, String> getFirstLevelDivisionsByCountry(int countryId);
    Integer getCountryIdFromFirstDivisionId(int firstDivId);
    ArrayList<Appointment> getCustomerAppointments(int customerId);
}
