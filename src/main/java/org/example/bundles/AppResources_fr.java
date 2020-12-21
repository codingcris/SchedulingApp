package org.example.bundles;

import java.util.ListResourceBundle;

public class AppResources_fr extends ListResourceBundle {

    private Object[][] contents = {
            {"appTitle", "Application de planification"},
            {"homeWindowFailure", "Échec de l'ouverture de l'écran d'accueil de l'application. Contacter le service technique"},
            {"databaseConnectionError", "Impossible d'établir les connexions à la base de données. Si le problème persiste, contactez le service informatique."},
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
