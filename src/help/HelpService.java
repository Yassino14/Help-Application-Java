package help;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class HelpService {
    private final Map<String, Utilisateur> users = new LinkedHashMap<>();
    private final Map<Integer, Reservation> reservations = new LinkedHashMap<>();
    private final Path dataDir = Paths.get("data");
    private final Path usersFile = dataDir.resolve("users.txt");
    private final Path reservationsFile = dataDir.resolve("reservations.txt");
    private int nextReservationId = 1;

    public HelpService() {
        initStorage();
        load();
        seedDefaultsIfNeeded();
        saveQuietly();
    }

    private void initStorage() {
        try {
            Files.createDirectories(dataDir);
            if (!Files.exists(usersFile)) Files.createFile(usersFile);
            if (!Files.exists(reservationsFile)) Files.createFile(reservationsFile);
        } catch (IOException e) {
            throw new RuntimeException("Erreur d'initialisation du stockage", e);
        }
    }

    private void seedDefaultsIfNeeded() {
        if (users.isEmpty()) {
            users.put("admin", new Admin("admin", "admin123", "Administrateur"));
            users.put("client1", new Client("client1", "1234", "Client Démo"));
            users.put("meca1", new Employe("meca1", "1234", "Mécano Pro", ServiceType.MECANICIEN));
            users.put("plomb1", new Employe("plomb1", "1234", "Plombier Pro", ServiceType.PLOMBIER));
            users.put("elec1", new Employe("elec1", "1234", "Électricien Pro", ServiceType.ELECTRICIEN));
        }
    }

    public Utilisateur login(String username, String password) {
        Utilisateur u = users.get(username);
        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }

    public boolean addClient(String username, String password, String nom) {
        if (users.containsKey(username)) return false;
        users.put(username, new Client(username, password, nom));
        saveQuietly();
        return true;
    }
    public boolean registerClient(String username, String password, String nom) {
        if (username == null || password == null || nom == null) return false;
        username = username.trim();
        password = password.trim();
        nom = nom.trim();

        if (username.isEmpty() || password.isEmpty() || nom.isEmpty()) return false;
        return addClient(username, password, nom);
    }

    public boolean addEmployee(String username, String password, String nom, ServiceType specialite) {
        if (users.containsKey(username)) return false;
        users.put(username, new Employe(username, password, nom, specialite));
        saveQuietly();
        return true;
    }

    public boolean addAdmin(String username, String password, String nom) {
        if (users.containsKey(username)) return false;
        users.put(username, new Admin(username, password, nom));
        saveQuietly();
        return true;
    }

    public List<Utilisateur> listUsers() {
        return new ArrayList<>(users.values());
    }

    public List<Utilisateur> listUsersByRole(Role role) {
        return users.values().stream()
                .filter(u -> u.getRole() == role)
                .collect(Collectors.toList());
    }

    public List<Employe> listEmployees() {
        return users.values().stream()
                .filter(u -> u instanceof Employe)
                .map(u -> (Employe) u)
                .collect(Collectors.toList());
    }

    public List<Employe> listEmployeesByService(ServiceType serviceType) {
        return listEmployees().stream()
                .filter(e -> e.getSpecialite() == serviceType || e.getSpecialite() == ServiceType.AUTRE)
                .collect(Collectors.toList());
    }

    public List<Reservation> listReservations() {
        return new ArrayList<>(reservations.values());
    }

    public List<Reservation> getReservationsByClient(String clientUsername) {
        return reservations.values().stream()
                .filter(r -> r.getClientUsername().equals(clientUsername))
                .sorted(Comparator.comparing(Reservation::getDateTime))
                .collect(Collectors.toList());
    }

    public List<Reservation> getReservationsByEmployee(String employeeUsername) {
        return reservations.values().stream()
                .filter(r -> employeeUsername.equals(r.getEmployeUsername()))
                .sorted(Comparator.comparing(Reservation::getDateTime))
                .collect(Collectors.toList());
    }

    public Reservation createReservation(String clientUsername, ServiceType serviceType, LocalDateTime dateTime) {
        Employe employee = findAvailableEmployee(serviceType, dateTime)
                .orElseThrow(() -> new IllegalStateException("Aucun employé disponible pour ce créneau."));
        int id = nextReservationId++;
        Reservation r = new Reservation(id, clientUsername, employee.getUsername(), serviceType, dateTime, ReservationStatus.EN_ATTENTE);
        reservations.put(id, r);
        saveQuietly();
        return r;
    }

    public boolean cancelReservation(int id, String requesterUsername) {
        Reservation r = reservations.get(id);
        if (r == null) return false;

        boolean allowed = requesterUsername.equals(r.getClientUsername())
                || "admin".equals(requesterUsername);

        if (!allowed) return false;

        r.setStatus(ReservationStatus.ANNULEE);
        saveQuietly();
        return true;
    }

    public boolean changeReservationStatus(int id, ReservationStatus status) {
        Reservation r = reservations.get(id);
        if (r == null) return false;
        r.setStatus(status);
        saveQuietly();
        return true;
    }

    public boolean deleteUser(String username) {
        if ("admin".equals(username)) return false;
        if (!users.containsKey(username)) return false;

        users.remove(username);
        reservations.entrySet().removeIf(e ->
                username.equals(e.getValue().getClientUsername()) ||
                        username.equals(e.getValue().getEmployeUsername()));
        saveQuietly();
        return true;
    }

    private Optional<Employe> findAvailableEmployee(ServiceType serviceType, LocalDateTime dateTime) {
        return listEmployeesByService(serviceType).stream()
                .filter(emp -> getReservationsByEmployee(emp.getUsername()).stream()
                        .noneMatch(r -> r.getDateTime().equals(dateTime) &&
                                r.getStatus() != ReservationStatus.ANNULEE))
                .findFirst();
    }

    private void load() {
        users.clear();
        reservations.clear();
        nextReservationId = 1;

        try (BufferedReader br = Files.newBufferedReader(usersFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (!line.startsWith("U;")) continue;

                String[] p = line.split(";");
                String type = p[1];
                String username = p[2];
                String password = p[3];
                String nom = p[4];

                switch (type) {
                    case "ADMIN":
                        users.put(username, new Admin(username, password, nom));
                        break;
                    case "CLIENT":
                        users.put(username, new Client(username, password, nom));
                        break;
                    case "EMPLOYE":
                        ServiceType st = ServiceType.valueOf(p[5]);
                        users.put(username, new Employe(username, password, nom, st));
                        break;
                }
            }
        } catch (IOException ignored) {
        }

        try (BufferedReader br = Files.newBufferedReader(reservationsFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (!line.startsWith("R;")) continue;

                Reservation r = Reservation.fromLine(line);
                reservations.put(r.getId(), r);
                nextReservationId = Math.max(nextReservationId, r.getId() + 1);
            }
        } catch (IOException ignored) {
        }
    }

    public void saveQuietly() {
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException("Erreur de sauvegarde", e);
        }
    }

    public void save() throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(usersFile,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE)) {

            for (Utilisateur u : users.values()) {
                if (u instanceof Admin) {
                    bw.write(String.join(";", "U", "ADMIN", u.getUsername(), u.getPassword(), u.getNom()));
                } else if (u instanceof Client) {
                    bw.write(String.join(";", "U", "CLIENT", u.getUsername(), u.getPassword(), u.getNom()));
                } else if (u instanceof Employe) {
                    Employe e = (Employe) u;
                    bw.write(String.join(";", "U", "EMPLOYE", e.getUsername(), e.getPassword(), e.getNom(), e.getSpecialite().name()));
                }
                bw.newLine();
            }
        }

        try (BufferedWriter bw = Files.newBufferedWriter(reservationsFile,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE)) {

            for (Reservation r : reservations.values()) {
                bw.write(r.toLine());
                bw.newLine();
            }
        }
    }
}