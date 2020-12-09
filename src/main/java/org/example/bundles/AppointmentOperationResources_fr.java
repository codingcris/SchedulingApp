package org.example.bundles;

import java.util.ListResourceBundle;

public class AppointmentOperationResources_fr extends ListResourceBundle {
    private Object[][] contents = {
            {"id", "ID"},
            {"save", "Enregister"},
            {"cancel", "Annuler"},
            {"dataLostNotification", "Toutes les données des champs de texte seront perdues"},
            {"confirmationQuestion", "Êtes-vous d'accord avec ça?"},
            {"noEmptyFields", "Veuillez remplir tous les champs vides."}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
