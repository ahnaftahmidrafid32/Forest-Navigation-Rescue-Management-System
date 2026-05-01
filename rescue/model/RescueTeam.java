package rescue.model;

public class RescueTeam extends BaseEntity implements Summarizable {
    private int size;
    private TeamStatus status;

    public RescueTeam(String teamId, String name, int size, Node currentNode) {
        super(teamId, name, currentNode);
        this.size = size;
        this.status = TeamStatus.IDLE;
    }

    public void dispatch(Node n) {
        setLocation(n);
        this.status = TeamStatus.DISPATCHED;
    }

    public void updateLocation(Node n) {
        setLocation(n);
    }

    public TeamStatus getStatus() { return status; }

    public void assignSurvivor(Survivor s) {
        this.status = TeamStatus.RESCUING;
        s.setFound(true);
        System.out.println(getName() + " assigned to survivor: " + s.getName());
    }

    public String getTeamId() { return getId(); }
    public int getSize() { return size; }
    public Node getCurrentNode() { return getLocation(); }
    public void setSize(int size) { this.size = size; }
    public void setStatus(TeamStatus status) { this.status = status; }


    public String getEntityType() {
        return "RescueTeam";
    }

    public String getSummary() {
        String loc = (getLocation() == null) ? "Unknown" : getLocation().getName();
        return "Team[" + getId() + "] " + getName() + " | Status: " + status + " @ " + loc;
    }

    public String toString() {
        return "Team[" + getId() + "] " + getName() + " | Size: " + size +
                " | Location: " + getLocation().getName() + " | Status: " + status;
    }
}

