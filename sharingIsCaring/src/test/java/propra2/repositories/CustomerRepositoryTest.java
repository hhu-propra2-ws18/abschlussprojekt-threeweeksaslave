package propra2.repositories;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.database.Customer;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    public void userRepositoryTestFindById() {
        Customer customer = new Customer();
        customer.setCustomerId(2L);
        customer.setUsername("userName2");

        customerRepository.save(customer);

        Optional<Customer> userOptional = customerRepository.findById(2L);

        Assertions.assertThat(userOptional.get().getUsername()).isEqualTo("userName2");

        customerRepository.delete(userOptional.get());
    }

    @Test
    public void userRepositoryTestFindByUserName() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setUsername("userName");
        customer.setMail("email@gmx.de");

        customerRepository.save(customer);

        Optional<Customer> userOptional = customerRepository.findByUsername("userName");

        Assertions.assertThat(userOptional.get().getMail()).isEqualTo("email@gmx.de");

        customerRepository.delete(userOptional.get());
    }
}
