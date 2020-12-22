package edu.wgu.cristianreyes.application.users;

/**
 * Interface for data access of User objects.
 */
public interface UserDAO {
    /**
     * Searches for the user in database with the passed in username and returns a User object with this user's data.
     * @param username username to search for
     * @return User object if user is found, null if otherwise.
     */
    User getUser(String username);

    /**
     * Performs login verification, recieves a user object and a password attempt String as parameters and returns true if
     * there is a match for a user with such username and password. If match is found, logs in user by setting the user object's
     * loggedIn field to true and returning true; otherwise, returns false.
     * @param user The user object whose password is being verified.
     * @param password  The password attempt
     * @return true if the user is verified, false if verification failed.
     */
    boolean verifyUser(User user, char[] password);
}
