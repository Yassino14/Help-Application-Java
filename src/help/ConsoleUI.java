package help;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final HelpService service;
    private final Scanner sc = new Scanner(System.in);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ConsoleUI(HelpService service) {
        this.service = service;
    }

    public void run() {
        banner();
        while (true) {
            System.out.println();
            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║              MENU HELP               ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║ 1. Connexion                         ║");
            System.out.println("║ 2. Inscription client                ║");
            System.out.println("║ 3. Quitter                           ║");
            System.out.println("╚══════════════════════════════════════╝");

            int choice = readInt("Choix: ");
            switch (choice) {
                case 1:
                    loginFlow();
                    break;
                case 2:
                    registerClientFlow();
                    break;
                case 3:
                    goodbye();
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private void loginFlow() {
        String username = read("Username: ");
        String password = read("Mot de passe: ");
        Utilisateur user = service.login(username, password);

        if (user == null) {
            System.out.println("Connexion refusée.");
            return;
        }

        System.out.println("Bienvenue " + user.getNom() + " [" + user.getRole() + "]");
        if (user instanceof Admin) {
            adminMenu((Admin) user);
        } else if (user instanceof Client) {
            clientMenu((Client) user);
        } else if (user instanceof Employe) {
            employeeMenu((Employe) user);
        }
    }

    private void registerClientFlow() {
        System.out.println("=== INSCRIPTION CLIENT ===");
        String username = read("Choisir un username: ");
        String password = read("Choisir un mot de passe: ");
        String nom = read("Nom complet: ");

        if (service.addClient(username, password, nom)) {
            System.out.println("Compte client créé avec succès.");
        } else {
            System.out.println("Username déjà utilisé.");
        }
    }

    private void adminMenu(Admin admin) {
        while (true) {
            System.out.println();
            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║              MENU ADMIN             ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║ 1. Voir tous les utilisateurs       ║");
            System.out.println("║ 2. Ajouter un employé               ║");
            System.out.println("║ 3. Supprimer un utilisateur         ║");
            System.out.println("║ 4. Voir toutes les réservations     ║");
            System.out.println("║ 5. Changer statut d'une réservation ║");
            System.out.println("║ 6. Déconnexion                      ║");
            System.out.println("╚══════════════════════════════════════╝");

            int choice = readInt("Choix: ");
            switch (choice) {
                case 1:
                    showUsers();
                    break;
                case 2:
                    addEmployeeFlow();
                    break;
                case 3:
                    deleteUserFlow();
                    break;
                case 4:
                    showReservations(service.listReservations());
                    break;
                case 5:
                    changeReservationStatusFlow();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private void clientMenu(Client client) {
        while (true) {
            System.out.println();
            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║              MENU CLIENT            ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║ 1. Consulter les employés           ║");
            System.out.println("║ 2. Réserver un employé              ║");
            System.out.println("║ 3. Mes réservations                 ║");
            System.out.println("║ 4. Annuler une réservation         ║");
            System.out.println("║ 5. Déconnexion                      ║");
            System.out.println("╚══════════════════════════════════════╝");

            int choice = readInt("Choix: ");
            switch (choice) {
                case 1:
                    showEmployees();
                    break;
                case 2:
                    createReservationFlow(client.getUsername());
                    break;
                case 3:
                    showReservations(service.getReservationsByClient(client.getUsername()));
                    break;
                case 4:
                    cancelReservationFlow(client.getUsername());
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private void employeeMenu(Employe employee) {
        while (true) {
            System.out.println();
            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║             MENU EMPLOYÉ            ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║ 1. Voir mes réservations            ║");
            System.out.println("║ 2. Déconnexion                      ║");
            System.out.println("╚══════════════════════════════════════╝");

            int choice = readInt("Choix: ");
            switch (choice) {
                case 1:
                    showReservations(service.getReservationsByEmployee(employee.getUsername()));
                    break;
                case 2:
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private void showUsers() {
        System.out.println("=== UTILISATEURS ===");
        for (Utilisateur u : service.listUsers()) {
            System.out.println(u);
        }
    }

    private void showEmployees() {
        System.out.println("=== EMPLOYÉS ===");
        for (Employe e : service.listEmployees()) {
            System.out.println(e);
        }
    }

    private void addEmployeeFlow() {
        System.out.println("=== AJOUT EMPLOYÉ ===");
        String username = read("Username: ");
        String password = read("Mot de passe: ");
        String nom = read("Nom complet: ");
        ServiceType specialite = readServiceType();

        if (service.addEmployee(username, password, nom, specialite)) {
            System.out.println("Employé ajouté.");
        } else {
            System.out.println("Username déjà utilisé.");
        }
    }

    private void deleteUserFlow() {
        String username = read("Username à supprimer: ");
        if (service.deleteUser(username)) {
            System.out.println("Utilisateur supprimé.");
        } else {
            System.out.println("Suppression impossible.");
        }
    }

    private void createReservationFlow(String clientUsername) {
        System.out.println("=== NOUVELLE RÉSERVATION ===");
        showEmployees();
        ServiceType serviceType = readServiceType();
        LocalDateTime dateTime = readDateTime();

        try {
            Reservation r = service.createReservation(clientUsername, serviceType, dateTime);
            System.out.println("Réservation créée:");
            System.out.println(r);
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private void cancelReservationFlow(String clientUsername) {
        int id = readInt("ID de la réservation à annuler: ");
        if (service.cancelReservation(id, clientUsername)) {
            System.out.println("Réservation annulée.");
        } else {
            System.out.println("Annulation impossible.");
        }
    }

    private void changeReservationStatusFlow() {
        int id = readInt("ID réservation: ");
        System.out.println("1. EN_ATTENTE");
        System.out.println("2. CONFIRMEE");
        System.out.println("3. ANNULEE");
        System.out.println("4. TERMINEE");
        int st = readInt("Statut: ");

        ReservationStatus status;
        switch (st) {
            case 1: status = ReservationStatus.EN_ATTENTE; break;
            case 2: status = ReservationStatus.CONFIRMEE; break;
            case 3: status = ReservationStatus.ANNULEE; break;
            case 4: status = ReservationStatus.TERMINEE; break;
            default:
                System.out.println("Statut invalide.");
                return;
        }

        if (service.changeReservationStatus(id, status)) {
            System.out.println("Statut mis à jour.");
        } else {
            System.out.println("Réservation introuvable.");
        }
    }

    private void showReservations(List<Reservation> list) {
        System.out.println("=== RÉSERVATIONS ===");
        if (list.isEmpty()) {
            System.out.println("Aucune réservation.");
            return;
        }
        for (Reservation r : list) {
            System.out.println(r);
        }
    }

    private ServiceType readServiceType() {
        while (true) {
            System.out.println("Choisir le service:");
            System.out.println("1. MECANICIEN");
            System.out.println("2. PLOMBIER");
            System.out.println("3. ELECTRICIEN");
            System.out.println("4. MENUISIER");
            System.out.println("5. PEINTRE");
            System.out.println("6. AUTRE");

            int c = readInt("Service: ");
            switch (c) {
                case 1: return ServiceType.MECANICIEN;
                case 2: return ServiceType.PLOMBIER;
                case 3: return ServiceType.ELECTRICIEN;
                case 4: return ServiceType.MENUISIER;
                case 5: return ServiceType.PEINTRE;
                case 6: return ServiceType.AUTRE;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private LocalDateTime readDateTime() {
        while (true) {
            String text = read("Date et heure (yyyy-MM-dd HH:mm): ");
            try {
                return LocalDateTime.parse(text, formatter);
            } catch (Exception e) {
                System.out.println("Format invalide.");
            }
        }
    }

    private String read(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Nombre invalide.");
            }
        }
    }

    private void banner() {
        System.out.println("==================================================");
        System.out.println("                 HELP - BOOKING                   ");
        System.out.println("   Réserver un mécanicien, plombier, et plus      ");
        System.out.println("==================================================");
        System.out.println("Comptes démo:");
        System.out.println("Admin   : admin / admin123");
        System.out.println("Client  : client1 / 1234");
        System.out.println("Employé : meca1 / 1234");
    }

    private void goodbye() {
        System.out.println("Fermeture de Help.");
    }
}