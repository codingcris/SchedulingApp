package org.example.application.appointment;

import org.example.application.contact.Contact;
import org.example.application.customer.Customer;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public interface AppointmentDao {
    List<Appointment> getAllAppointments();
    Appointment getAppointment(int appointmentId);
    void updateAppointment(Appointment updatedAppointment);
    boolean deleteAppointment(int appointmentId);
    void addAppointment(Appointment appointment);

    ArrayList<Contact> getAllContacts();

    String getContactName(Integer contactId);
}
