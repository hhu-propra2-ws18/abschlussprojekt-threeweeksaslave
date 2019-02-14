package propra2.Security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import propra2.database.Customer;
import propra2.repositories.CustomerRepository;

import java.util.Optional;

@Service
public class CustomerService implements UserDetailsService {

    @Autowired
    private CustomerRepository users;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> user = users.findByUsername(username);
        if (user.isPresent()) {
            Customer u = user.get();
            UserDetails userdetails = User.builder()
                    .username(u.getUsername())
                    .password(u.getPassword())
                    .authorities(u.getRole())
                    .build();
            return userdetails;
        }
        throw new UsernameNotFoundException("Invalid Username");
    }

}
