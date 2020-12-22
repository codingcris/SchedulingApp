package edu.wgu.cristianreyes.bundles;

import javafx.scene.control.Label;

import java.util.ListResourceBundle;

public class HomeWindowResources extends ListResourceBundle {
    private Object[][] contents = {
            {"welcome", "Welcome."},
            {"id", "ID"},
            {"name", "Name"},
            {"phoneNumber", "Phone Number"},
            {"address", "Address"},
            {"postalCode", "Postal Code"},
            {"country", "Country"},
            {"state", "State"},
            {"add", "Add"},
            {"modify", "Modify"},
            {"delete", "Delete"},
            {"deleteCustomerNotification", "This customer will be permanently deleted from database."},
            {"deleteAppointmentNotification", "This appointment will be permanently deleted."},
            {"customer", "Customer"},
            {"confirmationQuestion", "Are you OK with this?"},
            {"customers", "Customers"},
            {"appointment", "Appointment"},
            {"appointments", "Appointments"},
            {"appointmentId", "Appointment ID"},
            {"upcomingAppointments", "Upcoming Appointments"},
            {"noUpcomingAppointments", "You have no upcoming appointments."},
            {"noAppointmentsInDb", "There are no appointments in the database."},
            {"noContactsInDb", "There are no contacts in the database."},
            {"upcomingAppointmentsFailure", "Failed to display upcoming appointments."},
            {"appointmentDeleted", "Appointment succesfully deleted."},
            {"customerWithAppointmentsDelete", "This customer has upcoming appointments. All of these appointments will also be deleted."},
            {"customerDeleteSuccess", "Customer successfully deleted."},
            {"customerDeleteFailure", "Failed to delete customer."},
            {"title", "Title"},
            {"description", "Description"},
            {"type", "Type"},
            {"location", "Location"},
            {"startDate", "Start Date"},
            {"startTime", "Start Time"},
            {"endDate", "End Date"},
            {"endTime", "End Time"},
            {"customerId", "Customer ID"},
            {"contact", "Contact"},
            {"contactId", "Contact ID"},
            {"monthOf", "Month of"},
            {"weekOf", "Week of"},
            {"through", "Through"},
            {"monthly", "Monthly"},
            {"weekly", "Weekly"},
            {"filterBy", "Filter By"},
            {"previous", "Previous"},
            {"next", "Next"},
            {"appointmentType", "Appoinment Type"},
            {"appointmentCount", "Appoinments Count"},
            {"count", "Count"},
            {"appointmentsThisMonth","Appointments this month: "},
            {"monthlyAverage","Monthly Average: "},
            {"monthsReported","Months Reported: "},
            {"appointmentsThisWeek","Appointments this week: "},
            {"weeklyAverage","Weekly Average: "},
            {"weeksReported","Weeks Reported: "},
            {"noAppointmentsForContact", "No appointment data for Contact."},
            {"contactName", "Contact Name"},
            {"monthTypeReport", "Appointment Counts by Month/Type"},
            {"contactSchedules", "Contact Schedules"},
            {"contactInteraction", "Contact/Customer Interaction"},
            {"generateReport", "Generate Report"}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
