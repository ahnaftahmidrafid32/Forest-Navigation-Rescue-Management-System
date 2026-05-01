package rescue.ui;

import rescue.model.*;
import rescue.system.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            System.out.println("Unhandled error: " + error.getMessage());
            error.printStackTrace();
        });

        FileStorage storage = new FileStorage(Paths.get("data"));
        ForestMap1 map = loadMap(storage);

        RescueSystem rescueSystem = new RescueSystem(map);
        ManagementSystem managementSystem = new ManagementSystem(map);

        loadOrSeed(storage, map, rescueSystem, managementSystem);

        warnIfWeatherNotNavigable(map);

        System.out.println("Forest map ready. Nodes: " + map.getAllNodes().size());
        System.out.println("Weather: " + map.getWeather().getSummary());
        System.out.println("Data folder: " + Paths.get("data").toAbsolutePath());
        System.out.println();

        System.out.println("Launching GUI...");
        RescueManagementSystemUI.launch(map, rescueSystem, managementSystem, storage);
    }

    private static int countEdges(ForestMap1 map) {
        int total = 0;
        for (Node n : map.getAllNodes()) {
            total += n.getEdges().size();
        }
        return total;
    }

    private static ForestMap1 loadMap(FileStorage storage) {
        ForestMap1 map = null;
        try {
            try {
                storage.ensureReady();
                map = storage.loadMap();
                if (map == null || map.getAllNodes().isEmpty() || countEdges(map) == 0) {
                    throw new InvalidmapdataException("Map data missing or incomplete");
                }
            } catch (RuntimeException e) {
                throw new InvalidmapdataException("Map data invalid", e);
            } catch (IOException e) {
                throw new DataStorageException("Unable to load map data", e);
            }
        } catch (InvalidmapdataException e) {
            map = buildMap();
            saveMapIfPossible(storage, map, "Rebuilt map from defaults");
            System.out.println("Map data issue: " + e.getMessage());
        } catch (DataStorageException e) {
            map = buildMap();
            System.out.println("Storage error: " + e.getMessage());
        } finally {
            if (map == null) {
                map = buildMap();
            }
        }
        return map;
    }

    private static ForestMap1 buildMap() {
        WeatherCondition weather = new WeatherCondition(Visibility.FOGGY, 15.0, 18.0, 30.0);
        ForestMap1 map = new ForestMap1(weather);

        Node entrance = new Node("Entrance", 0, 0, NodeType.CLEARING);
        Node trailA = new Node("Trail-A", 2, 3, NodeType.TRAIL);
        Node trailB = new Node("Trail-B", 5, 1, NodeType.TRAIL);
        Node river = new Node("River", 4, 5, NodeType.RIVER);
        Node campsite = new Node("Campsite", 7, 4, NodeType.CAMPSITE);
        Node shelter = new Node("Shelter", 6, 7, NodeType.SHELTER);
        Node hazardZone = new Node("HazardZone", 3, 7, NodeType.HAZARD_ZONE);
        Node exit = new Node("Exit", 10, 6, NodeType.EXIT);

        map.addNode(entrance);
        map.addNode(trailA);
        map.addNode(trailB);
        map.addNode(river);
        map.addNode(campsite);
        map.addNode(shelter);
        map.addNode(hazardZone);
        map.addNode(exit);

        map.connectNodes(entrance, trailA, 3, 1);
        map.connectNodes(entrance, trailB, 5, 2);
        map.connectNodes(trailA, river, 4, 3);
        map.connectNodes(trailA, campsite, 6, 2);
        map.connectNodes(trailB, campsite, 3, 1);
        map.connectNodes(river, shelter, 5, 4);
        map.connectNodes(river, hazardZone, 2, 8);
        map.connectNodes(campsite, shelter, 4, 2);
        map.connectNodes(campsite, exit, 5, 3);
        map.connectNodes(shelter, exit, 3, 2);
        map.connectNodes(hazardZone, shelter, 4, 9);

        return map;
    }

    private static void loadOrSeed(FileStorage storage, ForestMap1 map, RescueSystem rescueSystem,
                                  ManagementSystem managementSystem) {
        List<Survivor> survivors = safeLoadSurvivors(storage, map);
        List<RescueTeam> teams = safeLoadTeams(storage, map);
        List<Hazard> hazards = safeLoadHazards(storage, map);
        List<Alert> alerts = safeLoadAlerts(storage);

        boolean seeded = false;
        if (survivors.isEmpty()) {
            survivors = createDefaultSurvivors(map);
            seeded = true;
        }
        if (teams.isEmpty()) {
            teams = createDefaultTeams(map);
            seeded = true;
        }
        if (hazards.isEmpty()) {
            hazards = createDefaultHazards(map);
            seeded = true;
        }
        if (alerts.isEmpty()) {
            alerts = createDefaultAlerts();
            seeded = true;
        }

        if (seeded) {
            saveAll(storage, map, survivors, teams, hazards, alerts);
        }

        for (Survivor s : survivors) {
            rescueSystem.addSurvivor(s);
        }
        for (RescueTeam t : teams) {
            rescueSystem.addTeam(t);
        }
        managementSystem.getHazards().addAll(hazards);
        managementSystem.getAlerts().addAll(alerts);
    }

    private static List<Survivor> safeLoadSurvivors(FileStorage storage, ForestMap1 map) {
        List<Survivor> survivors = new ArrayList<>();
        try {
            survivors = storage.loadSurvivors(map);
        } catch (IOException e) {
            DataStorageException error = new DataStorageException("Unable to load survivors", e);
            System.out.println(error.getMessage());
        } catch (RuntimeException e) {
            InvalidmapdataException error = new InvalidmapdataException("Survivor data invalid", e);
            System.out.println(error.getMessage());
        } finally {
        }
        return survivors;
    }

    private static List<RescueTeam> safeLoadTeams(FileStorage storage, ForestMap1 map) {
        List<RescueTeam> teams = new ArrayList<>();
        try {
            teams = storage.loadRescueTeams(map);
        } catch (IOException e) {
            DataStorageException error = new DataStorageException("Unable to load teams", e);
            System.out.println(error.getMessage());
        } catch (RuntimeException e) {
            InvalidmapdataException error = new InvalidmapdataException("Team data invalid", e);
            System.out.println(error.getMessage());
        } finally {
        }
        return teams;
    }

    private static List<Hazard> safeLoadHazards(FileStorage storage, ForestMap1 map) {
        List<Hazard> hazards = new ArrayList<>();
        try {
            hazards = storage.loadHazards(map);
        } catch (IOException e) {
            DataStorageException error = new DataStorageException("Unable to load hazards", e);
            System.out.println(error.getMessage());
        } catch (RuntimeException e) {
            InvalidmapdataException error = new InvalidmapdataException("Hazard data invalid", e);
            System.out.println(error.getMessage());
        } finally {
        }
        return hazards;
    }

    private static List<Alert> safeLoadAlerts(FileStorage storage) {
        List<Alert> alerts = new ArrayList<>();
        try {
            alerts = storage.loadAlerts();
        } catch (IOException e) {
            DataStorageException error = new DataStorageException("Unable to load alerts", e);
            System.out.println(error.getMessage());
        } catch (RuntimeException e) {
            InvalidmapdataException error = new InvalidmapdataException("Alert data invalid", e);
            System.out.println(error.getMessage());
        } finally {
        }
        return alerts;
    }

    private static List<Survivor> createDefaultSurvivors(ForestMap1 map) {
        List<Survivor> survivors = new ArrayList<>();
        Node trailA = map.getNode("Trail-A");
        Node river = map.getNode("River");
        Node hazardZone = map.getNode("HazardZone");
        survivors.add(new Survivor("S001", "Alice", Condition.INJURED, trailA));
        survivors.add(new Survivor("S002", "Bob", Condition.STABLE, river));
        survivors.add(new Survivor("S003", "Charlie", Condition.CRITICAL, hazardZone));
        return survivors;
    }

    private static List<RescueTeam> createDefaultTeams(ForestMap1 map) {
        List<RescueTeam> teams = new ArrayList<>();
        Node entrance = map.getNode("Entrance");
        teams.add(new RescueTeam("T01", "Alpha Team", 4, entrance));
        teams.add(new RescueTeam("T02", "Bravo Team", 3, entrance));
        return teams;
    }

    private static List<Hazard> createDefaultHazards(ForestMap1 map) {
        List<Hazard> hazards = new ArrayList<>();
        Node hazardZone = map.getNode("HazardZone");
        Node river = map.getNode("River");
        if (hazardZone != null) {
            hazards.add(new Hazard(HazardType.WILDFIRE, 4, hazardZone, "Wildfire detected at HazardZone"));
            hazardZone.setType(NodeType.HAZARD_ZONE);
        }
        if (river != null) {
            hazards.add(new Hazard(HazardType.FLOOD, 3, river, "Flood warning at River"));
            river.setType(NodeType.HAZARD_ZONE);
        }
        return hazards;
    }

    private static List<Alert> createDefaultAlerts() {
        List<Alert> alerts = new ArrayList<>();
        alerts.add(new Alert("ALT-1", "Initial hazard scan complete", Priority.MEDIUM));
        alerts.add(new Alert("ALT-2", "Weather advisory issued", Priority.HIGH));
        return alerts;
    }

    private static void saveAll(FileStorage storage, ForestMap1 map, List<Survivor> survivors,
                                List<RescueTeam> teams, List<Hazard> hazards, List<Alert> alerts) {
        try {
            storage.saveMap(map);
            storage.saveSurvivors(survivors);
            storage.saveRescueTeams(teams);
            storage.saveHazards(hazards);
            storage.saveAlerts(alerts);
        } catch (IOException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }

    private static void saveMapIfPossible(FileStorage storage, ForestMap1 map, String reason) {
        try {
            storage.saveMap(map);
        } catch (IOException e) {
            System.out.println(reason + ": " + e.getMessage());
        }
    }

    private static void warnIfWeatherNotNavigable(ForestMap1 map) {
        try {
            if (map == null || map.getWeather() == null) {
                return;
            }
            if (!map.getWeather().isNavigable()) {
                throw new WeatherNotnavigableException("Weather conditions are not currently navigable");
            }
        } catch (WeatherNotnavigableException e) {
            System.out.println("Warning: " + e.getMessage());
        } finally {
        }
    }
}
