package edu.wgu.cristianreyes.application.contact;

import java.time.ZonedDateTime;

/**
 * Represents a company contact.
 */
public class Contact {
    private int id;
    private String name;
    private String email;

    /**
     * Constructor initializes all fields in the Contact instance.
     * @param id The contact ID.
     * @param name the contact name.
     * @param email the contact email.
     */
    public Contact(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
