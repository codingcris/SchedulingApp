package org.example.bundles;

import java.util.ListResourceBundle;

public class AppResources extends ListResourceBundle {

    private Object[][] contents = {
            {"appTitle", "Scheduling App"},
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
