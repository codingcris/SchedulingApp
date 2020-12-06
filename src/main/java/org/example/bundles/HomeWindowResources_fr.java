package org.example.bundles;

import java.util.ListResourceBundle;

public class HomeWindowResources_fr extends ListResourceBundle {
    private Object[][] contents = {
            {"id", "ID"},
            {"name", "Nom"},
            {"phoneNumber", "Numéro de téléphone"},
            {"address", "Addresse"},
            {"postalCode", "Code postal"},
            {"country", "Pays"},
            {"state", "Etat"},
            {"add", "Ajouter"},
            {"modify", "Modifier"},
            {"delete", "Effacer"},
            {"deleteCustomerNotification", "Ce client sera définitivement supprimé de la base de données"},
            {"confirmationQuestion", "Êtes-vous d'accord avec ça?"},
            {"customer", "Client"},
            {"customers", "Clients"},
            {"appointments", "Rendez-vous"}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
