package rescue.system;

public class InvalidmapdataException extends Exception {
    public InvalidmapdataException(String message) {
        super(message);
    }

    public InvalidmapdataException(String message, Throwable cause) {
        super(message, cause);
    }
}
