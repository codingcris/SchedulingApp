package org.example.bundles;

import java.util.ListResourceBundle;

public class AppointmentOperationResources extends ListResourceBundle {
    private Object[][] contents = {
            {"id", "ID"},
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
