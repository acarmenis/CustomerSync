package codingdojo.services;

import codingdojo.CustomerMatches;
import codingdojo.ExternalCustomer;
import codingdojo.entities.ShoppingList;
import codingdojo.entities.Customer;
import codingdojo.enums.CustomerType;
import codingdojo.exceptions.ConflictException;
import codingdojo.repos.CustomerDao;
import codingdojo.repos.CustomerDataLayer;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;

public class CustomerSync {

    private final CustomerService customerService;

    public CustomerSync(CustomerDataLayer customerDataLayer) {
        // this -> Constructor chaining refers to the ability to call
        // a constructor inside another constructor.
        this(new CustomerService(new CustomerDao(customerDataLayer)));
    }

    // is being called from the above CustomerSync constructor
    public CustomerSync(CustomerService customerService) {
        this.customerService = customerService;
    }

    public boolean syncWithDataLayer(ExternalCustomer externalCustomer) {
        CustomerMatches customerMatches = (externalCustomer.isCompany())
                ? loadCompany(externalCustomer)
                : loadPerson(externalCustomer);
        Customer customer = customerMatches.getCustomer();
        if (Objects.isNull(customer)) {
            customer = Customer.builder()
                    .externalId(externalCustomer.getExternalId())
                    .masterExternalId(externalCustomer.getExternalId())
                    .build();
        }
        populateFields(externalCustomer, customer);
        boolean created = false;
        if (Objects.isNull(customer.getInternalId())) {
            customer = createCustomer(customer);
            created = true;
        } else {
            updateCustomer(customer);
        }
        updateContactInfo(externalCustomer, customer);
        if (customerMatches.hasDuplicates()) {
            customerMatches.getDuplicates().forEach(duplicate -> updateDuplicate(externalCustomer, duplicate));
        }
        updateRelations(externalCustomer, customer);
        updatePreferredStore(externalCustomer, customer);
        return created;
    }

    private void updateRelations(ExternalCustomer externalCustomer, Customer customer) {
        List<ShoppingList> consumerShoppingLists = externalCustomer.getShoppingLists();
        if(CollectionUtils.isNotEmpty(consumerShoppingLists)){
            consumerShoppingLists.forEach(
                    shoppingList -> this.customerService.updateShoppingList(customer, shoppingList)
            );
        }
    }

    private Customer updateCustomer(Customer customer) {
        return this.customerService.updateCustomerRecord(customer);
    }

    private void updateDuplicate(ExternalCustomer externalCustomer, Customer duplicate) {
        if (Objects.isNull(duplicate)) {
            duplicate = Customer.builder()
                    .externalId(externalCustomer.getExternalId())
                    .masterExternalId(externalCustomer.getExternalId())
                    .build();
        }
        duplicate.setName(externalCustomer.getName());
        if (Objects.isNull(duplicate.getInternalId())) {
            createCustomer(duplicate);
        } else {
            updateCustomer(duplicate);
        }
    }

    private void updatePreferredStore(ExternalCustomer externalCustomer, Customer customer) {
        customer.setPreferredStore(externalCustomer.getPreferredStore());
    }

    private Customer createCustomer(Customer customer) {
        return this.customerService.createCustomerRecord(customer);
    }

    private void populateFields(ExternalCustomer externalCustomer, Customer customer) {
        customer.setName(externalCustomer.getName());
        if (externalCustomer.isCompany()) {
            customer.setCompanyNumber(externalCustomer.getCompanyNumber());
            customer.setCustomerType(CustomerType.COMPANY);
        } else {
            customer.setCustomerType(CustomerType.PERSON);
        }
    }

    private void updateContactInfo(ExternalCustomer externalCustomer, Customer customer) {
        customer.setAddress(externalCustomer.getAddress());
    }

    public CustomerMatches loadCompany(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();
        final String companyNumber = externalCustomer.getCompanyNumber();
        CustomerMatches customerMatches = customerService.loadCompanyCustomer(externalId, companyNumber);
        if (Objects.nonNull(customerMatches.getCustomer()) &&
                !Objects.equals(CustomerType.COMPANY, customerMatches.getCustomer().getCustomerType())){
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a company");
        }
        if ("ExternalId".equals(customerMatches.getMatchTerm())) {
            String customerCompanyNumber = customerMatches.getCustomer().getCompanyNumber();
            if (!companyNumber.equals(customerCompanyNumber)) {
                customerMatches.getCustomer().setMasterExternalId(null);
                customerMatches.addDuplicate(customerMatches.getCustomer());
                customerMatches.setCustomer(null);
                customerMatches.setMatchTerm(null);
            }
        } else if ("CompanyNumber".equals(customerMatches.getMatchTerm())) {
            String customerExternalId = customerMatches.getCustomer().getExternalId();
            if (Objects.nonNull(customerExternalId) && !externalId.equals(customerExternalId)) {
                throw new ConflictException("Existing customer for externalCustomer " + companyNumber + " doesn't match external id " + externalId + " instead found " + customerExternalId );
            }
            Customer customer = customerMatches.getCustomer();
            customer.setExternalId(externalId);
            customer.setMasterExternalId(externalId);
            customerMatches.addDuplicate(null);
        }
        return customerMatches;
    }

    public CustomerMatches loadPerson(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();
        CustomerMatches customerMatches = customerService.loadPersonCustomer(externalId);
        if (Objects.nonNull(customerMatches.getCustomer())) {
            if (!Objects.equals(CustomerType.PERSON, customerMatches.getCustomer().getCustomerType())) {
                throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a person");
            }
            if (!"ExternalId".equals(customerMatches.getMatchTerm())) {
                Customer customer = customerMatches.getCustomer();
                customer.setExternalId(externalId);
                updateBonusPointsBalance( customer, externalCustomer);
                customer.setMasterExternalId(externalId);
            }
            updateBonusPointsBalance( customerMatches.getCustomer(), externalCustomer);
        }
        return customerMatches;
    }

    private void updateBonusPointsBalance( Customer customer, ExternalCustomer externalCustomer){
        if (externalCustomer.getBonusPointsBalance() != customer.getBonusPointsBalance()){
            customer.setBonusPointsBalance(externalCustomer.getBonusPointsBalance());
        }
    }
}
