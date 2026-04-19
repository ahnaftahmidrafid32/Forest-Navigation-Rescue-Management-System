public class Edge {
    private int distance;
    private int riskLevel;
    private Node destination;

    public Edge(int distance, int riskLevel, Node destination) {
        this.distance = distance;
        this.riskLevel = riskLevel;
        this.destination = destination;
    }

    public int getDistance() { return distance; }
    public int getRiskLevel() { return riskLevel; }
    public Node getDestination() { return destination; }

    public String toString() {
        return "Edge : "+destination.getName()+"[distance = "+distance+", risk = "+riskLevel+"]";
    }
}
