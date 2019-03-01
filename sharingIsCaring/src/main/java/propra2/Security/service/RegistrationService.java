package propra2.Security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import propra2.database.Customer;
import propra2.handler.UserHandler;
import propra2.model.Address;
import propra2.model.ProPayAccount;
import propra2.model.UserRegistration;
import propra2.repositories.CustomerRepository;

import java.util.ArrayList;

@Service
public class RegistrationService {

    @Autowired
    CustomerRepository customerRepo;

    @Autowired
    UserHandler userHandler;

    @Autowired
    PasswordEncoder bCryptPasswordEncoder;


    public void saveCredentials(UserRegistration user) {
        Customer customer = new Customer();
        customer.setRole(user.getRole());
        customer.setUsername(user.getUserName());
        customer.setMail(user.getEmailAddress());
        customer.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        if (userHandler.getProPayAccount(user.getUserName()) != null) {
            customer.setProPay(userHandler.getProPayAccount(user.getUserName()));
        } else {
            ProPayAccount proPayAccount = new ProPayAccount();
            proPayAccount.setReservations(new ArrayList<>());
            proPayAccount.setAccount(user.getUserName());
            proPayAccount.setAmount(0);
            customer.setProPay(proPayAccount);
        }
        Address address = new Address();
        address.setStreet("-");
        address.setHouseNumber(0);
        address.setPostcode(0);
        address.setCity("-");
        customer.setAddress(address);


        customerRepo.save(customer);
    }
}
