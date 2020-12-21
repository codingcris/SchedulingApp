package org.example.application.users;

import org.example.application.customer.Customer;

import java.util.List;
/**
 * Interface for data access of User objects.
 */
public interface UserDAO {
    User getUser(String username);
}
