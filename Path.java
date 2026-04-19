import java.util.List;

public class Path {
    private List<Node> nodes;
    private int totalDistance;
    private int riskScore;
    private boolean isViable;

    public Path(List<Node> nodes, int totalDistance, int riskScore, boolean isViable) {
        this.nodes = nodes;
        this.totalDistance = totalDistance;
        this.riskScore = riskScore;
        this.isViable = isViable;
    }

    public int getTotalDistance() {return totalDistance;}
    public int getRiskScore() {return riskScore;}
    public boolean isViable() {return isViable;}
    public List<Node> getNodes() { return nodes;}

    public String toString() {
        if(nodes == null || nodes.isEmpty())
            return "No Path Found";

        StringBuilder sb = new StringBuilder("Path : ");
        for(int i=0;i<nodes.size();i++) {
            sb.append(nodes.get(i).getName());
            if(i < nodes.size()-1) sb.append(" -> ");
        }

        sb.append(" | Distance : ").append(totalDistance);
        sb.append(" | Risk : ").append(riskScore);
        sb.append(" | Viable : ").append(isViable);

        return sb.toString();
    }
}
