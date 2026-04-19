import java.util.ArrayList;
import java.util.List;

public class ManagementSystem extends BaseSystem {
    private List<Alert> alerts;
    private List<Hazard> hazards;
    private int alertCounter = 1;
    private int reportCounter = 1;

    public ManagementSystem(ForestMap1 map) {
        super(map);
        this.alerts = new ArrayList<>();
        this.hazards = new ArrayList<>();
    }

    public String getSystemName() {
        return "Management System";
    }

    public String toString() {
        return "Management System";
    }

    public void markDanger(Node n, HazardType type) {
        n.setType(NodeType.HAZARD_ZONE);
        Hazard h = new Hazard(type,3, n, type+" detected at "+ n.getName());
        hazards.add(h);
        sendAlert("Hazard marked at "+ n.getName()+" : "+ type, Priority.HIGH);
        System.out.println("Danger Marked : " + h);
    }

    public Report generateReport() {
        return generateReport(0);
    }

    public Report generateReport(int survivorCount) {
        int activeHazards = 0;
        for (Hazard h : hazards) if (h.isActive()) activeHazards++;

        StringBuilder content = new StringBuilder();
        content.append("Active Hazards:\n");
        for (Hazard h : hazards) {
            content.append("  - ").append(h).append("\n");
        }
        content.append("Active Alerts:\n");
        for (Alert a : alerts) {
            if (a.IsActive()) content.append("  - ").append(a).append("\n");
        }
        content.append("Weather: ").append("Unknown");

        Report r = new Report("RPT-" + reportCounter++, activeHazards, survivorCount, content.toString());
        System.out.println(r.getSummary());
        return r;
    }

    public void sendAlert(String message, Priority priority) {
        Alert a = new Alert("ALT-" + alertCounter++, message, priority);
        alerts.add(a);
        System.out.println("ALERT: " + a);
    }

    public List<Hazard> getHazards() { return hazards;}
    public List<Alert> getAlerts() {return alerts; }
}
