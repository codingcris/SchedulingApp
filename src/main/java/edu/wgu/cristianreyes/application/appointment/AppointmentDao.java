package edu.wgu.cristianreyes.application.appointment;

import edu.wgu.cristianreyes.application.contact.Contact;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface for data access of Appointment objects.
 */
public interface AppointmentDao {
    /**
     * Queries database for all appointments and returns an ArrayList containing Appointment objects.
     * @return ArrayList with all appointments in database represented as Appointment objects.
     */
    ArrayList<Appointment> getAllAppointments();

    /**
     * Queries database for appointment with the given ID, and returns the result as an Appointment object.
     * @param appointmentId The ID value of the appointment being retrieved.
     * @return Appointment object if the database has a match, null otherwise.
     */
    Appointment getAppointment(int appointmentId);

    /**
     * Recieves an appointment object and updates the record for that appointment in the database to the fields within the passed Appointment object.
     * @param updatedAppointment The Appointment object with the updated data, which is used to update the database.
     */
    void updateAppointment(Appointment updatedAppointment);

    /**
     * If a match is found for the given appointment ID, the matching appointment will be deleted from the database.
     * @param appointmentId appointment ID of the appointment being queried for.
     * @return true if delete is successful, false if delete fails.
     */
    boolean deleteAppointment(int appointmentId);

    /**
     * Creates a new record in the database from an Appointment instance.
     * @param appointment the Appointment object to add to database.
     */
    void addAppointment(Appointment appointment);

    /**
     * Takes a query result set and returns a List of Appointment objects from the result set.
     * @param queryResults result set from an executed query.
     * @return ArrayList with Appointment objects representing the queryResults contents.
     */
    ArrayList<Appointment> appointmentsFromQueryResult(ResultSet queryResults);
}
