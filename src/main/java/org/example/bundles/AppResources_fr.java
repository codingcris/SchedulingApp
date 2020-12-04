package org.example.bundles;

import java.util.ListResourceBundle;

public class AppResources_fr extends ListResourceBundle {

    private Object[][] contents = {
            {"appTitle", "Application de planification"},
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
