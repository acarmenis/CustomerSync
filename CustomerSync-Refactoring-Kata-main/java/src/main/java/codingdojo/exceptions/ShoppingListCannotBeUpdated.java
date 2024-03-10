package codingdojo.exceptions;

/**
 * @author Andreas Karmenis on 3/9/2024
 * @project java
 */
public class ShoppingListCannotBeUpdated extends RuntimeException {
    public ShoppingListCannotBeUpdated(String message) {
        super("ShoppingList: "+message+", cannot be updated!");
    }
}
