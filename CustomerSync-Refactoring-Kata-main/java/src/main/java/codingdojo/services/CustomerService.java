package codingdojo.services;

import codingdojo.CustomerMatches;
import codingdojo.entities.ShoppingList;
import codingdojo.entities.Customer;
import codingdojo.repos.CustomerDao;
import lombok.AllArgsConstructor;

import java.util.Objects;

/**
 * @author Andreas Karmenis on 3/9/2024
 * @project java
 */
@AllArgsConstructor
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerMatches loadCompanyCustomer(String externalId, String companyNumber) {
        CustomerMatches matches = new CustomerMatches();
        Customer legalEntity = this.customerDao.findByExternalId(externalId);
        if (Objects.nonNull(legalEntity)) {
            matches.setCustomer(legalEntity);
            matches.setMatchTerm("ExternalId");
            // here it tries to get the same entity since as arg is passed the same externalId
            Customer matchByMasterId = this.customerDao.findByMasterExternalId(externalId);
            if (Objects.nonNull(matchByMasterId)) matches.addDuplicate(matchByMasterId);
        } else {
            legalEntity = this.customerDao.findByCompanyNumber(companyNumber);
            if (Objects.nonNull(legalEntity)) {
                matches.setCustomer(legalEntity);
                matches.setMatchTerm("CompanyNumber");
            }
        }
        return matches;
    }

    public CustomerMatches loadPersonCustomer(String externalId) {
        CustomerMatches matches = new CustomerMatches();
        Customer matchByPersonalNumber = this.customerDao.findByExternalId(externalId);
        matches.setCustomer(matchByPersonalNumber);
        if (Objects.nonNull(matchByPersonalNumber)) matches.setMatchTerm("ExternalId");
        return matches;
    }

    public Customer updateCustomerRecord(Customer customer) {
        return customerDao.updateCustomerRecord(customer);
    }

    public Customer createCustomerRecord(Customer customer) {
        return customerDao.createCustomerRecord(customer);
    }

    public void updateShoppingList(Customer customer, ShoppingList consumerShoppingList) {
        customer.addShoppingList(consumerShoppingList);
        customerDao.updateShoppingList(consumerShoppingList);
        customerDao.updateCustomerRecord(customer);
    }
}
