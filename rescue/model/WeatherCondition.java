package rescue.model;

public class WeatherCondition {
    private Visibility visibility;
    private double rainfall;
    private double temperature;
    private double windSpeed;

    public WeatherCondition(Visibility visibility, double rainfall, double temperature, double windSpeed) {
        this.visibility = visibility;
        this.rainfall = rainfall;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
    }

    public boolean isNavigable() {
        return visibility != Visibility.ZERO && windSpeed < 80.0 && rainfall < 50.0;
    }

    public String getSummary() {
        return "Visibility: " + visibility +
                " | Rainfall: " + rainfall + "mm" +
                " | Temp: " + temperature + "°C" +
                " | Wind: " + windSpeed + "km/h";
    }

    public double getRiskMultiplier() {
        double multiplier = 1.0;
        switch (visibility) {
            case FOGGY:    multiplier += 0.2; break;
            case LOW:      multiplier += 0.5; break;
            case VERY_LOW: multiplier += 0.8; break;
            case ZERO:     multiplier += 1.5; break;
            default:       break;
        }
        if (rainfall > 20) multiplier += 0.3;
        if (windSpeed > 50) multiplier += 0.4;
        return multiplier;
    }

    public Visibility getVisibility() { return visibility; }
    public double getRainfall() { return rainfall; }
    public double getTemperature() { return temperature; }
    public double getWindSpeed() { return windSpeed; }

    public void setVisibility(Visibility visibility) { this.visibility = visibility; }
    public void setRainfall(double rainfall) { this.rainfall = rainfall; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
}

