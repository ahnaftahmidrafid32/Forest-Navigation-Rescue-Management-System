public class Survivor extends BaseEntity implements Summarizable {
    private Condition condition;
    private boolean isFound;

    public Survivor(String id, String name, Condition condition, Node location) {
        super(id, name, location);
        this.condition = condition;
        this.isFound = false;
    }

    public void updateCondition(Condition c) {this.condition = c;}
    public void setFound(boolean b) {this.isFound = b;}

    public Condition getCondition() { return condition;}
    public boolean isFound() {return isFound; }
    public void setLocation(Node location) {super.setLocation(location); }

    public String getEntityType() {
        return "Survivor";
    }

    public String getSummary() {
        String loc = (getLocation() == null) ? "Unknown" : getLocation().getName();
        return getEntityType() + "[" + getId() + "] " + getName() + " | " + condition + " @ " + loc;
    }

    public String toString() {
        return "Survivor[" + getId() + "] " + getName() + " | Condition: " + condition + " | Location: " + getLocation().getName() + " | Found: " + isFound;
    }
}

