package codingdojo.repos;

import codingdojo.entities.ShoppingList;
import codingdojo.entities.Customer;
import codingdojo.exceptions.CustomerCannotBeCreatedException;
import codingdojo.exceptions.CustomerCannotBeUpdatedException;
import codingdojo.exceptions.NotFoundException;
import codingdojo.exceptions.ShoppingListCannotBeUpdated;

public interface CustomerDataLayer {
    Customer updateCustomerRecord(Customer customer) throws CustomerCannotBeUpdatedException;
    Customer createCustomerRecord(Customer customer) throws CustomerCannotBeCreatedException;
    void updateShoppingList(ShoppingList consumerShoppingList) throws ShoppingListCannotBeUpdated;
    Customer findByExternalId(String externalId) throws NotFoundException;
    Customer findByMasterExternalId(String externalId) throws NotFoundException;
    Customer findByCompanyNumber(String companyNumber) throws NotFoundException;
}
