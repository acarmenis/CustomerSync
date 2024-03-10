package codingdojo.exceptions;

/**
 * @author Andreas Karmenis on 3/9/2024
 * @project java
 */
public class CustomerCannotBeUpdatedException extends RuntimeException {
    public CustomerCannotBeUpdatedException(String message) {
        super("Customer with External id: "+message+", cannot be updated!");
    }
}
