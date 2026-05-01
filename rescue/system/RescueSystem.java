package rescue.system;

import rescue.model.*;

import java.util.ArrayList;
import java.util.List;

public class RescueSystem extends BaseSystem{
    private List<RescueTeam> teams;
    private List<Survivor> survivors;
    private Navigator navigator;
    private ForestMap1 map;

    public RescueSystem(ForestMap1 map) {
        super(map);
        this.map = map;
        this.teams = new ArrayList<>();
        this.survivors = new ArrayList<>();
        this.navigator = new Navigator(map);
    }

    public String getSystemName() {
        return "Rescue System";
    }

    public Path findExit(Survivor s) {
        Node exitNode = null;
        for (Node n : map.getAllNodes()) {
            if (n.getType() == NodeType.EXIT) {
                exitNode = n;
                break;
            }
        }
        if (exitNode == null) return new Path(new ArrayList<>(), 0, 0, false);
        return navigator.findSafestPath(s.getLocation(), exitNode);
    }

    public void dispatchTeam(RescueTeam t, Node n) {
        t.dispatch(n);
        System.out.println(t.getName() + " dispatched to " + n.getName());
    }

    public void trackSurvivor(Survivor s) {
        if (!survivors.contains(s)) survivors.add(s);
        System.out.println("Tracking: " + s);
    }

    public void addTeam(RescueTeam t) {teams.add(t);}

    public void addSurvivor(Survivor s) {survivors.add(s); }

    public List<Survivor> getSurvivors() {return survivors;}

    public List<RescueTeam> getTeams() {return teams; }
}
