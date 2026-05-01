package rescue.system;

public class WeatherNotnavigableException extends Exception {
    public WeatherNotnavigableException(String message) {
        super(message);
    }

    public WeatherNotnavigableException(String message, Throwable cause) {
        super(message, cause);
    }
}
