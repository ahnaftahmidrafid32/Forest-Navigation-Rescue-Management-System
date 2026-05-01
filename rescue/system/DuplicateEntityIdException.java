package rescue.system;

public class DuplicateEntityIdException extends Exception {
    public DuplicateEntityIdException(String message) {
        super(message);
    }

    public DuplicateEntityIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
