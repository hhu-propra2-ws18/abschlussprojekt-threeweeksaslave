package propra2.Security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import propra2.database.Customer;
import propra2.handler.UserHandler;
import propra2.model.UserRegistration;
import propra2.repositories.CustomerRepository;

@Service
public class RegistrationService {
    @Autowired
    private CustomerRepository customerRepo;
    private UserHandler userHandler;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    public RegistrationService() {
        userHandler = new UserHandler();
    }

    public Customer saveCredentials(UserRegistration user) {
        Customer customer = new Customer();
        customer.setRole("ROLE_USER");
        customer.setUsername(user.getUserName());
        customer.setMail(user.getEmailAddress());
        customer.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        customer.setProPay(userHandler.getProPayAccount(user.getUserName()));

        customerRepo.save(customer);

        return customer;
    }
}
