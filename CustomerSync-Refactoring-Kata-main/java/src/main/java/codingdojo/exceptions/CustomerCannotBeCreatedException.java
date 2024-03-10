package codingdojo.exceptions;

/**
 * @author Andreas Karmenis on 3/9/2024
 * @project java
 */
public class CustomerCannotBeCreatedException extends RuntimeException {
    public CustomerCannotBeCreatedException() {}
    public CustomerCannotBeCreatedException(String message) {
        super("Customer with ExternalId: "+message+", cannot be created!");
    }
}
