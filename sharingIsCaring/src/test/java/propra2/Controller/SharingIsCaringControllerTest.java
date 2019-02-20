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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import propra2.Security.service.CustomerService;
import propra2.Security.service.RegistrationService;
import propra2.Security.validator.CustomerValidator;
import propra2.database.Customer;
import propra2.database.OrderProcess;
import propra2.database.Product;
import propra2.handler.SearchProductHandler;
import propra2.model.Address;
import propra2.model.ProPayAccount;
import propra2.repositories.CustomerRepository;
import propra2.repositories.OrderProcessRepository;
import propra2.repositories.ProductRepository;
import propra2.repositories.TransactionRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static propra2.model.OrderProcessStatus.ACCEPTED;
import static propra2.model.OrderProcessStatus.PENDING;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration
public class SharingIsCaringControllerTest {

    @Autowired
    MockMvc mvc;

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


    private Customer bendisposto = new Customer();
    private Customer customer = new Customer();
    private Customer owner = new Customer();
    private Customer admin = new Customer();
    private Product product1 = new Product();
    private Product product2 = new Product();

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

        owner.setCustomerId(113L);
        owner.setUsername("Lukas");
        owner.setMail("lukas@web.de");


        product1.setTitle("Baumstamm");
        product1.setId(34L);
        product1.setAvailable(false);
        product1.setOwner(owner);

