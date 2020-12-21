package org.example.bundles;

import java.util.ListResourceBundle;

public class AppResources extends ListResourceBundle {

    private Object[][] contents = {
            {"appTitle", "Scheduling App"},
            {"homeScreenFailure", "Failure opening home screen of application. Contact IT"},
            {"databaseConnectionError", "Cannot establish database connections. If problem persists contact IT."},
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
