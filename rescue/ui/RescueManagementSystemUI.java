package rescue.ui;

import rescue.model.*;
import rescue.system.*;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RescueManagementSystemUI extends JFrame {
    private final ForestMap1 map;
    private final RescueSystem rescueSystem;
    private final ManagementSystem managementSystem;
    private final FileStorage storage;

    private JTable nodesTable;
    private JTable survivorsTable;
    private JTable teamsTable;
    private JTable hazardsTable;
    private JTable alertsTable;
    private JTextArea weatherArea;
    private JTextArea reportArea;
    private JTextArea pathArea;
    private JComboBox<String> startBox;
    private JComboBox<String> endBox;
    private JComboBox<String> modeBox;
    private List<Node> nodeList = new ArrayList<>();

    public static void launch(ForestMap1 map, RescueSystem rescueSystem, ManagementSystem managementSystem) {
        launch(map, rescueSystem, managementSystem, null);
    }

    public static void launch(ForestMap1 map, RescueSystem rescueSystem, ManagementSystem managementSystem,
                              FileStorage storage) {
        SwingUtilities.invokeLater(() -> {
            try {
                RescueManagementSystemUI ui = new RescueManagementSystemUI(map, rescueSystem, managementSystem, storage);
                ui.setVisible(true);
            } catch (RuntimeException e) {
                System.out.println("GUI startup failed: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private RescueManagementSystemUI(ForestMap1 map, RescueSystem rescueSystem, ManagementSystem managementSystem,
                               FileStorage storage) {
        super("Forest Navigation & Rescue Management System");
        this.map = map;
        this.rescueSystem = rescueSystem;
        this.managementSystem = managementSystem;
        this.storage = storage;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 640);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Map", buildMapPanel());
        tabs.add("Survivors", buildSurvivorsPanel());
        tabs.add("Teams", buildTeamsPanel());
        tabs.add("Hazards", buildHazardsPanel());
        tabs.add("Alerts", buildAlertsPanel());
        tabs.add("Reports", buildReportsPanel());
        tabs.add("Pathfinding", buildPathPanel());

        setJMenuBar(buildMenuBar());
        add(tabs, BorderLayout.CENTER);
        refreshAll();
    }

    private JPanel buildMapPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        weatherArea = new JTextArea(3, 40);
        weatherArea.setEditable(false);
        weatherArea.setBackground(new Color(245, 245, 245));
        weatherArea.setBorder(BorderFactory.createTitledBorder("Weather"));

        nodesTable = createTable(new String[] { "Name", "Type", "X", "Y", "Edges" });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshMap());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(refreshButton);

        panel.add(weatherArea, BorderLayout.NORTH);
        panel.add(new JScrollPane(nodesTable), BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildSurvivorsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        survivorsTable = createTable(new String[] { "ID", "Name", "Condition", "Location", "Found" });
        installTableEditor(survivorsTable, this::editSelectedSurvivor);
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addSurvivorDialog());
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> editSelectedSurvivor());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshSurvivors());
        panel.add(new JScrollPane(survivorsTable), BorderLayout.CENTER);
        panel.add(wrapRight(addButton, editButton, refreshButton), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildTeamsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        teamsTable = createTable(new String[] { "ID", "Name", "Size", "Status", "Location" });
        installTableEditor(teamsTable, this::editSelectedTeam);
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addTeamDialog());
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> editSelectedTeam());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshTeams());
        panel.add(new JScrollPane(teamsTable), BorderLayout.CENTER);
        panel.add(wrapRight(addButton, editButton, refreshButton), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildHazardsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        hazardsTable = createTable(new String[] { "Type", "Severity", "Node", "Active", "Description" });
        installTableEditor(hazardsTable, this::editSelectedHazard);
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addHazardDialog());
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> editSelectedHazard());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshHazards());
        panel.add(new JScrollPane(hazardsTable), BorderLayout.CENTER);
        panel.add(wrapRight(addButton, editButton, refreshButton), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildAlertsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        alertsTable = createTable(new String[] { "ID", "Priority", "Message", "Active", "Timestamp" });
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addAlertDialog());
        JButton dismissButton = new JButton("Dismiss");
        dismissButton.addActionListener(e -> dismissSelectedAlert());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshAlerts());
        panel.add(new JScrollPane(alertsTable), BorderLayout.CENTER);
        panel.add(wrapRight(addButton, dismissButton, refreshButton), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setBorder(BorderFactory.createTitledBorder("Report"));

        JButton generateButton = new JButton("Generate Report");
        generateButton.addActionListener(e -> {
            Report report = managementSystem.generateReport(rescueSystem.getSurvivors().size());
            reportArea.setText(report.export());
            refreshAlerts();
            refreshHazards();
        });

        panel.add(wrapLeft(generateButton), BorderLayout.NORTH);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildPathPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(2, 4, 8, 8));
        startBox = new JComboBox<>();
        endBox = new JComboBox<>();
        modeBox = new JComboBox<>(new String[] { "Shortest", "Safest" });
        JButton findButton = new JButton("Find Path");
        findButton.addActionListener(e -> computePath());

        form.add(new JLabel("Start"));
        form.add(startBox);
        form.add(new JLabel("End"));
        form.add(endBox);
        form.add(new JLabel("Mode"));
        form.add(modeBox);
        form.add(new JLabel(""));
        form.add(findButton);

        pathArea = new JTextArea();
        pathArea.setEditable(false);
        pathArea.setBorder(BorderFactory.createTitledBorder("Path"));

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(pathArea), BorderLayout.CENTER);
        return panel;
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu actions = new JMenu("Actions");
        actions.add(menuItem("Add Survivor", this::addSurvivorDialog));
        actions.add(menuItem("Edit Selected Survivor", this::editSelectedSurvivor));
        actions.addSeparator();
        actions.add(menuItem("Add Team", this::addTeamDialog));
        actions.add(menuItem("Edit Selected Team", this::editSelectedTeam));
        actions.addSeparator();
        actions.add(menuItem("Add Hazard", this::addHazardDialog));
        actions.add(menuItem("Edit Selected Hazard", this::editSelectedHazard));
        actions.addSeparator();
        actions.add(menuItem("Add Alert", this::addAlertDialog));
        actions.add(menuItem("Dismiss Selected Alert", this::dismissSelectedAlert));
        actions.addSeparator();
        actions.add(menuItem("Save Data", this::saveAll));
        actions.add(menuItem("Refresh All", this::refreshAll));
        actions.addSeparator();
        actions.add(menuItem("Exit", this::dispose));

        menuBar.add(actions);
        return menuBar;
    }

    private JMenuItem menuItem(String label, Runnable action) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(e -> action.run());
        return item;
    }

    private void installTableEditor(JTable table, Runnable onEdit) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    onEdit.run();
                }
            }
        });
    }

    private void addSurvivorDialog() {
        showSurvivorDialog(null);
    }

    private void editSelectedSurvivor() {
        int row = survivorsTable.getSelectedRow();
        if (row < 0) {
            showInfo("Select a survivor to edit.");
            return;
        }
        String id = String.valueOf(survivorsTable.getValueAt(row, 0));
        Survivor survivor = findSurvivor(id);
        if (survivor == null) {
            showError("Survivor not found.");
            return;
        }
        showSurvivorDialog(survivor);
    }

    private void showSurvivorDialog(Survivor survivor) {
        boolean editing = survivor != null;
        JTextField idField = new JTextField(editing ? survivor.getId() : "");
        idField.setEditable(!editing);
        JTextField nameField = new JTextField(editing ? survivor.getName() : "");
        JComboBox<Condition> conditionBox = new JComboBox<>(Condition.values());
        if (editing) {
            conditionBox.setSelectedItem(survivor.getCondition());
        }
        JComboBox<String> locationBox = new JComboBox<>(nodeNameArray());
        if (editing && survivor.getLocation() != null) {
            locationBox.setSelectedItem(survivor.getLocation().getName());
        }
        JCheckBox foundBox = new JCheckBox("Found");
        foundBox.setSelected(editing && survivor.isFound());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("ID"));
        form.add(idField);
        form.add(new JLabel("Name"));
        form.add(nameField);
        form.add(new JLabel("Condition"));
        form.add(conditionBox);
        form.add(new JLabel("Location"));
        form.add(locationBox);
        form.add(new JLabel(""));
        form.add(foundBox);

        if (!showForm(editing ? "Edit Survivor" : "Add Survivor", form)) {
            return;
        }

        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        if (id.isEmpty() || name.isEmpty()) {
            showError("ID and name are required.");
            return;
        }
        if (!editing && findSurvivor(id) != null) {
            showError("Survivor ID already exists.");
            return;
        }

        Node location = resolveNode((String) locationBox.getSelectedItem());
        if (location == null) {
            showError("Select a valid location.");
            return;
        }
        Condition condition = (Condition) conditionBox.getSelectedItem();
        boolean found = foundBox.isSelected();

        if (editing) {
            survivor.setName(name);
            survivor.updateCondition(condition);
            survivor.setLocation(location);
            survivor.setFound(found);
        } else {
            Survivor newSurvivor = new Survivor(id, name, condition, location);
            newSurvivor.setFound(found);
            rescueSystem.addSurvivor(newSurvivor);
        }

        saveAll();
        refreshSurvivors();
    }

    private void addTeamDialog() {
        showTeamDialog(null);
    }

    private void editSelectedTeam() {
        int row = teamsTable.getSelectedRow();
        if (row < 0) {
            showInfo("Select a team to edit.");
            return;
        }
        String id = String.valueOf(teamsTable.getValueAt(row, 0));
        RescueTeam team = findTeamById(id);
        if (team == null) {
            showError("Team not found.");
            return;
        }
        showTeamDialog(team);
    }

    private void showTeamDialog(RescueTeam team) {
        boolean editing = team != null;
        JTextField idField = new JTextField(editing ? team.getTeamId() : "");
        idField.setEditable(!editing);
        JTextField nameField = new JTextField(editing ? team.getName() : "");
        JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(editing ? team.getSize() : 1, 1, 99, 1));
        JComboBox<TeamStatus> statusBox = new JComboBox<>(TeamStatus.values());
        statusBox.setSelectedItem(editing ? team.getStatus() : TeamStatus.IDLE);
        JComboBox<String> locationBox = new JComboBox<>(nodeNameArray());
        if (editing && team.getCurrentNode() != null) {
            locationBox.setSelectedItem(team.getCurrentNode().getName());
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("ID"));
        form.add(idField);
        form.add(new JLabel("Name"));
        form.add(nameField);
        form.add(new JLabel("Size"));
        form.add(sizeSpinner);
        form.add(new JLabel("Status"));
        form.add(statusBox);
        form.add(new JLabel("Location"));
        form.add(locationBox);

        if (!showForm(editing ? "Edit Team" : "Add Team", form)) {
            return;
        }

        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        if (id.isEmpty() || name.isEmpty()) {
            showError("ID and name are required.");
            return;
        }
        if (!editing && findTeamById(id) != null) {
            showError("Team ID already exists.");
            return;
        }

        Node location = resolveNode((String) locationBox.getSelectedItem());
        if (location == null) {
            showError("Select a valid location.");
            return;
        }
        int size = (Integer) sizeSpinner.getValue();
        TeamStatus status = (TeamStatus) statusBox.getSelectedItem();

        if (editing) {
            team.setName(name);
            team.setSize(size);
            team.setStatus(status);
            team.updateLocation(location);
        } else {
            RescueTeam newTeam = new RescueTeam(id, name, size, location);
            newTeam.setStatus(status);
            rescueSystem.addTeam(newTeam);
        }

        saveAll();
        refreshTeams();
    }

    private void addHazardDialog() {
        showHazardDialog(-1);
    }

    private void editSelectedHazard() {
        int row = hazardsTable.getSelectedRow();
        if (row < 0) {
            showInfo("Select a hazard to edit.");
            return;
        }
        showHazardDialog(row);
    }

    private void showHazardDialog(int index) {
        List<Hazard> hazards = managementSystem.getHazards();
        Hazard hazard = (index >= 0 && index < hazards.size()) ? hazards.get(index) : null;
        boolean editing = hazard != null;

        JComboBox<HazardType> typeBox = new JComboBox<>(HazardType.values());
        typeBox.setSelectedItem(editing ? hazard.getType() : HazardType.WILDFIRE);
        JSpinner severitySpinner = new JSpinner(new SpinnerNumberModel(editing ? hazard.getSeverity() : 3, 1, 5, 1));
        JComboBox<String> nodeBox = new JComboBox<>(nodeNameArray());
        if (editing && hazard.getNode() != null) {
            nodeBox.setSelectedItem(hazard.getNode().getName());
        }
        JCheckBox activeBox = new JCheckBox("Active");
        activeBox.setSelected(!editing || hazard.isActive());
        JTextField descriptionField = new JTextField(editing ? hazard.getDescription() : "");

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Type"));
        form.add(typeBox);
        form.add(new JLabel("Severity"));
        form.add(severitySpinner);
        form.add(new JLabel("Node"));
        form.add(nodeBox);
        form.add(new JLabel("Description"));
        form.add(descriptionField);
        form.add(new JLabel(""));
        form.add(activeBox);

        if (!showForm(editing ? "Edit Hazard" : "Add Hazard", form)) {
            return;
        }

        Node node = resolveNode((String) nodeBox.getSelectedItem());
        if (node == null) {
            showError("Select a valid node.");
            return;
        }

        HazardType type = (HazardType) typeBox.getSelectedItem();
        int severity = (Integer) severitySpinner.getValue();
        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            description = type + " detected at " + node.getName();
        }

        Hazard updated = new Hazard(type, severity, node, description);
        if (!activeBox.isSelected()) {
            updated.deactivate();
        }
        node.setType(NodeType.HAZARD_ZONE);

        if (editing) {
            hazards.set(index, updated);
        } else {
            hazards.add(updated);
        }

        saveAll();
        refreshHazards();
        refreshMap();
    }

    private void addAlertDialog() {
        JTextField messageField = new JTextField();
        JComboBox<Priority> priorityBox = new JComboBox<>(Priority.values());
        JCheckBox activeBox = new JCheckBox("Active");
        activeBox.setSelected(true);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Message"));
        form.add(messageField);
        form.add(new JLabel("Priority"));
        form.add(priorityBox);
        form.add(new JLabel(""));
        form.add(activeBox);

        if (!showForm("Add Alert", form)) {
            return;
        }

        String message = messageField.getText().trim();
        if (message.isEmpty()) {
            showError("Message is required.");
            return;
        }

        Priority priority = (Priority) priorityBox.getSelectedItem();
        Alert alert = new Alert(nextAlertId(), message, priority);
        if (!activeBox.isSelected()) {
            alert.dismiss();
        }

        managementSystem.getAlerts().add(alert);
        saveAll();
        refreshAlerts();
    }

    private void dismissSelectedAlert() {
        int row = alertsTable.getSelectedRow();
        if (row < 0) {
            showInfo("Select an alert to dismiss.");
            return;
        }
        String id = String.valueOf(alertsTable.getValueAt(row, 0));
        Alert alert = findAlert(id);
        if (alert == null) {
            showError("Alert not found.");
            return;
        }
        alert.dismiss();
        saveAll();
        refreshAlerts();
    }

    private boolean showForm(String title, JPanel panel) {
        int result = JOptionPane.showConfirmDialog(this, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }

    private JTable createTable(String[] columns) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        return table;
    }

    private JPanel wrapRight(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        for (JButton button : buttons) {
            panel.add(button);
        }
        return panel;
    }

    private JPanel wrapLeft(JButton button) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(button);
        return panel;
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private Survivor findSurvivor(String id) {
        for (Survivor s : rescueSystem.getSurvivors()) {
            if (s.getId().equalsIgnoreCase(id)) {
                return s;
            }
        }
        return null;
    }

    private RescueTeam findTeamById(String id) {
        for (RescueTeam t : rescueSystem.getTeams()) {
            if (t.getTeamId().equalsIgnoreCase(id)) {
                return t;
            }
        }
        return null;
    }

    private Alert findAlert(String id) {
        for (Alert a : managementSystem.getAlerts()) {
            if (a.getAlertID().equalsIgnoreCase(id)) {
                return a;
            }
        }
        return null;
    }

    private String nextAlertId() {
        int max = 0;
        for (Alert a : managementSystem.getAlerts()) {
            max = Math.max(max, parseAlertNumber(a.getAlertID()));
        }
        return "ALT-" + (max + 1);
    }

    private int parseAlertNumber(String id) {
        if (id == null) {
            return 0;
        }
        String trimmed = id.trim();
        if (trimmed.toUpperCase().startsWith("ALT-")) {
            trimmed = trimmed.substring(4);
        }
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void refreshAll() {
        refreshMap();
        refreshSurvivors();
        refreshTeams();
        refreshHazards();
        refreshAlerts();
    }

    private void refreshMap() {
        String statusLine = rescueSystem.getStatus() + " | " + managementSystem.getStatus();
        weatherArea.setText(
                "Weather: " + map.getWeather().getSummary() + "\n" +
                        "Risk Multiplier: " + map.getWeather().getRiskMultiplier() + "\n" +
                        statusLine
        );

        DefaultTableModel model = (DefaultTableModel) nodesTable.getModel();
        model.setRowCount(0);
        for (Node n : map.getAllNodes()) {
            Coordinate c = n.getCoordinate();
            model.addRow(new Object[] { n.getName(), n.getType(), c.getX(), c.getY(), n.getEdges().size() });
        }
        refreshNodeList();
    }

    private void updateNodeTable() {
        DefaultTableModel model = (DefaultTableModel) nodesTable.getModel();
        model.setRowCount(0);
        for (Node node : nodeList) {
            Coordinate c = node.getCoordinate();
            model.addRow(new Object[] { node.getName(), node.getType(), c.getX(), c.getY(), node.getEdges().size() });
        }
    }

    private void refreshSurvivors() {
        DefaultTableModel model = (DefaultTableModel) survivorsTable.getModel();
        model.setRowCount(0);
        for (Survivor survivor : rescueSystem.getSurvivors()) {
            model.addRow(new Object[] { survivor.getId(), survivor.getName(), survivor.getCondition(),
                    survivor.getLocation() != null ? survivor.getLocation().getName() : "", survivor.isFound() });
        }
    }

    private void refreshTeams() {
        DefaultTableModel model = (DefaultTableModel) teamsTable.getModel();
        model.setRowCount(0);
        for (RescueTeam team : rescueSystem.getTeams()) {
            model.addRow(new Object[] { team.getTeamId(), team.getName(), team.getSize(), team.getStatus(),
                    team.getCurrentNode() != null ? team.getCurrentNode().getName() : "" });
        }
    }

    private void refreshHazards() {
        DefaultTableModel model = (DefaultTableModel) hazardsTable.getModel();
        model.setRowCount(0);
        for (Hazard hazard : managementSystem.getHazards()) {
            model.addRow(new Object[] { hazard.getType(), hazard.getSeverity(), hazard.getNode() != null ? hazard.getNode().getName() : "",
                    hazard.isActive(), hazard.getDescription() });
        }
    }

    private void refreshAlerts() {
        DefaultTableModel model = (DefaultTableModel) alertsTable.getModel();
        model.setRowCount(0);
        for (Alert alert : managementSystem.getAlerts()) {
            model.addRow(new Object[] { alert.getAlertID(), alert.getPriority(), alert.getMessage(), alert.isActive(), alert.getTimestamp() });
        }
    }

    private void refreshNodeList() {
        if (startBox == null || endBox == null) {
            return;
        }

        int startIndex = startBox.getSelectedIndex();
        int endIndex = endBox.getSelectedIndex();

        nodeList = new ArrayList<>(map.getAllNodes());
        DefaultComboBoxModel<String> startModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> endModel = new DefaultComboBoxModel<>();
        for (Node n : nodeList) {
            startModel.addElement(n.getName());
            endModel.addElement(n.getName());
        }
        startBox.setModel(startModel);
        endBox.setModel(endModel);

        if (startIndex >= 0 && startIndex < nodeList.size()) {
            startBox.setSelectedIndex(startIndex);
        }
        if (endIndex >= 0 && endIndex < nodeList.size()) {
            endBox.setSelectedIndex(endIndex);
        }
    }

    private void computePath() {
        String startName = (String) startBox.getSelectedItem();
        String endName = (String) endBox.getSelectedItem();
        if (startName == null || endName == null) {
            showError("Select both start and end nodes.");
            return;
        }
        Node start = resolveNode(startName);
        Node end = resolveNode(endName);
        if (start == null || end == null) {
            showError("Invalid start or end node.");
            return;
        }

        Navigator nav = new Navigator(map);
        Path path;
        String mode = (String) modeBox.getSelectedItem();
        if ("Safest".equals(mode)) {
            path = nav.findSafestPath(start, end);
            Path shortest = nav.findShortestPath(start, end);
            if (path.getNodes().equals(shortest.getNodes())) {
                pathArea.setText(path.toString() + "\nNote: Safest and Shortest are identical for this route.");
                return;
            }
        } else {
            path = nav.findShortestPath(start, end);
        }

        pathArea.setText(path.toString());
    }

    private Node resolveNode(String name) {
        for (Node node : nodeList) {
            if (node.getName().equalsIgnoreCase(name)) {
                return node;
            }
        }
        return null;
    }

    private String[] nodeNameArray() {
        return nodeList.stream().map(Node::getName).toArray(String[]::new);
    }

    private void saveAll() {
        try {
            storage.saveMap(map);
            storage.saveRescueTeams(rescueSystem.getTeams());
            storage.saveSurvivors(rescueSystem.getSurvivors());
            storage.saveHazards(managementSystem.getHazards());
            storage.saveAlerts(managementSystem.getAlerts());
        } catch (IOException e) {
            showError("Error saving data: " + e.getMessage());
        }
    }
}
