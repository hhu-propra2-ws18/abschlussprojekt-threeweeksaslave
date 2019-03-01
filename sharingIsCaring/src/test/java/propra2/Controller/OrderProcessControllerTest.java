package propra2.Controller;


import org.junit.Before;
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
import propra2.database.OrderProcess;
import propra2.database.Product;
import propra2.handler.OrderProcessHandler;
import propra2.handler.SearchProductHandler;
import propra2.model.Address;
import propra2.model.ProPayAccount;
import propra2.repositories.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static propra2.model.OrderProcessStatus.ACCEPTED;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = OrderProcessController.class)
//@ContextConfiguration
public class OrderProcessControllerTest {

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
    OrderProcessHandler orderProcessHandler;

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
        bendisposto.setRole("User");



        /* auskommentiert, weil duplicated
        admin.setUsername("admin");
        admin.setMail("admin@admin.de");
        admin.setPassword("adminPass");
        */

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

    @Test
    @WithMockUser(username="Zoidberg", password = "propra2")
    public void testStartOrderProcess() throws Exception{
        OrderProcess orderProcess = new OrderProcess();
        orderProcess.setStatus(ACCEPTED);
        orderProcess.setRequestId(111L);
        orderProcess.setOwnerId(2L);
        orderProcess.setProduct(product1);

        Mockito.when(customerRepository.findByUsername("Zoidberg")).thenReturn(java.util.Optional.of(bendisposto));
        Mockito.when(productRepository.findById(34L)).thenReturn(java.util.Optional.of(product1));

        mvc.perform(get("/product/{id}/orderProcess", 34L)
                .requestAttr("hasEnoughMoney", true)
                .requestAttr("incorrectDates", false)
                .requestAttr("ownProduct", false)
                .requestAttr("availability", false))

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("orderProcess"))
                .andExpect(MockMvcResultMatchers.model().attribute("product", allOf(
                        hasProperty("title", is("Baumstamm")))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", allOf(
                        hasProperty("username", is("Zoidberg")))))
                .andExpect(MockMvcResultMatchers.model().attribute("notEnoughMoney", false))
                .andExpect(MockMvcResultMatchers.model().attribute("incorrectDates", false))
                .andExpect(MockMvcResultMatchers.model().attribute("ownProduct", false))
                .andExpect(MockMvcResultMatchers.model().attribute("availability", false));
    }
}
