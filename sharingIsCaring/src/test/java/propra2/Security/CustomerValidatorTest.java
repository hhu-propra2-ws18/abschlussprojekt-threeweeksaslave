package propra2.Security;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import propra2.model.Customer;
import propra2.repositories.CustomerRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerValidatorTest {

    @Autowired
    CustomerRepository customerRepo;

    @Test
    public void validate() {
        Customer customer = new Customer();
        customer.setMail("mail@gmail.com");
        customer.setUsername("exampleuser123");

        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.customerRepo = this.customerRepo;
        Errors bindingErrors = new BeanPropertyBindingResult(customer, "customer");

        customerValidator.validate(customer, bindingErrors);

        Assertions.assertThat(bindingErrors.hasErrors()).isFalse();
    }

    @Test
    public void tooShortMail() {
        Customer customer = new Customer();
        customer.setMail("b");
        customer.setUsername("exampleuser123");

        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.customerRepo = this.customerRepo;
        Errors bindingErrors = new BeanPropertyBindingResult(customer, "customer");

        customerValidator.validate(customer, bindingErrors);

        Assertions.assertThat(bindingErrors.hasErrors()).isTrue();
    }

    @Test
    public void tooShortUsername() throws Exception {
        Customer customer = new Customer();
        customer.setMail("mail@gmail.com");
        customer.setUsername("a");

        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.customerRepo = this.customerRepo;
        Errors bindingErrors = new BeanPropertyBindingResult(customer, "customer");

        customerValidator.validate(customer, bindingErrors);

        Assertions.assertThat(bindingErrors.hasErrors()).isTrue();
    }

    @Test
    public void duplicateCustomer() {
        Customer customer = new Customer();
        customer.setMail("mail@gmail.com");
        customer.setUsername("exampleuser123");

        customerRepo.save(customer);

        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.customerRepo = this.customerRepo;
        Errors bindingErrors = new BeanPropertyBindingResult(customer, "customer");

        customerValidator.validate(customer, bindingErrors);

        Assertions.assertThat(bindingErrors.hasErrors()).isTrue();
    }

}