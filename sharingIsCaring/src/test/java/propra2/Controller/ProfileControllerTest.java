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
import propra2.handler.SearchProductHandler;
import propra2.handler.UserHandler;
import propra2.model.Address;
import propra2.model.ProPayAccount;
import propra2.repositories.CustomerRepository;
import propra2.repositories.SoldProductRepository;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ProfileController.class)
@ContextConfiguration
public class ProfileControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    CustomerRepository customerRepository;

    @MockBean
    UserHandler userHandler;

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

    Customer bendisposto = new Customer();
    ProPayAccount account = new ProPayAccount();

    @Before
    public void setup() {
        Address address = new Address();
        address.setStreet("Unistraße");
        address.setPostcode(34509);
        address.setHouseNumber(1);
        address.setCity("Ddorf");

        account.setAccount("Zoidberg");
        account.setAmount(100);

        bendisposto.setUsername("Zoidberg");
        bendisposto.setCustomerId(2L);
        bendisposto.setMail("bendisposto@web.de");
        bendisposto.setAddress(address);
        bendisposto.setPassword("propra2");
        bendisposto.setProPay(account);
        bendisposto.setRole("USER");
    }

    @Test
    @WithMockUser(username="Zoidberg", password = "propra2")
    public void testShowProfile() throws Exception {

        Mockito.when(customerRepository.findByUsername("Zoidberg")).thenReturn(java.util.Optional.of(bendisposto));
        Mockito.when(customerRepository.findById(2L)).thenReturn(java.util.Optional.of(bendisposto));
        Mockito.when(userHandler.getProPayAccount("Zoidberg")).thenReturn(account);

        mvc.perform(get("/profile"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("profile"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", allOf(
                        hasProperty("username", is("Zoidberg")),
                        hasProperty("mail", is("bendisposto@web.de")),
                        hasProperty("address", hasProperty("street", is("Unistraße")))
                )));
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
                        hasProperty("mail", is("bendisposto@web.de")),
                        hasProperty("address", hasProperty("street", is("Unistraße")))
                )));
    }

    @Test
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
}