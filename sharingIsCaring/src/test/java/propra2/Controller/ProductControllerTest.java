package propra2.Controller;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import propra2.Security.service.CustomerService;
import propra2.Security.service.RegistrationService;
import propra2.Security.validator.CustomerValidator;
import propra2.database.Customer;
import propra2.database.Product;
import propra2.handler.OrderProcessHandler;
import propra2.handler.SearchProductHandler;
import propra2.model.Address;
import propra2.model.ProPayAccount;
import propra2.repositories.*;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ProductController.class)
//@ContextConfiguration
public class ProductControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    NotificationRepository notificationRepository;

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

    @MockBean
    OrderProcessHandler orderProcessHandler;

    Customer bendisposto = new Customer();
    Customer customer = new Customer();
    Customer owner = new Customer();
    Customer admin = new Customer();
    Product product1 = new Product();
    Product product2 = new Product();

    @Before
    public void setup(){
        Address address = new Address();
        address.setStreet("Unistraße");
        address.setPostcode(34509);
        address.setHouseNumber(1);
        address.setCity("Ddorf");

        ProPayAccount account = new ProPayAccount();
        account.setAccount("Zoidberg");
        account.setAmount(100);

        bendisposto.setUsername("Zoidberg");
        bendisposto.setCustomerId(2L);
        bendisposto.setMail("bendisposto@web.de");
        bendisposto.setAddress(address);
        bendisposto.setPassword("propra2");
        bendisposto.setProPay(account);



        admin.setUsername("admin");
        admin.setMail("admin@admin.de");
        admin.setPassword("adminPass");

        customer.setCustomerId(111L);
        customer.setUsername("Kevin");
        customer.setMail("kevin@istdumm.de");
        customer.setPassword("Baumhaus");
        customer.setRole("USER");


        owner.setCustomerId(113L);
        owner.setUsername("Lukas");
        owner.setMail("lukas@web.de");
        owner.setRole("USER");


        product1.setTitle("Baumstamm");
        product1.setId(34L);
        product1.setAvailable(false);
        product1.setOwner(owner);

        /*
        product2.setTitle("Baumlaube");
        product2.setId(56L);
        product2.setAvailable(false);
        */
    }


    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void showProductsTest() throws Exception{

        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));

        mvc.perform(get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("productsSearch"));



    }

    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void searchProductsTest() throws Exception{

        Product product3 = new Product();
        product3.setTitle("Gartenhaus");
        product3.setId(78L);
        product3.setAvailable(false);

        List<Product> products = new ArrayList<>();
        products.add(product3);
        products.add(product1);


        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));
        Mockito.when(searchProductHandler.getSearchedProducts("Baum", "borrowed", customer, productRepository)).thenReturn(products);

        mvc.perform(get("/searchProducts")
                .param("query", "Baum")
                .param("filter", "borrowed"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("productsSearch"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", customer))
                .andExpect(MockMvcResultMatchers.model().attribute("query", "Baum" ))
                .andExpect(MockMvcResultMatchers.model().attribute("products", products))
                .andExpect(MockMvcResultMatchers.model().attribute("filter", "borrowed"));
    }


    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void searchForOwnerTest() throws Exception{

        Mockito.when(customerRepository.findById(113L)).thenReturn(java.util.Optional.of(owner));
        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));

        mvc.perform(get("/customer/{customerId}", 113L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("customer"))
                .andExpect(MockMvcResultMatchers.model().attribute("owner", owner))
                .andExpect(MockMvcResultMatchers.model().attribute("user", customer))
                .andExpect(MockMvcResultMatchers.model().attribute("admin", false));

    }

    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void getProductTest() throws Exception{

        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));

        mvc.perform(get("/product"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("user", customer))
                .andExpect(MockMvcResultMatchers.model().attribute("admin", false))
                .andExpect(MockMvcResultMatchers.view().name("addProduct"));

    }


    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void getProductDetailsTest() throws Exception {
        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));
        Mockito.when(customerRepository.findById(111L)).thenReturn(java.util.Optional.of(customer));
        Mockito.when(productRepository.findById(34L)).thenReturn(java.util.Optional.of(product1));

        mvc.perform(get("/product/{id}", 34L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("productDetails"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", customer))
                .andExpect(MockMvcResultMatchers.model().attribute("product", product1))
                .andExpect(MockMvcResultMatchers.model().attribute("owner", owner))
                .andExpect(MockMvcResultMatchers.model().attribute("admin", false));
    }


    //TODO passende Methode dazu noch nicht fertig
    @Ignore
    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void createProductTest() throws Exception{
        Mockito.when(customerRepository.findById(111L)).thenReturn(java.util.Optional.of(customer));
    }

    
    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void editProductTest() throws Exception{

        Mockito.when(productRepository.findById(34L)).thenReturn(java.util.Optional.of(product1));
        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));

        mvc.perform(get("/product/edit/34"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("product", product1))
                .andExpect(MockMvcResultMatchers.view().name("editProduct"));


    }


    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void searchForProductsTest(){

    }

    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void getAvailabilityTest(){

    }

    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void checkAvailabilityTest(){

    }




}
