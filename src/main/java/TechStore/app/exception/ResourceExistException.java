package TechStore.app.exception;

public class ResourceExistException extends RuntimeException {
    public ResourceExistException(String msg) {
        super(msg);
    }
}
