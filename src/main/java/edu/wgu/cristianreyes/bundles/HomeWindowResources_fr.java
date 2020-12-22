package edu.wgu.cristianreyes.bundles;

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
            {"deleteAppointmentNotification", "Ce rendez-vous sera définitivement supprimé."},
            {"confirmationQuestion", "Êtes-vous d'accord avec ça?"},
            {"customer", "Client"},
            {"customers", "Clients"},
            {"appointment", "Rendez-vous"},
            {"appointments", "Rendez-vous"},
            {"appointmentId", "ID de rendez-vous"},
            {"upcomingAppointments", "Prochains rendez-vous"},
            {"noUpcomingAppointments", "Vous n'avez pas de rendez-vous à venir"},
            {"noAppointmentsInDb", "Il n'y a pas de rendez-vous dans la base de données."},
            {"noContactsInDb", "Il n'y a aucun contact dans la base de données."},
            {"upcomingAppointmentsFailure", "Échec de l'affichage des rendez-vous à venir."},
            {"customerWithAppointmentsDelete", "Ce client a des rendez-vous à venir. Tous ces rendez-vous seront également supprimés."},
            {"customerDeleteSuccess", "Client supprimé avec succès."},
            {"customerDeleteFailure", "Échec de la suppression du client"},
            {"title", "Titre"},
            {"description", "La Description"},
            {"type", "Catégorie"},
            {"location", "Emplacement"},
            {"startDate", "Date de Début"},
            {"startTime", "Heure de Début"},
            {"endDate", "Date de Fin"},
            {"endTime", "Heure de Fin"},
            {"customerId", "Identification du Client"},
            {"contact", "Le Contact"},
            {"contactId", "Identifiant de Contact"},
            {"monthOf", "Mois de"},
            {"weekOf", "Semaine de"},
            {"through", "à"},
            {"monthly", "Mensuel"},
            {"weekly", "Hebdomadaire"},
            {"appointmentDeleted", "Rendez-vous supprimé avec succès"},
            {"filterBy", "Filtrer Par"},
            {"previous", "Précédente"},
            {"next", "Prochaine"},
            {"appointmentType", "Type de rendez-vous"},
            {"appointmentCount", "Nombre de rendez-vous"},
            {"count", "Compter"},
            {"appointmentsThisMonth", "Rendez-vous ce mois-ci: "},
            {"monthlyAverage", "Moyenne mensuelle: "},
            {"monthsReported", "Mois déclarés: "},
            {"appointmentsThisWeek", "Rendez-vous cette semaine: "},
            {"weeklyAverage", "Moyenne hebdomadaire: "},
            {"weeksReported","Semaines déclarées: "},
            {"noAppointmentsForContact", "Pas de données de rendez-vous pour le contact."},
            {"contactName", "Nom du Contact"},
            {"monthTypeReport", "Nombre de rendez-vous par mois / type"},
            {"contactSchedules", "Horaires de Contact"},
            {"contactInteraction", "Interaction des contacts avec les clients"},
            {"generateReport", "Générer un rapport"}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