        product2.setTitle("Baumlaube");
        product2.setId(56L);
        product2.setAvailable(false);
    }

    /*********************************************************************************
     LOGIN AND REGISTRATION, fertig
     **********************************************************************************/

    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void startTest() throws Exception{
        Mockito.when(customerRepository.findByUsername("admin")).thenReturn(java.util.Optional.of(admin));

        mvc.perform(get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("start"));
    }

    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void homeTest() throws Exception{
        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));

        mvc.perform(get("/home"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("user", customer))
                .andExpect(MockMvcResultMatchers.view().name("home"));
    }

    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void registrationTest() throws Exception{
        mvc.perform(get("/registration"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registration"));
    }

    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void createUserTest() throws Exception{
        Customer user = new Customer();
        user.setUsername("Klaus");
        user.setMail("klaus@web.de");
        user.setPassword("kuhlerKlaus");

        Mockito.when(customerRepository.findByUsername("Klaus")).thenReturn(java.util.Optional.of(user));
        Mockito.when(customerRepository.findByMail("klaus@web.de")).thenReturn(java.util.Optional.of(user));

        mvc.perform(MockMvcRequestBuilders.post("/registration")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/home"));
    }




    /*********************************************************************************
     PRODUCTS
     **********************************************************************************/

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

        mvc.perform(get("/owner/{customerId}", 113L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("customer"))
                .andExpect(MockMvcResultMatchers.model().attribute("owner", owner))
                .andExpect(MockMvcResultMatchers.model().attribute("user", customer));

    }

    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void getProductTest() throws Exception{

        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));

        mvc.perform(get("/product"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("addProduct"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", customer));

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
                .andExpect(MockMvcResultMatchers.model().attribute("owner", owner));
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


    /*********************************************************************************
     PROFILES
     **********************************************************************************/

    @Test
    @WithMockUser(username="Zoidberg", password = "propra2")
    public void testShowProfile() throws Exception {


        Mockito.when(customerRepository.findByUsername("Zoidberg")).thenReturn(java.util.Optional.of(bendisposto));
        Mockito.when(customerRepository.findById(2L)).thenReturn(java.util.Optional.of(bendisposto));

        mvc.perform(get("/profile"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("profile"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", allOf(
                        hasProperty("username", is("Zoidberg")),
                        hasProperty("mail", is("bendisposto@web.de")),
                        hasProperty("address", hasProperty("street", is("Unistraße")))

                )));
        //verify(customerRepository, times(1)).findById(2L);
    }

    @Test
    @WithMockUser(username="Zoidberg", password = "propra2")
    public void testShowProfileUpdate() throws Exception {

        Mockito.when(customerRepository.findByUsername("Zoidberg")).thenReturn(java.util.Optional.of(bendisposto));
        Mockito.when(customerRepository.findById(2L)).thenReturn(java.util.Optional.of(bendisposto));

        mvc.perform(get("/profile/update"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("profileUpdate"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", allOf(
                        hasProperty("username", is("Zoidberg")),
                        hasProperty("mail", is("bendisposto@web.de"))
                )));
        //verify(customerRepository, times(1)).findById(2L);
    }


    @Test
    @WithMockUser(username="Zoidberg", password = "propra2")
    public void testUpdateProfile() throws Exception {

        Mockito.when(customerRepository.findById(2L)).thenReturn(java.util.Optional.of(bendisposto));
        Mockito.when(customerRepository.save(bendisposto)).thenReturn(bendisposto);
        Mockito.when(customerRepository.findByUsername("Zoidberg")).thenReturn(java.util.Optional.of(bendisposto));

        mvc.perform(post("/profile/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("action", "save")
                .requestAttr("user", new Customer()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/profile"));
    }

    /*********************************************************************************
     Orders
     **********************************************************************************/

    /*********************************************************************************
     REQUESTS
     **********************************************************************************/
    @Test
    @WithMockUser(username = "Zoidberg", password = "propra2")
    public void testShowRequests() throws Exception {

        OrderProcess process1 = new OrderProcess();
        process1.setId(13L);
        process1.setOwnerId(2L);
        process1.setProduct(product1);
        process1.setRequestId(111L);
        process1.setStatus(PENDING);
        List owner = new ArrayList();
        owner.add(process1);

        OrderProcess process2 = new OrderProcess();
        process2.setId(17L);
        process2.setOwnerId(111L);
        process2.setProduct(product2);
        process2.setRequestId(2L);
        process2.setStatus(ACCEPTED);
        List borrowed = new ArrayList();
        borrowed.add(process2);

        Mockito.when(orderProcessRepository.findAllByOwnerId(2L)).thenReturn(owner);
        Mockito.when(orderProcessRepository.findAllByRequestId(2L)).thenReturn(borrowed);
        Mockito.when(customerRepository.findById(2L)).thenReturn(java.util.Optional.of(bendisposto));

        mvc.perform(get("/requests/{id}", 2L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("requests"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", allOf(
                        hasProperty("username", is("Zoidberg")),
                        hasProperty("mail", is("bendisposto@web.de")))))
                .andExpect(MockMvcResultMatchers.model().attribute("owner", hasItem(
                        allOf(
                                hasProperty("ownerId", is(2L)),
                                hasProperty("requestId", is(111L)),
                                hasProperty("product", hasProperty("title", is("Baumstamm"))),
                                hasProperty("status", is(PENDING))))))
                .andExpect(MockMvcResultMatchers.model().attribute("borrower", hasItem(
                        allOf(
                                hasProperty("ownerId", is(111L)),
                                hasProperty("requestId", is(2L)),
                                hasProperty("product", hasProperty("title", is("Baumlaube"))),
                                hasProperty("status", is(ACCEPTED))))));
    }

    @Test
    //TODO Fick diesen Test...
    @Ignore
    @WithMockUser(username = "Zoidberg", password = "propra2")
    public void testShowRequestDetailsOwner() throws Exception {

        OrderProcess process = new OrderProcess();
        process.setId(13L);
        process.setOwnerId(2L);
        process.setProduct(product1);
        process.setRequestId(111L);
        process.setStatus(PENDING);

        Mockito.when(orderProcessRepository.findById(13L)).thenReturn(java.util.Optional.of(process));
        //Mockito.when(customerRepository.findByUsername("Zoidberg").get().getCustomerId()).thenReturn(2L);
        Mockito.when(customerRepository.findByUsername("Zoidberg")).thenReturn(java.util.Optional.of(bendisposto));

        mvc.perform(get("/requests/detailsOwner/{processId}", 13L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("requestDetailsOwner"))
                .andExpect(MockMvcResultMatchers.model().attribute("borrower", allOf(
        hasProperty("username", is("Kevin")),
                hasProperty("mail", is("kevin@istdumm.de")))))
                .andExpect(MockMvcResultMatchers.model().attribute("process", allOf(
                hasProperty("ownerId", is(2L)),
                hasProperty("requestId", is(111L)),
                hasProperty("product", hasProperty("title", is("Baumstamm"))),
                hasProperty("status", is(PENDING)))))
                .andExpect(MockMvcResultMatchers.model().attribute("product", allOf(
                        hasProperty("avaiable", is(false)),
                        hasProperty("title", is("Baumlaube")),
                        hasProperty("id", is(34L)))));
    }

    @Test
    @Ignore
    // TODO Fick auch diesen Test(gleicher wie der davor)
    @WithMockUser(username = "Zoidberg", password = "propra2")
    public void testShowRequestDetailsBorrower() throws Exception {

        OrderProcess process = new OrderProcess();
        process.setId(13L);
        process.setOwnerId(111L);
        process.setProduct(product1);
        process.setRequestId(2L);
        process.setStatus(PENDING);

        Mockito.when(orderProcessRepository.findById(13L)).thenReturn(java.util.Optional.of(process));
        //Mockito.when(customerRepository.findByUsername("Zoidberg").get().getCustomerId()).thenReturn(2L);
        //Mockito.when(customerRepository.findByUsername("Zoidberg")).thenReturn(java.util.Optional.of(bendisposto));

        mvc.perform(get("/requests/detailsBorrower/{processId}", 13L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("requestDetailsBorrower"))
                .andExpect(MockMvcResultMatchers.model().attribute("owner", allOf(
                        hasProperty("username", is("Kevin")),
                        hasProperty("mail", is("kevin@istdumm.de")))))
                .andExpect(MockMvcResultMatchers.model().attribute("process", allOf(
                        hasProperty("ownerId", is(2L)),
                        hasProperty("requestId", is(111L)),
                        hasProperty("product", hasProperty("title", is("Baumstamm"))),
                        hasProperty("status", is(PENDING)))))
                .andExpect(MockMvcResultMatchers.model().attribute("product", allOf(
                        hasProperty("avaiable", is(false)),
                        hasProperty("title", is("Baumlaube")),
                        hasProperty("id", is(34L)))));


    }
}



