package codingdojo.exceptions;

/**
 * @author Andreas Karmenis on 3/9/2024
 * @project java
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
