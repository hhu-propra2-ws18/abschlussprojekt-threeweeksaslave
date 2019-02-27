package propra2.Controller;

import org.junit.Before;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import propra2.Security.service.CustomerService;
import propra2.Security.service.RegistrationService;
import propra2.Security.validator.CustomerValidator;
import propra2.database.Customer;
import propra2.database.Transaction;
import propra2.handler.SearchProductHandler;
import propra2.handler.UserHandler;
import propra2.repositories.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ProPayController.class)
public class ProPayControllerTest {

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
    SoldProductRepository soldProductRepository;

    @MockBean
    CustomerService customerService;

    @MockBean
    UserHandler userHandler;

    Customer customer = new Customer();
    Customer admin = new Customer();

    @Before
    public void setup(){

        admin.setUsername("admin");
        admin.setMail("admin@admin.de");
        admin.setPassword("adminPass");

        customer.setCustomerId(111L);
        customer.setUsername("Kevin");
        customer.setMail("kevin@istdumm.de");
        customer.setPassword("Baumhaus");
        customer.setRole("USER_ROLE");
    }


    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void getRechargeCreditTest() throws Exception{
        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));

        mvc.perform(get("/rechargeCredit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("user", customer))
                .andExpect(MockMvcResultMatchers.view().name("rechargeCredit"));
    }

    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void rechargeCreditTest()throws Exception{
        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));

        mvc.perform(post("/rechargeCredit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("amount", "10")
                .param("iban", "iban"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/profile"));

    }

    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void rechargeCreditTestAmountFailed()throws Exception{
        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));


        mvc.perform(post("/rechargeCredit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("amount", "0")
                .param("iban", "iban"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/rechargeCredit"));

    }


    @Test
    @WithMockUser(username="Kevin", password = "Baumhaus")
    public void getTransactionsTest() throws Exception{
        List<Transaction> transactionList = new ArrayList<>();
        Transaction transaction = new Transaction();
        transactionList.add(transaction);
        Mockito.when(transactionRepository.findAllByUserName("Kevin")).thenReturn(transactionList);
        Mockito.when(customerRepository.findByUsername("Kevin")).thenReturn(java.util.Optional.of(customer));

        mvc.perform(get("/transactions"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("user", customer))
                .andExpect(MockMvcResultMatchers.model().attribute("transactions", transactionList))
                .andExpect(MockMvcResultMatchers.view().name("transactions"));
    }
}
