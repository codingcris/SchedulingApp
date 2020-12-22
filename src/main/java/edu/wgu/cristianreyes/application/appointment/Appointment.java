package edu.wgu.cristianreyes.application.appointment;

import java.time.ZonedDateTime;

/**
 * Represents an appointment between a customer and a company contact.
 */
public class Appointment {
    private int appointmentId;
    private int customerId;
    private int userId;
    private int contactId;
    private String title;
    private String description;
    private String location;
    private String type;
    private ZonedDateTime start;
    private ZonedDateTime end;

    /**
     * Constructor initializes all fields in the Appointment instance.
     * @param appointmentId the appointment ID.
     * @param customerId The customer ID.
     * @param userId the user ID.
     * @param contactId the contact ID.
     * @param title appointment title.
     * @param description appointment description.
     * @param location appointment location.
     * @param type appointment type.
     * @param start appointment start.
     * @param end appointment end.
     */
    public Appointment(int appointmentId, int customerId, int userId, int contactId, String title, String description, String location, String type,
                       ZonedDateTime start, ZonedDateTime end) {
        this(customerId, userId, contactId, title, description, location, type, start, end);

        this.setId(appointmentId);
    }

    /**
     * Constructor initializes all fields in the Appointment instance except for the appointmentId field.
     * @param customerId The customer ID.
     * @param userId the user ID.
     * @param contactId the contact ID.
     * @param title appointment title.
     * @param description appointment description.
     * @param location appointment location.
     * @param type appointment type.
     * @param start appointment start.
     * @param end appointment end.
     */
    public Appointment(int customerId, int userId, int contactId, String title, String description, String location, String type,
                       ZonedDateTime start, ZonedDateTime end) {
        this.customerId = customerId;
        this.userId = userId;
        this.contactId = contactId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
    }

    private void setId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

}
