package propra2.Security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import propra2.database.Customer;
import propra2.handler.UserHandler;
import propra2.model.Address;
import propra2.model.UserRegistration;
import propra2.repositories.CustomerRepository;

@Service
public class RegistrationService {

    @Autowired
    CustomerRepository customerRepo;

    @Autowired
    private UserHandler userHandler;

    @Autowired
    PasswordEncoder bCryptPasswordEncoder;


    public void saveCredentials(UserRegistration user) {
        Customer customer = new Customer();
        customer.setRole(user.getRole());
        customer.setUsername(user.getUserName());
        customer.setMail(user.getEmailAddress());
        customer.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        customer.setProPay(userHandler.getProPayAccount(user.getUserName()));
        Address address = new Address();
        address.setStreet("-");
        address.setHouseNumber(0);
        address.setPostCode(0);
        address.setCity("-");
        customer.setAddress(address);


        customerRepo.save(customer);
    }
}
