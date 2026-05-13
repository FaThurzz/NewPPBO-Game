package game.exception;

/**
 * CUSTOM EXCEPTION: InvalidMapException
 * Konsep OOP: Exception & Error Handling
 */
public class InvalidMapException extends Exception {
    public InvalidMapException(String message) {
        super(message);
    }
    public InvalidMapException(String message, Throwable cause) {
        super(message, cause);
    }
}
