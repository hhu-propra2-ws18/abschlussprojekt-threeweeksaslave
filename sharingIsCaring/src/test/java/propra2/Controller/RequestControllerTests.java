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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static propra2.model.OrderProcessStatus.ACCEPTED;
import static propra2.model.OrderProcessStatus.PENDING;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration
public class RequestControllerTests {

    @Autowired
    MockMvc mvc;
    @MockBean
    SearchProductHandler searchProductHandler;
    @MockBean
    CustomerValidator customerValidator;
    @MockBean
    RegistrationService registrationService;
    @MockBean
    CustomerService customerService;
    @MockBean
    AuthenticationController authenticationController;
    @MockBean
    ConflictController conflictController;
    @MockBean
    OrderProcessController orderProcessController;
    @MockBean
    ProPayController proPayController;
    @MockBean
    ProductController productController;
    @MockBean
    ProfileController profileController;
    @MockBean
    RequestController requestController;
    @MockBean
    OrderProcessRepository orderProcessRepository;
    @MockBean
    CustomerRepository customerRepository;

    Customer bendisposto = new Customer();
    Customer kevin = new Customer();
    Product product1 = new Product();
    Product product2 = new Product();


    @Before
    public void setup() {
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


        kevin.setCustomerId(111L);
        kevin.setUsername("Kevin");
        kevin.setMail("kevin@istdumm.de");
        kevin.setPassword("Baumhaus");

        product1.setTitle("Baumstamm");
        product1.setId(34L);
        product1.setAvailable(false);
        product1.setOwner(kevin);

        product2.setTitle("Baumlaube");
        product2.setId(56L);
        product2.setAvailable(false);
    }

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

    @Ignore
    @Test
    // TODO Fick auch diesen Test(gleicher wie der davor)
    @WithMockUser(username = "Zoidberg", password = "propra2")
    public void testShowRequestDetailsBorrower() throws Exception {

        OrderProcess process = new OrderProcess();
        process.setId(13L);
        process.setOwnerId(111L);
        process.setProduct(product1);
        process.setRequestId(2L);
        process.setStatus(PENDING);

        Customer owner2 = new Customer();
        owner2.setCustomerId(111L);
        owner2.setUsername("Luke");
        owner2.setMail("luke@web.de");

        Mockito.when(orderProcessRepository.findById(13L)).thenReturn(java.util.Optional.of(process));
        Mockito.when(customerRepository.findById(2L)).thenReturn(java.util.Optional.of(bendisposto));
        Mockito.when(customerRepository.findById(111L)).thenReturn(java.util.Optional.of(owner2));
        Mockito.when(customerRepository.findByUsername("Zoidberg")).thenReturn(java.util.Optional.of(bendisposto));

        mvc.perform(get("/requests/detailsBorrower/{processId}", 13L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("requestDetailsBorrower"))
                .andExpect(MockMvcResultMatchers.model().attribute("process", process))
                .andExpect(MockMvcResultMatchers.model().attribute("owner", owner2))
                .andExpect(MockMvcResultMatchers.model().attribute("product", product1));


    }
}