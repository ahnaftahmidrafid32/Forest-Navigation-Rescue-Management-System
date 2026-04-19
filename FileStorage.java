import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileStorage {
    private static final String NODES_FILE = "nodes.txt";
    private static final String WEATHER_FILE = "weather.txt";
    private static final String TEAMS_FILE = "teams.txt";
    private static final String SURVIVORS_FILE = "survivors.txt";
    private static final String HAZARDS_FILE = "hazards.txt";
    private static final String ALERTS_FILE = "alerts.txt";

    private final Path root;

    public FileStorage(Path root) {
        this.root = root;
    }

    public void ensureReady() throws IOException {
        Files.createDirectories(root);
    }

    public void saveMap(ForestMap1 map) throws IOException {
        saveWeather(map.getWeather());
        saveNodes(map.getAllNodes());
    }

    public ForestMap1 loadMap() throws IOException {
        WeatherCondition weather = loadWeather();
        ForestMap1 map = (weather == null) ? new ForestMap1() : new ForestMap1(weather);
        for (Node node : loadNodes()) {
            map.addNode(node);
        }
        return map;
    }

    public void saveNodes(List<Node> nodes) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Node n : nodes) {
            Coordinate c = n.getCoordinate();
            double x = (c == null) ? 0.0 : c.getX();
            double y = (c == null) ? 0.0 : c.getY();

            lines.add(n.getName() + "," + x + "," + y + "," + n.getType().name());
        }
        writeAll(path(NODES_FILE), lines);
    }

    public List<Node> loadNodes() throws IOException {
        Path file = path(NODES_FILE);
        if (!Files.exists(file)) return Collections.emptyList();
        List<Node> nodes = new ArrayList<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty()) continue;
            String[] parts = split(line, 4);
            String name = parts[0];
            double x = parseDouble(parts[1]);
            double y = parseDouble(parts[2]);
            NodeType type = parts[3].isEmpty() ? NodeType.TRAIL : NodeType.valueOf(parts[3]);
            nodes.add(new Node(name, (int) x, (int) y, type));
        }
        return nodes;
    }

    public void saveWeather(WeatherCondition weather) throws IOException {
        if (weather == null) return;

        String line = weather.getVisibility().name() + "," +
                weather.getRainfall() + "," +
                weather.getTemperature() + "," +
                weather.getWindSpeed();
        writeAll(path(WEATHER_FILE), Collections.singletonList(line));
    }

    public WeatherCondition loadWeather() throws IOException {
        Path file = path(WEATHER_FILE);
        if (!Files.exists(file)) return null;
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty()) return null;
        String[] parts = split(lines.get(0), 4);
        Visibility visibility = Visibility.valueOf(parts[0]);
        double rainfall = parseDouble(parts[1]);
        double temperature = parseDouble(parts[2]);
        double windSpeed = parseDouble(parts[3]);
        return new WeatherCondition(visibility, rainfall, temperature, windSpeed);
    }


    public void saveRescueTeams(List<RescueTeam> teams) throws IOException {
        List<String> lines = new ArrayList<>();
        for (RescueTeam t : teams) {
            String nodeName = t.getCurrentNode() == null ? "" : t.getCurrentNode().getName();
            // id,name,size,status,nodeName
            lines.add(t.getId() + "," + t.getName() + "," + t.getSize() + "," + t.getStatus().name() + "," + nodeName);
        }
        writeAll(path(TEAMS_FILE), lines);
    }

    public List<RescueTeam> loadRescueTeams(ForestMap1 map) throws IOException {
        Path file = path(TEAMS_FILE);
        if (!Files.exists(file)) return Collections.emptyList();
        List<RescueTeam> teams = new ArrayList<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty()) continue;
            String[] parts = split(line, 5);
            String id = parts[0];
            String name = parts[1];
            int size = parseInt(parts[2]);
            TeamStatus status = TeamStatus.valueOf(parts[3]);
            String nodeName = parts[4];
            Node node = nodeName.isEmpty() ? null : map.getNode(nodeName);
            RescueTeam team = new RescueTeam(id, name, size, node);
            team.setStatus(status);
            teams.add(team);
        }
        return teams;
    }

    public void saveSurvivors(List<Survivor> survivors) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Survivor s : survivors) {
            String nodeName = s.getLocation() == null ? "" : s.getLocation().getName();
            // id,name,condition,found,nodeName
            lines.add(s.getId() + "," + s.getName() + "," + s.getCondition().name() + "," + s.isFound() + "," + nodeName);
        }
        writeAll(path(SURVIVORS_FILE), lines);
    }

    public List<Survivor> loadSurvivors(ForestMap1 map) throws IOException {
        Path file = path(SURVIVORS_FILE);
        if (!Files.exists(file)) return Collections.emptyList();
        List<Survivor> survivors = new ArrayList<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty()) continue;
            String[] parts = split(line, 5);
            String id = parts[0];
            String name = parts[1];
            Condition condition = Condition.valueOf(parts[2]);
            boolean found = Boolean.parseBoolean(parts[3]);
            String nodeName = parts[4];
            Node node = nodeName.isEmpty() ? null : map.getNode(nodeName);
            Survivor survivor = new Survivor(id, name, condition, node);
            survivor.setFound(found);
            survivors.add(survivor);
        }
        return survivors;
    }

    public void saveHazards(List<Hazard> hazards) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Hazard h : hazards) {
            String nodeName = h.getNode() == null ? "" : h.getNode().getName();

            lines.add(h.getType().name() + "," + h.getSeverity() + "," + nodeName + "," + h.isActive() + "," + h.getDescription());
        }
        writeAll(path(HAZARDS_FILE), lines);
    }

    public List<Hazard> loadHazards(ForestMap1 map) throws IOException {
        Path file = path(HAZARDS_FILE);
        if (!Files.exists(file)) return Collections.emptyList();
        List<Hazard> hazards = new ArrayList<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty()) continue;
            String[] parts = split(line, 5);
            HazardType type = HazardType.valueOf(parts[0]);
            int severity = parseInt(parts[1]);
            String nodeName = parts[2];
            boolean active = Boolean.parseBoolean(parts[3]);
            String description = parts[4];
            Node node = nodeName.isEmpty() ? null : map.getNode(nodeName);
            Hazard hazard = new Hazard(type, severity, node, description);
            if (!active) hazard.deactivate();
            hazards.add(hazard);
        }
        return hazards;
    }

    public void saveAlerts(List<Alert> alerts) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Alert a : alerts) {
            // id,message,priority,timestamp,active
            lines.add(a.getAlertID() + "," + a.getMessage() + "," + a.getPriority().name() + "," + a.getTimestamp() + "," + a.IsActive());
        }
        writeAll(path(ALERTS_FILE), lines);
    }

    public List<Alert> loadAlerts() throws IOException {
        Path file = path(ALERTS_FILE);
        if (!Files.exists(file)) return Collections.emptyList();
        List<Alert> alerts = new ArrayList<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty()) continue;
            String[] parts = split(line, 5);
            String id = parts[0];
            String message = parts[1];
            Priority priority = Priority.valueOf(parts[2]);
            boolean active = Boolean.parseBoolean(parts[4]);
            Alert alert = new Alert(id, message, priority);
            if (!active) alert.dismiss();
            alerts.add(alert);
        }
        return alerts;
    }

    private Path path(String fileName) {
        return root.resolve(fileName);
    }

    private void writeAll(Path file, List<String> lines) throws IOException {
        Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // Very simple split: we do NOT support commas inside values.
    private static String[] split(String line, int expectedParts) {
        String[] parts = line.split(",", -1);
        if (parts.length >= expectedParts) return parts;
        String[] padded = new String[expectedParts];
        for (int i = 0; i < expectedParts; i++) {
            padded[i] = i < parts.length ? parts[i] : "";
        }
        return padded;
    }

    private static int parseInt(String value) {
        if (value == null || value.trim().isEmpty()) return 0;
        return Integer.parseInt(value.trim());
    }

    private static double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) return 0.0;
        return Double.parseDouble(value.trim());
    }
}
