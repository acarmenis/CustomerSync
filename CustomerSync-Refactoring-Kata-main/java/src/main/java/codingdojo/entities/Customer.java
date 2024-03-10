package codingdojo.entities;

import codingdojo.enums.CustomerType;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"externalId", "masterExternalId", "companyNumber"})
public class Customer {
    private String externalId;
    private String masterExternalId;
    private Address address;
    private String preferredStore;
    private int bonusPointsBalance;
    private List<ShoppingList> shoppingLists = new ArrayList<>();
    private String internalId;
    private String name;
    private CustomerType customerType;
    private String companyNumber;

    public void addShoppingList(ShoppingList consumerShoppingList) {
        if (CollectionUtils.isEmpty(this.shoppingLists)){
            this.shoppingLists = new ArrayList<>();
        }
        ArrayList<ShoppingList> newList = new ArrayList<ShoppingList>(this.shoppingLists);
        newList.add(consumerShoppingList);
        this.setShoppingLists(newList);
    }

}
