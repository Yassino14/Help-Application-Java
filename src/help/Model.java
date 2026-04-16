package help;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

enum Role {
    ADMIN, CLIENT, EMPLOYE
}

enum ServiceType {
    MECANICIEN, PLOMBIER, ELECTRICIEN, MENUISIER, PEINTRE, AUTRE
}

enum ReservationStatus {
    EN_ATTENTE, CONFIRMEE, ANNULEE, TERMINEE
}

abstract class Utilisateur {
    private final String username;
    private String password;
    private String nom;
    private final Role role;

    protected Utilisateur(String username, String password, String nom, Role role) {
        this.username = username;
        this.password = password;
        this.nom = nom;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNom() {
        return nom;
    }

    public Role getRole() {
        return role;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return role + " | " + username + " | " + nom;
    }
}

class Admin extends Utilisateur {
    public Admin(String username, String password, String nom) {
        super(username, password, nom, Role.ADMIN);
    }
}

class Client extends Utilisateur {
    public Client(String username, String password, String nom) {
        super(username, password, nom, Role.CLIENT);
    }
}

class Employe extends Utilisateur {
    private ServiceType specialite;

    public Employe(String username, String password, String nom, ServiceType specialite) {
        super(username, password, nom, Role.EMPLOYE);
        this.specialite = specialite;
    }

    public ServiceType getSpecialite() {
        return specialite;
    }

    public void setSpecialite(ServiceType specialite) {
        this.specialite = specialite;
    }

    @Override
    public String toString() {
        return super.toString() + " | spécialité=" + specialite;
    }
}

class Reservation {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private int id;
    private String clientUsername;
    private String employeUsername;
    private ServiceType serviceType;
    private LocalDateTime dateTime;
    private ReservationStatus status;

    public Reservation(int id, String clientUsername, String employeUsername,
                       ServiceType serviceType, LocalDateTime dateTime,
                       ReservationStatus status) {
        this.id = id;
        this.clientUsername = clientUsername;
        this.employeUsername = employeUsername;
        this.serviceType = serviceType;
        this.dateTime = dateTime;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public String getEmployeUsername() {
        return employeUsername;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setEmployeUsername(String employeUsername) {
        this.employeUsername = employeUsername;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String toLine() {
        return String.join(";",
                "R",
                String.valueOf(id),
                clientUsername,
                employeUsername == null ? "-" : employeUsername,
                serviceType.name(),
                dateTime.toString(),
                status.name());
    }

    public static Reservation fromLine(String line) {
        String[] p = line.split(";");
        int id = Integer.parseInt(p[1]);
        String client = p[2];
        String emp = "-".equals(p[3]) ? null : p[3];
        ServiceType type = ServiceType.valueOf(p[4]);
        LocalDateTime dt = LocalDateTime.parse(p[5]);
        ReservationStatus st = ReservationStatus.valueOf(p[6]);
        return new Reservation(id, client, emp, type, dt, st);
    }

    @Override
    public String toString() {
        return "ID=" + id +
                " | client=" + clientUsername +
                " | employé=" + (employeUsername == null ? "-" : employeUsername) +
                " | service=" + serviceType +
                " | date=" + dateTime.format(FORMATTER) +
                " | statut=" + status;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Reservation)) return false;
        Reservation other = (Reservation) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}