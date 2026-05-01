package rescue.model;

import java.util.ArrayList;
import java.util.List;

public class ForestMap1 {
    private List<Node> nodes;
    private WeatherCondition weather;

    public ForestMap1() {
        this.nodes = new ArrayList<>();
        this.weather = new WeatherCondition(Visibility.CLEAR, 0.0, 20.0, 10.0);
    }

    public ForestMap1(WeatherCondition weather) {
        this.nodes = new ArrayList<>();
        this.weather = weather;
    }

    public void addNode(Node n) {
        nodes.add(n);
    }

    public void connectNodes(Node a, Node b, int distance, int riskLevel) {
        Edge edgeAB = new Edge(distance, riskLevel, b);
        Edge edgeBA = new Edge(distance, riskLevel, a);
        a.addEdge(edgeAB);
        b.addEdge(edgeBA);
    }

    public Node getNode(String name) {
        for (Node n : nodes) {
            if (n.getName().equals(name)) return n;
        }
        return null;
    }

    public List<Node> getAllNodes() {
        return nodes;
    }

    public WeatherCondition getWeather() { return weather; }
    public void setWeather(WeatherCondition weather) { this.weather = weather; }
}
