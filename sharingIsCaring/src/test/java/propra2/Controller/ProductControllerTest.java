package propra2.Controller;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import propra2.Security.service.CustomerService;
import propra2.Security.service.RegistrationService;
import propra2.Security.validator.CustomerValidator;
import propra2.database.Customer;
import propra2.database.Product;
import propra2.handler.SearchProductHandler;
import propra2.model.Address;
import propra2.model.ProPayAccount;
import propra2.repositories.*;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration
public class ProductControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    RequestController requestController;

    @MockBean
    ProfileController profileController;

    @MockBean
    ProductController productController;

    @MockBean
    ProPayController proPayController;

    @MockBean
    ConflictController conflictController;

    @MockBean
    OrderProcessController orderProcessController;

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
        address.setPostCode(34509);
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


    @Ignore //Leo fragen
    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void showProductsTest() throws Exception{

        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));

        mvc.perform(get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("customer"));



    }

    @Ignore //Leo fragen
    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void searchProductsTest() throws Exception{

        Product product3 = new Product();
        product3.setTitle("Gartenhaus");
        product3.setId(78L);
        product3.setAvailable(false);


        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));
        Mockito.when(searchProductHandler.getSearchedProducts("Baum", "borrowed", customer, productRepository)).thenReturn(Arrays.asList(product1, product2));

        mvc.perform(get("/searchProducts"))
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
                                hasProperty("title", is("Baumlaube"))
                        )
                )));
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
                .andExpect(MockMvcResultMatchers.view().name("addProduct"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", customer))
                .andExpect(MockMvcResultMatchers.model().attribute("admin", false));

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

    @Ignore
    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void searchForProductTest() throws Exception {

        Mockito.when(productRepository.findByTitle("Baum")).thenReturn(Arrays.asList(product1,product2));


    }

    @Ignore
    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void getProductInformationByIdTest() throws Exception{
        Mockito.when(productRepository.findById(34L)).thenReturn(java.util.Optional.of(product1));


        mvc.perform(post("/product/{id}", 34L)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/home"));

    }



}
