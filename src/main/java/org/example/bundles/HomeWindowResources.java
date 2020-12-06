package org.example.bundles;

import java.util.ListResourceBundle;

public class HomeWindowResources extends ListResourceBundle {
    private Object[][] contents = {
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
            {"customer", "Customer"},
            {"confirmationQuestion", "Are you OK with this?"},
            {"customers", "Customers"},
            {"appointments", "Appointments"}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
