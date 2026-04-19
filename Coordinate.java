public class Coordinate {
	private double x;
	private double y;
	
	public Coordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setX(double x) { this.x = x; }
	public double getX() { return x; }
	
	public void setY(double y) { this.y = y; }
	public double getY() { return y; }
	
	public double DistanceTo(Coordinate c) {
		double dx = this.x - c.x;
		double dy = this.y - c.y;
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public String ToString() {
		return "("+x+", "+y+")";
	}
}
