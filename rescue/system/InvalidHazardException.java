package rescue.system;

public class InvalidHazardException extends Exception {
    public InvalidHazardException(String message) {
        super(message);
    }

    public InvalidHazardException(String message, Throwable cause) {
        super(message, cause);
    }
}
