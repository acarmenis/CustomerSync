package codingdojo;

import codingdojo.entities.Address;
import codingdojo.entities.ShoppingList;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ExternalCustomer {
    private Address address;
    private String name;
    private String preferredStore;
    private List<ShoppingList> shoppingLists = new ArrayList<>();
    private int bonusPointsBalance;
    private String externalId;
    private String companyNumber;
    public boolean isCompany() {
        return companyNumber != null;
    }
}
