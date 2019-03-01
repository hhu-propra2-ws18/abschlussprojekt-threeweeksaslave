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
import propra2.handler.UserHandler;
import propra2.model.Address;
import propra2.model.ProPayAccount;
import propra2.repositories.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static propra2.model.OrderProcessStatus.ACCEPTED;
import static propra2.model.OrderProcessStatus.CONFLICT;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers =ConflictController.class)
//@ContextConfiguration
public class ConflictControllerTest {

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

    @MockBean
    UserHandler userHandler;


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


        product1.setTitle("Baumstamm");
        product1.setId(34L);
        product1.setAvailable(false);
        product1.setOwner(owner);

        product2.setTitle("Baumlaube");
        product2.setId(56L);
        product2.setAvailable(false);
    }

    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void getConflictTest() throws Exception{
        OrderProcess orderProcess = new OrderProcess();
        orderProcess.setId(5L);
        orderProcess.setStatus(CONFLICT);
        orderProcess.setProduct(product2);

        OrderProcess orderProcess2 = new OrderProcess();
        orderProcess2.setId(6L);
        orderProcess2.setStatus(CONFLICT);
        orderProcess2.setProduct(product1);

        OrderProcess orderProcess3 = new OrderProcess();
        orderProcess3.setId(7L);
        orderProcess3.setStatus(ACCEPTED);

        List<OrderProcess> processList = new ArrayList<>();
        processList.add(orderProcess);
        processList.add(orderProcess2);

        Mockito.when(customerRepository.findById(111L)).thenReturn(java.util.Optional.of(customer));
        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));
        Mockito.when(orderProcessRepository.findByStatus(CONFLICT)).thenReturn(processList);

        mvc.perform(get("/conflicts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("processes", processList))
                .andExpect(MockMvcResultMatchers.model().attribute("admin", false))
                .andExpect(MockMvcResultMatchers.view().name("conflict"));
    }

    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void testShowConflictDetails() throws Exception{
        OrderProcess orderProcess = new OrderProcess();
        orderProcess.setId(5L);
        orderProcess.setStatus(CONFLICT);
        orderProcess.setProduct(product2);
        orderProcess.setOwnerId(111L);
        orderProcess.setRequestId(111L);

        Mockito.when(customerRepository.findById(111L)).thenReturn(java.util.Optional.of(customer));
        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));
        Mockito.when(orderProcessRepository.findById(5L)).thenReturn(java.util.Optional.of(orderProcess));

        mvc.perform(get("/conflicts/details/5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("user", customer))
                .andExpect(MockMvcResultMatchers.model().attribute("product", orderProcess.getProduct()))
                .andExpect(MockMvcResultMatchers.model().attribute("process", orderProcess))
                .andExpect(MockMvcResultMatchers.model().attribute("admin", false))
                .andExpect(MockMvcResultMatchers.view().name("conflictDetails"));
    }

}
