package propra2.Security.service;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.database.Customer;
import propra2.repositories.CustomerRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerServiceTest {

    @Autowired
    private CustomerRepository customerRepo;

    @Test
    public void testLoadUserByUsername() throws Exception{
        Customer customer = new Customer();
        customer.setCustomerId(111L);
        customer.setUsername("Kevin");
        customer.setMail("kevin@istdumm.de");
        customer.setPassword("Baumhaus");
        customer.setRole("USER");
        customerRepo.save(customer);
        CustomerService customerService = new CustomerService();
        customerService.users = this.customerRepo;
        UserDetails userDetails = customerService.loadUserByUsername("Kevin");

        Assertions.assertThat(userDetails.getPassword().equals("Baumhaus"));
        Assertions.assertThat(userDetails.getUsername().equals("Kevin"));



    }
}
