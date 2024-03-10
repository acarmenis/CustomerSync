package codingdojo;

import codingdojo.entities.Address;
import codingdojo.entities.Customer;
import codingdojo.entities.ShoppingList;
import codingdojo.enums.CustomerType;
import codingdojo.repos.CustomerDao;
import codingdojo.repos.CustomerDataLayer;
import codingdojo.services.CustomerSync;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerSyncTest {
    @Test
    public void syncCompanyByExternalIdForCustomerTypePerson() {
        String externalId = "33456";
        String internalId = "99999";

        Address address = Address.builder().street("127 main st").city("Nolvik").postalCode("SE-127 49").build();
        ShoppingList shoppingList = new ShoppingList("Knit scrunchy", "SIXTIES WATCH YGME600-07", "FERENDI WOMEN'S BRACELET GA00855851");

        ExternalCustomer externalCustomer = ExternalCustomer.builder()
                .address(address)
                .name("Marilyn Monroe")
                .shoppingLists(Arrays.asList(shoppingList))
                .externalId(externalId)
                .bonusPointsBalance(1234)
                .build();

        assertNotNull(externalCustomer);
        assertEquals(1, externalCustomer.getShoppingLists().size());
        assertNotNull(externalCustomer.getShoppingLists().get(0).getProducts());
        assertEquals(3, externalCustomer.getShoppingLists().get(0).getProducts().size());
        assertEquals(1234, externalCustomer.getBonusPointsBalance());

        Customer customer = Customer.builder()
                .internalId(internalId)
                .externalId(externalId)
                .customerType(CustomerType.PERSON)
                .bonusPointsBalance(54)
                .build();

        assertNotNull(customer);
        assertEquals(internalId, customer.getInternalId());
        assertEquals(CustomerType.PERSON, customer.getCustomerType());
        assertEquals(54, customer.getBonusPointsBalance());

        CustomerDataLayer db = mock(CustomerDao.class);
        when(db.findByExternalId(externalId)).thenReturn(customer);
        CustomerSync sut = new CustomerSync(db);

        // ACT
        boolean created = sut.syncWithDataLayer(externalCustomer);

        // ASSERT
        assertFalse(created);

        ArgumentCaptor<Customer> argument = ArgumentCaptor.forClass(Customer.class);
        verify(db, atLeastOnce()).updateCustomerRecord(argument.capture());

        Customer updatedCustomer = argument.getValue();
        assertEquals(externalCustomer.getName(), updatedCustomer.getName());
        assertEquals(externalCustomer.getExternalId(), updatedCustomer.getExternalId());
        assertNull(updatedCustomer.getMasterExternalId());
        assertEquals(externalCustomer.getAddress(), updatedCustomer.getAddress());
        assertEquals(externalCustomer.getShoppingLists(), updatedCustomer.getShoppingLists());
        assertEquals(CustomerType.PERSON, updatedCustomer.getCustomerType());
        assertNull(updatedCustomer.getPreferredStore());
        assertEquals(externalCustomer.getBonusPointsBalance(), updatedCustomer.getBonusPointsBalance());

    }



    @Test
    public void syncCompanyByExternalIdForCustomerTypeCompany() {
        String externalId = "12345";
        String internalId = "45435";

        Address address = Address.builder().street("123 main st").city("Helsingborg").postalCode("SE-123 45").build();
        ShoppingList shoppingList = new ShoppingList("lipstick", "blusher");

        ExternalCustomer externalCustomer = ExternalCustomer.builder()
                .address(address)
                .name("Acme Inc.")
                .shoppingLists(Arrays.asList(shoppingList))
                .externalId(externalId)
                .companyNumber("470813-8895")
                .build();

        assertNotNull(externalCustomer);
        assertEquals(1, externalCustomer.getShoppingLists().size());
        assertNotNull(externalCustomer.getShoppingLists().get(0).getProducts());
        assertEquals(2, externalCustomer.getShoppingLists().get(0).getProducts().size());

        Customer customer = Customer.builder()
                .internalId(internalId)
                .externalId(externalId)
                .customerType(CustomerType.COMPANY)
                .companyNumber(externalCustomer.getCompanyNumber())
                .build();

        assertNotNull(customer);
        assertEquals(internalId, customer.getInternalId());
        assertEquals(CustomerType.COMPANY, customer.getCustomerType());

        CustomerDataLayer db = mock(CustomerDao.class);
        when(db.findByExternalId(externalId)).thenReturn(customer);
        CustomerSync sut = new CustomerSync(db);
        // ACT
       boolean created = sut.syncWithDataLayer(externalCustomer);

        // ASSERT
        assertFalse(created);

        ArgumentCaptor<Customer> argument = ArgumentCaptor.forClass(Customer.class);
        verify(db, atLeastOnce()).updateCustomerRecord(argument.capture());

        Customer updatedCustomer = argument.getValue();
        assertEquals(externalCustomer.getName(), updatedCustomer.getName());
        assertEquals(externalCustomer.getExternalId(), updatedCustomer.getExternalId());
        assertNull(updatedCustomer.getMasterExternalId());
        assertEquals(externalCustomer.getCompanyNumber(), updatedCustomer.getCompanyNumber());
        assertEquals(externalCustomer.getAddress(), updatedCustomer.getAddress());
        assertEquals(externalCustomer.getShoppingLists(), updatedCustomer.getShoppingLists());
        assertEquals(CustomerType.COMPANY, updatedCustomer.getCustomerType());
        assertNull(updatedCustomer.getPreferredStore());
    }


}
