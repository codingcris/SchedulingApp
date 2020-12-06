package org.example.bundles;

import java.util.ListResourceBundle;

public class CustomerOperationResources_fr extends ListResourceBundle {
    private Object[][] contents = {
            {"addCustomer", "Ajouter un client"},
            {"modifyCustomer", "Modifier le client"},
            {"id", "ID"},
            {"name", "Nom"},
            {"phoneNumber", "Numéro de téléphone"},
            {"address", "Addresse"},
            {"postalCode", "Code postal"},
            {"country", "Pays"},
            {"state", "Etat"},
            {"save","Enregister"},
            {"cancel", "Annuler"},
            {"dataLostNotification", "Toutes les données des champs de texte seront perdues"},
            {"confirmationQuestion", "Êtes-vous d'accord avec ça?"}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
