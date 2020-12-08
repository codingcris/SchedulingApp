package org.example.bundles;

import java.util.ListResourceBundle;

public class CustomerOperationResources extends ListResourceBundle {
    private Object[][] contents = {
            {"addCustomer", "Add Customer"},
            {"modifyCustomer", "Modify Customer"},
            {"id", "ID"},
            {"name", "Name"},
            {"phoneNumber", "Phone Number"},
            {"address", "Address"},
            {"postalCode", "Postal Code"},
            {"country", "Country"},
            {"state", "State"},
            {"save", "Save"},
            {"cancel", "Cancel"},
            {"dataLostNotification", "All unsaved data in text fields will be lost."},
            {"confirmationQuestion", "Are you OK with this?"},
            {"noEmptyFields", "Please fill out all empty fields."}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
