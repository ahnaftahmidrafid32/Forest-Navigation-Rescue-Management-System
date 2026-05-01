package rescue.model;

import java.util.*;

public class Node {
   private String name;
   private Coordinate coordinate;
   private List<Edge> edges;
   private NodeType type;

   public Node(String name, int x, int y) {
       this.name = name;
       this.coordinate = new Coordinate(x, y);
       this.edges = new ArrayList<>();
       this.type = NodeType.TRAIL;
   }

   public Node(String name, int x, int y, NodeType type) {
       this.name = name;
       this.coordinate = new Coordinate(x, y);
       this.edges = new ArrayList<>();
       this.type = type;
   }

   public void addEdge(Edge e) {
       edges.add(e);
   }

   public void addEdge(Node destination) {
       edges.add(new Edge(0, 0, destination));
   }

   public List<Edge> getEdges() {
       return edges;
   }

   public List<Node> getNeighbors() {
       List<Node> neighbors = new ArrayList<>();
       for (Edge e : edges) {
           neighbors.add(e.getDestination());
       }
       return neighbors;
   }

   public String getName() { return name; }
    public Coordinate getCoordinate() { return coordinate; }
    public NodeType getType() { return type; }
    public void setType(NodeType type) { this.type = type; }

   public String toString() {
       return name+" "+coordinate+" ["+type+")";
   }
}
