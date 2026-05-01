package rescue.model;

public class Hazard {
    private HazardType type;
    private int severity;
    private Node node;
    private boolean isActive;
    private String description;

    public Hazard(HazardType type, int severity, Node node, String description) {
        this.type = type;
        this.severity = Math.min(5, Math.max(1, severity));
        this.node = node;
        this.isActive = true;
        this.description = description;
    }

    public void activate() { this.isActive = true; }
    public void deactivate() { this.isActive = false;}
    public int getSeverity() { return severity;}
    public HazardType getType() {return type;}
    public Node getNode() {return node;}
    public boolean isActive() { return isActive;}
    public String getDescription() { return description; }

    public String toString() {
        return type + " at " + node.getName() + " [Severity = " + severity + " , Active = " + isActive + " ]";
    }
}
