package propra2.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.*;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import propra2.Security.service.CustomerService;
import propra2.Security.service.RegistrationService;
import propra2.Security.validator.CustomerValidator;
import propra2.database.Customer;
import propra2.database.Product;
import propra2.handler.SearchProductHandler;
import propra2.model.Address;
import propra2.repositories.CustomerRepository;
import propra2.repositories.OrderProcessRepository;
import propra2.repositories.ProductRepository;
import propra2.repositories.TransactionRepository;

import java.util.Arrays;

import static com.sun.javaws.JnlpxArgs.verify;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration
        //@WithMockUser(username="admin",roles={"USER","ADMIN"})
public class SharingIsCaringControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CustomerRepository customerRepository;

    @MockBean
    OrderProcessRepository orderProcessRepository;

    @MockBean
    ProductRepository productRepository;

    @MockBean
    TransactionRepository transactionRepository;

    @MockBean
    SearchProductHandler searchProductHandler;

    @MockBean
    CustomerValidator customerValidator;

    @MockBean
    RegistrationService registrationService;

    @MockBean
    CustomerService customerService;


    /*********************************************************************************
     PRODUCTS
     **********************************************************************************/


    @Test
   // @WithMockUser
    public void searchProductsTest() throws Exception{
        Customer customer = new Customer();
        customer.setCustomerId(1234L);
        customer.setUsername("Kevin");
        customer.setMail("kevin@istdumm.de");
        Address address = new Address();
        address.setStreet("Baumstrasse");
        address.setHouseNumber(7);
        address.setPostCode(2345);
        address.setCity("Erkrath");
        customer.setAddress(address);

        Product product1 = new Product();
        product1.setTitle("Baumstamm");
        product1.setId(34L);

        Product product2 = new Product();
        product1.setTitle("Baumhaus");
        product2.setId(56L);

        Product product3 = new Product();
        product1.setTitle("Gartenhaus");
        product3.setId(78L);


        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));
        Mockito.when(searchProductHandler.getSearchedProducts("baum", "borrowed", customer, productRepository)).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(get("/searchProducts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("productsSearch"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", customer))
                .andExpect(MockMvcResultMatchers.model().attribute("query", "borrowed" ))
                .andExpect(MockMvcResultMatchers.model().attribute("products",hasItem(
                        allOf(
                                hasProperty("id", is("34L")),
                                hasProperty("title", is("Baumstamm"))
                        )
                )))
                .andExpect(MockMvcResultMatchers.model().attribute("products", hasItem(
                        allOf(
                                hasProperty("id", is("56L")),
                                hasProperty("title", is("Baumhaus"))
                        )
                )));

    }


}
