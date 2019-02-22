package propra2.Security.service;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.database.Customer;
import propra2.model.ProPayAccount;
import propra2.model.UserRegistration;
import propra2.repositories.CustomerRepository;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RegistrationServiceTest {

    @Autowired
    private CustomerRepository customerRepo;

    PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Test
    public void testSaveCredentials(){
        UserRegistration testUserReg = new UserRegistration();
        testUserReg.setUserName("peter");
        testUserReg.setPassword("password");
        testUserReg.setPasswordConfirm("password");
        testUserReg.setEmailAddress("example@example");
        testUserReg.setRole("ROLE_USER");

        RegistrationService regService = new RegistrationService();
        regService.bCryptPasswordEncoder = this.bCryptPasswordEncoder;
        regService.customerRepo = this.customerRepo;

        regService.saveCredentials(testUserReg);

        Customer testCustomer = customerRepo.findByUsername("peter").get();
        Assertions.assertThat(testCustomer.getUsername()).isEqualTo("peter");
        Assertions.assertThat(testCustomer.getRole()).isEqualTo("ROLE_USER");
        Assertions.assertThat(testCustomer.getMail()).isEqualTo("example@example");
        Assertions.assertThat(bCryptPasswordEncoder.matches("password", testCustomer.getPassword())).isTrue();
        Assertions.assertThat(testCustomer.getAddress().getCity()).isEqualTo("-");
        Assertions.assertThat(testCustomer.getAddress().getStreet()).isEqualTo("-");
        Assertions.assertThat(testCustomer.getAddress().getHouseNumber()).isEqualTo(0);
        Assertions.assertThat(testCustomer.getAddress().getPostCode()).isEqualTo(0);
        Assertions.assertThat(testCustomer.getProPay().getAccount()).isEqualTo("peter");
        Assertions.assertThat(testCustomer.getProPay().getAmount()).isEqualTo(0);
        Assertions.assertThat(testCustomer.getProPay().getReservations().size()).isEqualTo(0);


    }
}