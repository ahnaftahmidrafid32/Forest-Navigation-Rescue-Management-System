public abstract class BaseSystem {
    protected ForestMap1 map;

    protected BaseSystem(ForestMap1 map){
        this.map = map;
    }

    public ForestMap1 getMap() { return map;}

    public abstract String getSystemName();

    public String getStatus() {
        return getSystemName()+" ready";
    }
}
