package propra2.Controller;


import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import propra2.Security.service.CustomerService;
import propra2.Security.service.RegistrationService;
import propra2.Security.validator.CustomerValidator;
import propra2.database.Customer;
import propra2.database.Product;
import propra2.handler.SearchProductHandler;
import propra2.model.Address;
import propra2.model.ProPayAccount;
import propra2.repositories.*;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration
public class OrderProcessControllerTest {

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


}
