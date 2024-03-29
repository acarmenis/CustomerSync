package codingdojo;

import codingdojo.entities.Customer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class CustomerMatches {
    private Collection<Customer> duplicates = new ArrayList<>();
    private String matchTerm;
    private Customer customer;
    public boolean hasDuplicates() {
        return !duplicates.isEmpty();
    }
    public void addDuplicate(Customer duplicate) {
        duplicates.add(duplicate);
    }
}
