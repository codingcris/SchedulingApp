package edu.wgu.cristianreyes.application.users;

import java.time.ZonedDateTime;

/**
 * Represents a registered application user.
 */
public class User {
    private String username;
    private boolean loggedIn;

    /**
     * Constructor initializes all fields in the Appointment instance.
     * @param username User username.
     * @param loggedIn true if User has been validated and is logged in; false otherwise.
     */
    public User(String username, boolean loggedIn) {
        this.username = username;
        this.loggedIn = loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
