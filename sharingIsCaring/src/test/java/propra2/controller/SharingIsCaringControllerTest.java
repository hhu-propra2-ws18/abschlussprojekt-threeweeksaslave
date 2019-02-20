package propra2.controller;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
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

import static com.sun.javaws.JnlpxArgs.verify;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static propra2.model.OrderProcessStatus.ACCEPTED;
import static propra2.model.OrderProcessStatus.PENDING;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration
        //@WithMockUser(username="admin",roles={"USER","ADMIN"})
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


    Customer bendisposto = new Customer();
    Customer customer = new Customer();

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



        customer.setCustomerId(111L);
        customer.setUsername("Kevin");
        customer.setMail("kevin@istdumm.de");
        customer.setPassword("Baumhaus");
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
        Product product1 = new Product();
        product1.setTitle("Baumstamm");
        product1.setId(34L);
        product1.setAvailable(false);

        Product product2 = new Product();
        product2.setTitle("Baumlaube");
        product2.setId(56L);
        product2.setAvailable(false);

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

        Customer owner = new Customer();
        owner.setCustomerId(113L);
        owner.setUsername("Lukas");
        owner.setMail("lukas@web.de");



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
                        hasProperty("mail", is("bendisposto@web.de"))

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

        mvc.perform(get("/requests/{id}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("requests"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", allOf(
                        hasProperty("username", is("Zoidberg")),
                        hasProperty("mail", is("bendisposto@web.de")))))
                .andExpect(MockMvcResultMatchers.model().attribute("owner", allOf(
                        hasProperty("username", is("Zoidberg")),
                        hasProperty("mail", is("bendisposto@web.de")))))
                .andExpect(MockMvcResultMatchers.model().attribute("borrower", allOf(
                hasProperty("username", is("Zoidberg")),
                hasProperty("mail", is("bendisposto@web.de")))));
    }
}



