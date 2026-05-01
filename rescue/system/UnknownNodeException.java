package rescue.system;

public class UnknownNodeException extends Exception {
    public UnknownNodeException(String message) {
        super(message);
    }

    public UnknownNodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
