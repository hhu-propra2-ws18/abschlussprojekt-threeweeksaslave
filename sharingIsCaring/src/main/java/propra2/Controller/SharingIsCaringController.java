package propra2.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import propra2.database.UserRepository;
import propra2.model.Product;
import propra2.model.User;

@Controller
public class SharingIsCaringController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @PostMapping("/registration")
    String createUser(User newUser){
        userRepository.save(newUser);
        return "";
    }

    @PostMapping("/product")
    String createProduct(Product newProduct){
        User owner = userRepository.findById(newProduct.getOwnerId()).get();
        owner.addProductToLend(newProduct);
        userRepository.save(owner);
        productRepository.save(newProduct);

        return "";
    }
}
