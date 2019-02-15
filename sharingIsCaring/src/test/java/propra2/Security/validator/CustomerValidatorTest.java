package propra2.Security.validator;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import propra2.database.Customer;
import propra2.model.UserRegistration;
import propra2.repositories.CustomerRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerValidatorTest {

    @Autowired
    CustomerRepository customerRepo;

    @Test
    public void validate() {
        UserRegistration customer = new UserRegistration();
        customer.setEmailAddress("mail@gmail.com");
        customer.setUserName("exampleuser123");
        customer.setPassword("password");
        customer.setPasswordConfirm("password");

        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.customerRepo = this.customerRepo;
        Errors bindingErrors = new BeanPropertyBindingResult(customer, "customer");

        customerValidator.validate(customer, bindingErrors);

        Assertions.assertThat(bindingErrors.hasErrors()).isFalse();
    }

    @Test
    public void tooShortMail() {
        UserRegistration customer = new UserRegistration();
        customer.setEmailAddress("b");
        customer.setUserName("exampleuser123");
        customer.setPassword("password");
        customer.setPasswordConfirm("password");

        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.customerRepo = this.customerRepo;
        Errors bindingErrors = new BeanPropertyBindingResult(customer, "customer");

        customerValidator.validate(customer, bindingErrors);

        Assertions.assertThat(bindingErrors.hasErrors()).isTrue();
    }

    @Test
    public void tooShortUsername() throws Exception {
        UserRegistration customer = new UserRegistration();
        customer.setEmailAddress("mail@gmail.com");
        customer.setUserName("a");
        customer.setPassword("password");
        customer.setPasswordConfirm("password");

        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.customerRepo = this.customerRepo;
        Errors bindingErrors = new BeanPropertyBindingResult(customer, "customer");

        customerValidator.validate(customer, bindingErrors);

        Assertions.assertThat(bindingErrors.hasErrors()).isTrue();
    }

    @Test
    public void duplicateCustomer() {
        UserRegistration customer = new UserRegistration();
        customer.setEmailAddress("mail@gmail.com");
        customer.setUserName("exampleuser123");
        customer.setPassword("password");
        customer.setPasswordConfirm("password");

        Customer oldCustomer = new Customer();
        oldCustomer.setMail("mail@gmail.com");
        oldCustomer.setUsername("exampleuser123");
        oldCustomer.setPassword("password");

        customerRepo.save(oldCustomer);

        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.customerRepo = this.customerRepo;
        Errors bindingErrors = new BeanPropertyBindingResult(customer, "customer");

        customerValidator.validate(customer, bindingErrors);

        Assertions.assertThat(bindingErrors.hasErrors()).isTrue();
    }

}