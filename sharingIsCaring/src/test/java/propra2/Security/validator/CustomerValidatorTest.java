package propra2.Security.validator;

import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import propra2.database.Customer;
import propra2.model.Address;
import propra2.model.UserRegistration;
import propra2.repositories.CustomerRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerValidatorTest {

    @Autowired
    CustomerRepository customerRepo;

    @Test
    public void validate() {
        UserRegistration user = new UserRegistration();
        user.setUserName("exampleuser123");
        user.setEmailAddress("mail@gmail.com");
        user.setPassword("password");
        user.setPasswordConfirm("password");

        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.customerRepo = this.customerRepo;
        Errors bindingErrors = new BeanPropertyBindingResult(user, "customer");

        customerValidator.validate(user, bindingErrors);

        Assertions.assertThat(bindingErrors.hasErrors()).isFalse();
    }

    @Test
    public void tooShortMail() {
        UserRegistration user = new UserRegistration();
        user.setEmailAddress("mail");
        user.setUserName("exampleuser123");
        user.setPassword("password");
        user.setPasswordConfirm("password");

        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.customerRepo = this.customerRepo;
        Errors bindingErrors = new BeanPropertyBindingResult(user, "customer");

        customerValidator.validate(user, bindingErrors);

        Assertions.assertThat(bindingErrors.hasErrors()).isTrue();
    }

    @Test
    public void tooShortUsername() {
        UserRegistration user = new UserRegistration();
        user.setEmailAddress("example@mail.com");
        user.setUserName("My");
        user.setPassword("password");
        user.setPasswordConfirm("password");

        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.customerRepo = this.customerRepo;
        Errors bindingErrors = new BeanPropertyBindingResult(user, "customer");

        customerValidator.validate(user, bindingErrors);

        Assertions.assertThat(bindingErrors.hasErrors()).isTrue();
    }

    @Ignore
    @Test
    public void duplicateCustomer() {
        UserRegistration user = new UserRegistration();
        user.setEmailAddress("mail@gmail.com");
        user.setUserName("exampleuser123");
        user.setPassword("password");
        user.setPasswordConfirm("password");

        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.customerRepo = this.customerRepo;
        Errors bindingErrors = new BeanPropertyBindingResult(user, "customer");

        customerValidator.validate(user, bindingErrors);

        Assertions.assertThat(bindingErrors.hasErrors()).isTrue();
    }

}