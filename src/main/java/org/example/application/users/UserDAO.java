package org.example.application.users;

import org.example.application.customer.Customer;

import java.util.List;
/**
 * Interface for data access of User objects.
 */
public interface UserDAO {
    /**
     * Returns a User with the given username.
     * @param username username
     * @return User
     */
    User getUser(String username);
}
