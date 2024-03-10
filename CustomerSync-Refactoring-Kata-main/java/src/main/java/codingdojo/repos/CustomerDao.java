package codingdojo.repos;

import codingdojo.entities.ShoppingList;
import codingdojo.entities.Customer;
import codingdojo.exceptions.CustomerCannotBeCreatedException;
import codingdojo.exceptions.CustomerCannotBeUpdatedException;
import codingdojo.exceptions.NotFoundException;
import codingdojo.exceptions.ShoppingListCannotBeUpdated;
import lombok.AllArgsConstructor;

/**
 * @author Andreas Karmenis on 3/9/2024
 * @project java
 */

@AllArgsConstructor
public class CustomerDao implements CustomerDataLayer {

    private final CustomerDataLayer customerDataLayer;

    @Override
    public Customer updateCustomerRecord(Customer customer) throws CustomerCannotBeUpdatedException{
        Customer retrievedCustomer = null;
        try{
            retrievedCustomer = customerDataLayer.updateCustomerRecord(customer);
        } catch(Exception e){
           throw new CustomerCannotBeUpdatedException(customer.getExternalId());
        }
        return retrievedCustomer;
    }

    @Override
    public Customer createCustomerRecord(Customer customer) throws CustomerCannotBeCreatedException {
        Customer createdCustomer = null;
        try{
            createdCustomer = customerDataLayer.createCustomerRecord(customer);
        } catch(Exception e){
            throw new CustomerCannotBeCreatedException(customer.getExternalId());
        }
        return createdCustomer;
    }

    @Override
    public void updateShoppingList(ShoppingList consumerShoppingList) throws ShoppingListCannotBeUpdated {
        try{
            customerDataLayer.updateShoppingList(consumerShoppingList);
        } catch(Exception e){
            throw new CustomerCannotBeCreatedException();
        }
    }

    @Override
    public Customer findByExternalId(String externalId) throws NotFoundException {
        Customer foundCustomer = null;
        try{
            foundCustomer = customerDataLayer.findByExternalId(externalId);
        } catch(Exception e){
            throw new NotFoundException(externalId);
        }
        return foundCustomer;
    }

    @Override
    public Customer findByMasterExternalId(String externalId) throws NotFoundException {
        Customer foundCustomer = null;
        try{
            foundCustomer = customerDataLayer.findByMasterExternalId(externalId);
        } catch(Exception e){
            throw new NotFoundException(externalId);
        }
        return foundCustomer;
    }

    @Override
    public Customer findByCompanyNumber(String companyNumber) throws NotFoundException {
        Customer foundCustomer = null;
        try{
            foundCustomer = customerDataLayer.findByCompanyNumber(companyNumber);
        } catch(Exception e){
            throw new NotFoundException(companyNumber);
        }
        return foundCustomer;
    }
}
