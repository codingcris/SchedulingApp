package org.example.application.appointment;

import org.example.application.customer.Customer;

import java.util.List;

public interface AppointmentDao {
    List<Appointment> getAllAppointment();
    Appointment getAppointment(int appointmentId);
    void updateAppointment(Appointment updatedAppointment);
    void deleteAppointment(int appointmentId);
    void addAppointment(Appointment appointment);
}
