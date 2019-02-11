package propra2.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import propra2.database.UserRepository;
import propra2.model.User;

import java.util.Optional;

@Controller
public class SharingIsCaringController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @GetMapping("/profile/{customerId}")
    public User getUserDataById(@PathVariable Long customerId){
        Optional<User> user = userRepository.findById(customerId);
        return user.get();
    }
}
