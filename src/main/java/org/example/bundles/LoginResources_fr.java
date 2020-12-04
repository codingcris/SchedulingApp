package org.example.bundles;
import java.util.ListResourceBundle;

public class LoginResources_fr extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return contents;
    }

    private Object[][] contents = {
            {"title", "Application de planification"},
            {"function", "S'identifier"},
            {"username", "Nom d'utilisateur"},
            {"password", "Mot de passe"},
            {"invalidLogin", "Identifiants de connexion non valides, veuillez vérifier le nom d'utilisateur et le mot de passe."},
            {"loginClientError", "Client de connexion indisponible. Si le problème persiste, contactez le service informatique."}
    };
}

