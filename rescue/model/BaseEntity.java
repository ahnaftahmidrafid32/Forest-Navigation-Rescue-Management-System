package rescue.model;

public abstract class BaseEntity {
    private String id;
    private String name;
    private Node location;

    protected BaseEntity(String id, String name, Node location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public void setID(String id) {this.id = id;}
    public String getID() { return id;}
    public void setId(String id) { this.id = id; }
    public String getId() { return id; }

    public void setName(String name) {this.name = name;}
    public String getName() { return name;}

    public void setLocation(Node location) {this.location = location; }
    public Node getLocation() { return location; }

    public abstract String getEntityType();
}
