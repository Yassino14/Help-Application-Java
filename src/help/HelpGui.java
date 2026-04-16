package help;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class HelpGui extends JFrame {
    private final HelpService service = new HelpService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private CardLayout mainLayout;
    private JPanel mainContainer;
    private CardLayout roleLayout;
    private JPanel roleContainer;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private JLabel headerTitle;
    private JLabel headerSubtitle;

    private Utilisateur currentUser;

    private JPanel adminPanel;
    private JPanel clientPanel;
    private JPanel employeePanel;

    private JTable usersTable;
    private JTable reservationsTable;
    private JTable clientEmployeesTable;
    private JTable clientReservationsTable;
    private JTable employeeReservationsTable;

    private DefaultTableModel usersModel;
    private DefaultTableModel reservationsModel;
    private DefaultTableModel clientEmployeesModel;
    private DefaultTableModel clientReservationsModel;
    private DefaultTableModel employeeReservationsModel;

    public HelpGui() {
        setTitle("HELP - Home Services Booking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setLocationRelativeTo(null);

        initTheme();

        mainLayout = new CardLayout();
        mainContainer = new JPanel(mainLayout);
        setContentPane(mainContainer);

        mainContainer.add(buildLoginScreen(), "LOGIN");
        buildDashboardScreens();
        mainContainer.add(buildDashboardContainer(), "DASHBOARD");

        mainLayout.show(mainContainer, "LOGIN");
    }

    private void initTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 14));
        UIManager.put("Table.font", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("TableHeader.font", new Font("SansSerif", Font.BOLD, 13));
        UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("PasswordField.font", new Font("SansSerif", Font.PLAIN, 14));
    }

    private JPanel buildLoginScreen() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));

        JPanel left = new JPanel();
        left.setBackground(new Color(25, 35, 55));
        left.setPreferredSize(new Dimension(420, 0));
        left.setLayout(new GridBagLayout());

        JLabel logo = new JLabel("HELP");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 42));

        JLabel slogan = new JLabel("<html><center>Réservation d'employés<br>mécaniciens, plombiers, électriciens...</center></html>");
        slogan.setForeground(new Color(210, 220, 235));
        slogan.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JPanel leftBox = new JPanel();
        leftBox.setOpaque(false);
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        slogan.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftBox.add(logo);
        leftBox.add(Box.createVerticalStrut(15));
        leftBox.add(slogan);

        left.add(leftBox);

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(new Color(245, 247, 250));

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(420, 380));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(25, 25, 25, 25)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Connexion");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Admin, client ou employé");
        subtitle.setForeground(new Color(100, 100, 100));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Se connecter");
        stylePrimaryButton(loginButton);
        loginButton.addActionListener(e -> login());

        JButton demoButton = new JButton("Remplir les comptes de démo");
        styleSecondaryButton(demoButton);
        demoButton.addActionListener(e -> {
            usernameField.setText("admin");
            passwordField.setText("admin123");
        });

        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(25));
        card.add(makeFieldBlock("Username", usernameField));
        card.add(Box.createVerticalStrut(12));
        card.add(makeFieldBlock("Mot de passe", passwordField));
        card.add(Box.createVerticalStrut(20));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(10));
        card.add(demoButton);

        right.add(card);

        root.add(left, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        return root;
    }

    private JPanel makeFieldBlock(String label, JComponent field) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        block.add(l);
        block.add(Box.createVerticalStrut(5));
        block.add(field);
        return block;
    }

    private void buildDashboardScreens() {
        adminPanel = buildAdminPanel();
        clientPanel = buildClientPanel();
        employeePanel = buildEmployeePanel();
    }
    private JPanel buildDashboardContainer() {
        JPanel root = new JPanel(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(25, 35, 55));

        headerTitle = new JLabel("HELP");
        headerTitle.setForeground(Color.WHITE);

        headerSubtitle = new JLabel("Dashboard");
        headerSubtitle.setForeground(Color.LIGHT_GRAY);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(headerTitle);
        titles.add(headerSubtitle);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> logout());

        topBar.add(titles, BorderLayout.WEST);
        topBar.add(logout, BorderLayout.EAST);

        // FIX
        roleLayout = new CardLayout();
        roleContainer = new JPanel(roleLayout);

        roleContainer.add(adminPanel, "ADMIN");
        roleContainer.add(clientPanel, "CLIENT");
        roleContainer.add(employeePanel, "EMPLOYE");

        root.add(topBar, BorderLayout.NORTH);
        root.add(roleContainer, BorderLayout.CENTER);

        return root;
    }
    private void showRolePanel() {
        if (currentUser.getRole() == Role.ADMIN) {
            roleLayout.show(roleContainer, "ADMIN");
        } else if (currentUser.getRole() == Role.CLIENT) {
            roleLayout.show(roleContainer, "CLIENT");
        } else {
            roleLayout.show(roleContainer, "EMPLOYE");
        }
    }

    private JPanel buildAdminPanel() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.setBackground(new Color(245, 247, 250));

        JLabel title = sectionTitle("Espace Administrateur");
        JPanel top = wrapTop(title, "Gestion des utilisateurs, des employés et des réservations.");

        usersModel = new DefaultTableModel(new Object[]{"Rôle", "Username", "Nom", "Spécialité"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        usersTable = new JTable(usersModel);
        styleTable(usersTable);

        reservationsModel = new DefaultTableModel(new Object[]{"ID", "Client", "Employé", "Service", "Date", "Statut"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        reservationsTable = new JTable(reservationsModel);
        styleTable(reservationsTable);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton refreshUsers = new JButton("Rafraîchir utilisateurs");
        styleSecondaryButton(refreshUsers);
        refreshUsers.addActionListener(e -> loadUsersTable());

        JButton addEmployee = new JButton("Ajouter employé");
        stylePrimaryButton(addEmployee);
        addEmployee.addActionListener(e -> addEmployeeDialog());

        JButton deleteUser = new JButton("Supprimer utilisateur");
        styleDangerButton(deleteUser);
        deleteUser.addActionListener(e -> deleteSelectedUser());

        leftActions.add(refreshUsers);
        leftActions.add(addEmployee);
        leftActions.add(deleteUser);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton refreshReservations = new JButton("Rafraîchir réservations");
        styleSecondaryButton(refreshReservations);
        refreshReservations.addActionListener(e -> loadReservationsTable());

        JButton changeStatus = new JButton("Changer statut");
        stylePrimaryButton(changeStatus);
        changeStatus.addActionListener(e -> changeReservationStatusDialog());

        rightActions.add(refreshReservations);
        rightActions.add(changeStatus);

        JPanel usersCard = cardPanel("Utilisateurs", new JScrollPane(usersTable), leftActions);
        JPanel reservationsCard = cardPanel("Réservations", new JScrollPane(reservationsTable), rightActions);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, usersCard, reservationsCard);
        split.setResizeWeight(0.48);
        split.setBorder(null);

        root.add(top, BorderLayout.NORTH);
        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildClientPanel() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.setBackground(new Color(245, 247, 250));

        JLabel title = sectionTitle("Espace Client");
        JPanel top = wrapTop(title, "Consulter les employés disponibles et réserver un service.");

        clientEmployeesModel = new DefaultTableModel(new Object[]{"Username", "Nom", "Spécialité"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        clientEmployeesTable = new JTable(clientEmployeesModel);
        styleTable(clientEmployeesTable);

        clientReservationsModel = new DefaultTableModel(new Object[]{"ID", "Client", "Employé", "Service", "Date", "Statut"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        clientReservationsTable = new JTable(clientReservationsModel);
        styleTable(clientReservationsTable);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton refresh = new JButton("Rafraîchir");
        styleSecondaryButton(refresh);
        refresh.addActionListener(e -> {
            loadClientEmployeesTable();
            loadClientReservationsTable();
        });

        JButton reserve = new JButton("Réserver");
        stylePrimaryButton(reserve);
        reserve.addActionListener(e -> createReservationDialog());

        JButton cancel = new JButton("Annuler réservation");
        styleDangerButton(cancel);
        cancel.addActionListener(e -> cancelSelectedReservation());

        actions.add(refresh);
        actions.add(reserve);
        actions.add(cancel);

        JPanel employeesCard = cardPanel("Employés", new JScrollPane(clientEmployeesTable), null);
        JPanel reservationsCard = cardPanel("Mes réservations", new JScrollPane(clientReservationsTable), actions);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, employeesCard, reservationsCard);
        split.setResizeWeight(0.45);
        split.setBorder(null);

        root.add(top, BorderLayout.NORTH);
        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildEmployeePanel() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.setBackground(new Color(245, 247, 250));

        JLabel title = sectionTitle("Espace Employé");
        JPanel top = wrapTop(title, "Consulter uniquement les réservations qui vous concernent.");

        employeeReservationsModel = new DefaultTableModel(new Object[]{"ID", "Client", "Employé", "Service", "Date", "Statut"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        employeeReservationsTable = new JTable(employeeReservationsModel);
        styleTable(employeeReservationsTable);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton refresh = new JButton("Rafraîchir");
        styleSecondaryButton(refresh);
        refresh.addActionListener(e -> loadEmployeeReservationsTable());
        actions.add(refresh);

        JPanel card = cardPanel("Mes réservations", new JScrollPane(employeeReservationsTable), actions);
        root.add(top, BorderLayout.NORTH);
        root.add(card, BorderLayout.CENTER);
        return root;
    }

    private JPanel cardPanel(String title, JComponent content, JComponent actions) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel head = new JLabel(title);
        head.setFont(new Font("SansSerif", Font.BOLD, 18));
        head.setForeground(new Color(30, 30, 30));

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(head, BorderLayout.WEST);

        if (actions != null) {
            actions.setOpaque(false);
            north.add(actions, BorderLayout.EAST);
        }

        card.add(north, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel wrapTop(JLabel title, String subtitle) {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);

        JLabel sub = new JLabel(subtitle);
        sub.setForeground(new Color(90, 90, 90));
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));

        top.add(title);
        top.add(Box.createVerticalStrut(4));
        top.add(sub);
        top.add(Box.createVerticalStrut(14));
        return top;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        label.setForeground(new Color(25, 35, 55));
        return label;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(new Color(35, 90, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(new Color(235, 240, 248));
        button.setForeground(new Color(25, 35, 55));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleDangerButton(JButton button) {
        button.setBackground(new Color(190, 55, 55));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void login() {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword());

        Utilisateur user = service.login(u, p);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Identifiants incorrects.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentUser = user;
        updateHeader();
        refreshAllTables();

        mainLayout.show(mainContainer, "DASHBOARD");
        showRolePanel();
    }

    private void logout() {
        currentUser = null;
        usernameField.setText("");
        passwordField.setText("");
        mainLayout.show(mainContainer, "LOGIN");
    }

    private void updateHeader() {
        if (currentUser == null) {
            headerTitle.setText("HELP");
            headerSubtitle.setText("Tableau de bord");
            return;
        }

        headerTitle.setText("HELP - " + currentUser.getRole());
        headerSubtitle.setText("Connecté en tant que " + currentUser.getNom() + " (" + currentUser.getUsername() + ")");
    }

    private void refreshAllTables() {
        loadUsersTable();
        loadReservationsTable();
        loadClientEmployeesTable();
        loadClientReservationsTable();
        loadEmployeeReservationsTable();
    }

    private void loadUsersTable() {
        usersModel.setRowCount(0);
        List<Utilisateur> users = service.listUsers();
        for (Utilisateur u : users) {
            String role = String.valueOf(u.getRole());
            String spe = "-";
            if (u instanceof Employe) {
                spe = ((Employe) u).getSpecialite().name();
            }
            usersModel.addRow(new Object[]{role, u.getUsername(), u.getNom(), spe});
        }
    }

    private void loadReservationsTable() {
        reservationsModel.setRowCount(0);
        for (Reservation r : service.listReservations()) {
            reservationsModel.addRow(new Object[]{
                    r.getId(),
                    r.getClientUsername(),
                    r.getEmployeUsername() == null ? "-" : r.getEmployeUsername(),
                    r.getServiceType(),
                    r.getDateTime().format(formatter),
                    r.getStatus()
            });
        }
    }

    private void loadClientEmployeesTable() {
        clientEmployeesModel.setRowCount(0);
        for (Employe e : service.listEmployees()) {
            clientEmployeesModel.addRow(new Object[]{
                    e.getUsername(),
                    e.getNom(),
                    e.getSpecialite()
            });
        }
    }

    private void loadClientReservationsTable() {
        clientReservationsModel.setRowCount(0);
        if (currentUser == null) return;
        for (Reservation r : service.getReservationsByClient(currentUser.getUsername())) {
            clientReservationsModel.addRow(new Object[]{
                    r.getId(),
                    r.getClientUsername(),
                    r.getEmployeUsername() == null ? "-" : r.getEmployeUsername(),
                    r.getServiceType(),
                    r.getDateTime().format(formatter),
                    r.getStatus()
            });
        }
    }

    private void loadEmployeeReservationsTable() {
        employeeReservationsModel.setRowCount(0);
        if (currentUser == null) return;
        if (!(currentUser instanceof Employe)) return;

        for (Reservation r : service.getReservationsByEmployee(currentUser.getUsername())) {
            employeeReservationsModel.addRow(new Object[]{
                    r.getId(),
                    r.getClientUsername(),
                    r.getEmployeUsername() == null ? "-" : r.getEmployeUsername(),
                    r.getServiceType(),
                    r.getDateTime().format(formatter),
                    r.getStatus()
            });
        }
    }

    private void addEmployeeDialog() {
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JTextField nom = new JTextField();
        JComboBox<ServiceType> speciality = new JComboBox<>(ServiceType.values());

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.add(new JLabel("Username"));
        panel.add(username);
        panel.add(new JLabel("Mot de passe"));
        panel.add(password);
        panel.add(new JLabel("Nom complet"));
        panel.add(nom);
        panel.add(new JLabel("Spécialité"));
        panel.add(speciality);

        int res = JOptionPane.showConfirmDialog(this, panel, "Ajouter un employé", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            boolean ok = service.addEmployee(
                    username.getText().trim(),
                    new String(password.getPassword()),
                    nom.getText().trim(),
                    (ServiceType) speciality.getSelectedItem()
            );
            if (ok) {
                loadUsersTable();
                loadClientEmployeesTable();
                JOptionPane.showMessageDialog(this, "Employé ajouté avec succès.");
            } else {
                JOptionPane.showMessageDialog(this, "Username déjà utilisé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedUser() {
        int row = usersTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionne un utilisateur.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String username = String.valueOf(usersModel.getValueAt(row, 1));
        if ("admin".equals(username)) {
            JOptionPane.showMessageDialog(this, "L'admin principal ne peut pas être supprimé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer l'utilisateur " + username + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = service.deleteUser(username);
            if (ok) {
                refreshAllTables();
                JOptionPane.showMessageDialog(this, "Utilisateur supprimé.");
            } else {
                JOptionPane.showMessageDialog(this, "Suppression impossible.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changeReservationStatusDialog() {
        int row = reservationsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionne une réservation.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = Integer.parseInt(String.valueOf(reservationsModel.getValueAt(row, 0)));
        ReservationStatus[] values = ReservationStatus.values();
        ReservationStatus status = (ReservationStatus) JOptionPane.showInputDialog(
                this,
                "Choisir le nouveau statut",
                "Modifier réservation",
                JOptionPane.QUESTION_MESSAGE,
                null,
                values,
                values[0]
        );

        if (status != null) {
            boolean ok = service.changeReservationStatus(id, status);
            if (ok) {
                refreshAllTables();
                JOptionPane.showMessageDialog(this, "Statut mis à jour.");
            } else {
                JOptionPane.showMessageDialog(this, "Réservation introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createReservationDialog() {
        if (currentUser == null || !(currentUser instanceof Client)) {
            return;
        }

        JComboBox<ServiceType> serviceType = new JComboBox<>(ServiceType.values());
        JTextField dateField = new JTextField("2026-04-16 15:00");

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.add(new JLabel("Service"));
        panel.add(serviceType);
        panel.add(new JLabel("Date et heure (yyyy-MM-dd HH:mm)"));
        panel.add(dateField);

        int res = JOptionPane.showConfirmDialog(this, panel, "Nouvelle réservation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                LocalDateTime dt = LocalDateTime.parse(dateField.getText().trim(), formatter);
                Reservation created = service.createReservation(
                        currentUser.getUsername(),
                        (ServiceType) serviceType.getSelectedItem(),
                        dt
                );
                refreshAllTables();
                JOptionPane.showMessageDialog(this, "Réservation créée avec succès.\n" + created);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelSelectedReservation() {
        int row = clientReservationsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionne une réservation.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = Integer.parseInt(String.valueOf(clientReservationsModel.getValueAt(row, 0)));
        int confirm = JOptionPane.showConfirmDialog(this, "Annuler la réservation #" + id + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = service.cancelReservation(id, currentUser.getUsername());
            if (ok) {
                refreshAllTables();
                JOptionPane.showMessageDialog(this, "Réservation annulée.");
            } else {
                JOptionPane.showMessageDialog(this, "Annulation impossible.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}