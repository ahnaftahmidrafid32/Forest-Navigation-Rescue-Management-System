import java.util.*;

public class Navigator {
    private ForestMap1 map;

    public Navigator(ForestMap1 map) {
        this.map = map;
    }

    public Path findPath(Node start, Node end) {
        return findShortestPath(start, end);
    }

    public Path findShortestPath(Node start, Node end) {
        Map<Node, Integer> dist = new HashMap<>();
        Map<Node, Node> prev = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> dist.getOrDefault(n, Integer.MAX_VALUE)));

        for (Node n : map.getAllNodes()) dist.put(n, Integer.MAX_VALUE);
        dist.put(start, 0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (current == end) break;
            for (Edge e : current.getEdges()) {
                Node neighbor = e.getDestination();
                int newDist = dist.get(current) + e.getDistance();
                if (newDist < dist.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    dist.put(neighbor, newDist);
                    prev.put(neighbor, current);
                    pq.remove(neighbor);
                    pq.add(neighbor);
                }
            }
        }

        return buildPath(start, end, dist, prev, false);
    }

    public Path findSafestPath(Node start, Node end) {
        Path shortest = findShortestPath(start, end);
        Path safest = findSafestPathInternal(start, end, Collections.emptySet());
        if (shortest.getNodes().equals(safest.getNodes()) && shortest.getNodes().size() > 1) {
            Set<String> penalized = new HashSet<>();
            List<Node> nodes = shortest.getNodes();
            for (int i = 0; i < nodes.size() - 1; i++) {
                penalized.add(edgeKey(nodes.get(i), nodes.get(i + 1)));
            }
            Path alternative = findSafestPathInternal(start, end, penalized);
            if (!alternative.getNodes().isEmpty()) {
                return alternative;
            }
        }
        return safest;
    }

    private Path findSafestPathInternal(Node start, Node end, Set<String> penalizedEdges) {
        Map<Node, Integer> risk = new HashMap<>();
        Map<Node, Node> prev = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> risk.getOrDefault(n, Integer.MAX_VALUE)));

        for (Node n : map.getAllNodes()) risk.put(n, Integer.MAX_VALUE);
        risk.put(start, 0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (current == end) break;
            for (Edge e : current.getEdges()) {
                Node neighbor = e.getDestination();
                int newRisk = risk.get(current) + edgeRisk(current, neighbor, e, penalizedEdges);
                if (newRisk < risk.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    risk.put(neighbor, newRisk);
                    prev.put(neighbor, current);
                    pq.remove(neighbor);
                    pq.add(neighbor);
                }
            }
        }

        return buildPath(start, end, risk, prev, true);
    }

    private Path buildPath(Node start, Node end, Map<Node, Integer> costMap, Map<Node, Node> prev, boolean isSafest) {
        if (costMap.getOrDefault(end, Integer.MAX_VALUE) == Integer.MAX_VALUE) {
            return new Path(new ArrayList<>(), 0, 0, false);
        }

        LinkedList<Node> pathNodes = new LinkedList<>();
        Node current = end;
        while (current != null) {
            pathNodes.addFirst(current);
            current = prev.get(current);
        }

        int totalDist = 0, totalRisk = 0;
        List<Node> list = new ArrayList<>(pathNodes);
        for (int i = 0; i < list.size() - 1; i++) {
            Node a = list.get(i);
            for (Edge e : a.getEdges()) {
                if (e.getDestination() == list.get(i + 1)) {
                    totalDist += e.getDistance();
                    totalRisk += isSafest ? edgeRisk(a, e.getDestination(), e) : e.getRiskLevel();
                    break;
                }
            }
        }

        double multiplier = map.getWeather().getRiskMultiplier();
        int displayRisk = isSafest ? totalRisk : (int) (totalRisk * multiplier);
        boolean viable = map.getWeather().isNavigable() && displayRisk < 50;

        return new Path(list, totalDist, displayRisk, viable);
    }

    private int edgeRisk(Node from, Node to, Edge edge) {
        return edgeRisk(from, to, edge, Collections.emptySet());
    }

    private int edgeRisk(Node from, Node to, Edge edge, Set<String> penalizedEdges) {
        int risk = edge.getRiskLevel() * 10;
        if (from.getType() == NodeType.HAZARD_ZONE) risk += 50;
        if (to.getType() == NodeType.HAZARD_ZONE) risk += 50;
        if (penalizedEdges.contains(edgeKey(from, to))) risk += 1000;
        double multiplier = map.getWeather().getRiskMultiplier();
        return (int) Math.ceil(risk * multiplier);
    }

    private String edgeKey(Node from, Node to) {
        return from.getName() + "->" + to.getName();
    }
}

