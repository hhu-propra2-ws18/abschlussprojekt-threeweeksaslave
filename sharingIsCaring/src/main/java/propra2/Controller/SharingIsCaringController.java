package propra2.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import propra2.database.UserRepository;

@Controller
public class SharingIsCaringController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;
}
