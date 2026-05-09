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
    private ImageIcon heroIcon;
    private static final Color APP_BG = new Color(15, 18, 25);
    private static final Color PANEL_BG = new Color(22, 26, 35);
    private static final Color CARD_BG = new Color(30, 35, 46);
    private static final Color CARD_BG_2 = new Color(36, 42, 55);
    private static final Color TEXT = new Color(240, 243, 248);
    private static final Color MUTED = new Color(170, 178, 190);
    private static final Color PRIMARY = new Color(70, 110, 220);
    private static final Color PRIMARY_HOVER = new Color(90, 130, 240);
    private static final Color DANGER = new Color(185, 64, 64);
    private static final Color BORDER = new Color(55, 62, 75);

    private CardLayout mainLayout;
    private JPanel mainContainer;

    private CardLayout roleLayout;
    private JPanel roleContainer;

    private JTabbedPane authTabs;

    private JTextField usernameField;
    private JPasswordField passwordField;

    private JTextField registerUsernameField;
    private JPasswordField registerPasswordField;
    private JTextField registerNameField;

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
        setSize(1280, 820);
        setLocationRelativeTo(null);

        initTheme();
        heroIcon = loadScaledIcon("/help/assets/image.png", 600, 750);
        mainLayout = new CardLayout();
        mainContainer = new JPanel(mainLayout);
        mainContainer.setBackground(APP_BG);
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

        Font base = new Font("SansSerif", Font.PLAIN, 14);
        Font bold = new Font("SansSerif", Font.BOLD, 14);
        UIManager.put("Label.font", base);
        UIManager.put("Button.font", bold);
        UIManager.put("TextField.font", base);
        UIManager.put("PasswordField.font", base);
        UIManager.put("Table.font", base);
        UIManager.put("TableHeader.font", bold);
        UIManager.put("TabbedPane.font", bold);
    }

    private JPanel buildLoginScreen() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(APP_BG);

        JPanel left = new JPanel(new GridBagLayout());
        left.setPreferredSize(new Dimension(460, 0));
        left.setBackground(new Color(12, 15, 22));
        left.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel leftBox = new JPanel();
        leftBox.setOpaque(false);
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
        if (heroIcon != null) {
            JLabel hero = new JLabel(heroIcon);
            hero.setAlignmentX(Component.CENTER_ALIGNMENT);
            leftBox.add(hero);
            leftBox.add(Box.createVerticalStrut(18));
        }
        JLabel slogan = new JLabel("<html><center>Réservation d'employés<br>mécaniciens, plombiers, électriciens</center></html>");
        slogan.setForeground(new Color(215, 222, 235));
        slogan.setFont(new Font("SansSerif", Font.PLAIN, 19));
        slogan.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftBox.add(Box.createVerticalGlue());
        leftBox.add(Box.createVerticalStrut(14));
        leftBox.add(slogan);
        leftBox.add(Box.createVerticalStrut(12));
        leftBox.add(Box.createVerticalGlue());

        left.add(leftBox);

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(APP_BG);
        right.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setPreferredSize(new Dimension(500, 520));
        card.setBackground(new Color(5, 15, 40));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("Bienvenue");
        title.setForeground(TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel subtitle = new JLabel("Connexion ou création de compte client");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);

        authTabs = new JTabbedPane();
        authTabs.setBackground(CARD_BG);

        authTabs.setForeground(Color.BLACK);

        authTabs.addTab("Connexion", buildLoginTab());
        authTabs.addTab("Créer un compte", buildRegisterTab());

        card.add(header, BorderLayout.NORTH);
        card.add(authTabs, BorderLayout.CENTER);

        right.add(card);

        root.add(left, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildLoginTab() {
        JPanel tab = new JPanel();
        tab.setBackground(CARD_BG);
        tab.setBorder(new EmptyBorder(18, 14, 14, 14));
        tab.setLayout(new BoxLayout(tab, BoxLayout.Y_AXIS));

        usernameField = createField();
        passwordField = new JPasswordField();
        styleInput(passwordField);

        tab.add(fieldBlock("Username", usernameField));
        tab.add(Box.createVerticalStrut(12));
        tab.add(fieldBlock("Mot de passe", passwordField));
        tab.add(Box.createVerticalStrut(18));

        JButton loginButton = new JButton("Se connecter");
        stylePrimaryButton(loginButton);
        loginButton.addActionListener(e -> login());

        JButton demoButton = new JButton("Remplir avec admin");
        styleSecondaryButton(demoButton);
        demoButton.addActionListener(e -> {
            usernameField.setText("admin");
            passwordField.setText("admin123");
        });

        tab.add(loginButton);
        tab.add(Box.createVerticalStrut(10));
        tab.add(demoButton);
        tab.add(Box.createVerticalStrut(10));

        JLabel help = new JLabel("Comptes démo : admin/admin123, client1/1234, meca1/1234");
        help.setForeground(MUTED);
        help.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tab.add(help);

        return tab;
    }

    private JPanel buildRegisterTab() {
        JPanel tab = new JPanel();
        tab.setBackground(CARD_BG);
        tab.setBorder(new EmptyBorder(18, 14, 14, 14));
        tab.setLayout(new BoxLayout(tab, BoxLayout.Y_AXIS));

        registerUsernameField = createField();
        registerPasswordField = new JPasswordField();
        registerNameField = createField();

        tab.add(fieldBlock("Username", registerUsernameField));
        tab.add(Box.createVerticalStrut(12));
        tab.add(fieldBlock("Mot de passe", registerPasswordField));
        tab.add(Box.createVerticalStrut(12));
        tab.add(fieldBlock("Nom complet", registerNameField));
        tab.add(Box.createVerticalStrut(18));

        JButton registerButton = new JButton("Créer le compte client");
        stylePrimaryButton(registerButton);
        registerButton.addActionListener(e -> registerClient());

        tab.add(registerButton);
        tab.add(Box.createVerticalStrut(10));

        JLabel note = new JLabel("Le compte créé est automatiquement un client.");
        note.setForeground(MUTED);
        note.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tab.add(note);

        return tab;
    }

    private JTextField createField() {
        JTextField f = new JTextField();
        styleInput(f);
        return f;
    }

    private void styleInput(JTextField field) {
        field.setBackground(CARD_BG_2);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    }

    private JPanel fieldBlock(String label, JComponent field) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));

        JLabel l = new JLabel(label);
        l.setForeground(TEXT);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        block.add(l);
        block.add(Box.createVerticalStrut(6));
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
        root.setBackground(APP_BG);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(12, 15, 22));
        topBar.setBorder(new EmptyBorder(16, 22, 16, 22));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        headerTitle = new JLabel("HELP");
        headerTitle.setForeground(Color.WHITE);
        headerTitle.setFont(new Font("SansSerif", Font.BOLD, 28));

        headerSubtitle = new JLabel("Dashboard");
        headerSubtitle.setForeground(MUTED);
        headerSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        titles.add(headerTitle);
        titles.add(Box.createVerticalStrut(2));
        titles.add(headerSubtitle);

        JButton logoutButton = new JButton("Déconnexion");
        styleDangerButton(logoutButton);
        logoutButton.addActionListener(e -> logout());

        topBar.add(titles, BorderLayout.WEST);
        topBar.add(logoutButton, BorderLayout.EAST);

        roleLayout = new CardLayout();
        roleContainer = new JPanel(roleLayout);
        roleContainer.setBackground(APP_BG);

        roleContainer.add(adminPanel, "ADMIN");
        roleContainer.add(clientPanel, "CLIENT");
        roleContainer.add(employeePanel, "EMPLOYE");

        root.add(topBar, BorderLayout.NORTH);
        root.add(roleContainer, BorderLayout.CENTER);
        return root;
    }

    private void showRolePanel() {
        if (currentUser == null) return;

        switch (currentUser.getRole()) {
            case ADMIN:
                roleLayout.show(roleContainer, "ADMIN");
                break;
            case CLIENT:
                roleLayout.show(roleContainer, "CLIENT");
                break;
            case EMPLOYE:
                roleLayout.show(roleContainer, "EMPLOYE");
                break;
        }
    }

    private JPanel buildAdminPanel() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.setBackground(APP_BG);

        JLabel title = sectionTitle("Espace Administrateur");
        JPanel top = wrapTop(title, "Gestion des utilisateurs, employés et réservations.");

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
        leftActions.setOpaque(false);

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
        rightActions.setOpaque(false);

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
        split.setOpaque(false);

        root.add(top, BorderLayout.NORTH);
        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildClientPanel() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.setBackground(APP_BG);

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
        actions.setOpaque(false);

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
        split.setOpaque(false);

        root.add(top, BorderLayout.NORTH);
        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildEmployeePanel() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.setBackground(APP_BG);

        JLabel title = sectionTitle("Espace Employé");
        JPanel top = wrapTop(title, "Consulter uniquement les réservations qui vous concernent.");

        employeeReservationsModel = new DefaultTableModel(new Object[]{"ID", "Client", "Employé", "Service", "Date", "Statut"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        employeeReservationsTable = new JTable(employeeReservationsModel);
        styleTable(employeeReservationsTable);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

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
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel head = new JLabel(title);
        head.setFont(new Font("SansSerif", Font.BOLD, 18));
        head.setForeground(TEXT);

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(head, BorderLayout.WEST);

        if (actions != null) {
            north.add(actions, BorderLayout.EAST);
        }

        card.add(north, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        styleScrollPane((JScrollPane) content);
        return card;
    }

    private void styleScrollPane(JScrollPane pane) {
        pane.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        pane.getViewport().setBackground(CARD_BG);
        pane.setBackground(CARD_BG);
    }

    private JPanel wrapTop(JLabel title, String subtitle) {
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel sub = new JLabel(subtitle);
        sub.setForeground(MUTED);
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
        label.setForeground(TEXT);
        return label;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(CARD_BG);
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(75, 110, 200));
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.setGridColor(BORDER);
        table.setOpaque(true);
        table.getTableHeader().setBackground(CARD_BG_2);
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(BORDER, 1));
        table.getTableHeader().setReorderingAllowed(false);
    }

    private void stylePrimaryButton(JButton button) {
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addChangeListener(e -> {
            ButtonModel m = button.getModel();
            if (m.isRollover() && m.isEnabled()) {
                button.setBackground(PRIMARY_HOVER);
            } else {
                button.setBackground(PRIMARY);
            }
            button.setForeground(Color.WHITE);
        });
    }

    private void styleSecondaryButton(JButton button) {
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBackground(CARD_BG_2);
        button.setForeground(TEXT);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleDangerButton(JButton button) {
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBackground(DANGER);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void login() {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword()).trim();

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Remplis username et mot de passe.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

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

    private void registerClient() {
        String u = registerUsernameField.getText().trim();
        String p = new String(registerPasswordField.getPassword()).trim();
        String n = registerNameField.getText().trim();

        if (u.isEmpty() || p.isEmpty() || n.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = service.registerClient(u, p, n);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Compte client créé avec succès.");
            registerUsernameField.setText("");
            registerPasswordField.setText("");
            registerNameField.setText("");
            authTabs.setSelectedIndex(0);
            usernameField.setText(u);
            passwordField.setText(p);
        } else {
            JOptionPane.showMessageDialog(this, "Username déjà utilisé ou données invalides.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
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
            headerSubtitle.setText("Dashboard");
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
        panel.setBackground(CARD_BG);
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
                    new String(password.getPassword()).trim(),
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

        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer l'utilisateur " + username + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
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
        if (currentUser == null || !(currentUser instanceof Client)) return;

        JComboBox<ServiceType> serviceType = new JComboBox<>(ServiceType.values());
        JTextField dateField = new JTextField("2026-04-16 15:00");

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBackground(CARD_BG);
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
    private ImageIcon loadScaledIcon(String path, int width, int height) {
        java.net.URL url = getClass().getResource(path);
        if (url == null) return null;
        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}