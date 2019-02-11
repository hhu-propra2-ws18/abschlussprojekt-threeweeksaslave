package propra2.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import propra2.database.ProductRepository;
import propra2.database.UserRepository;
import propra2.model.Product;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import propra2.database.UserRepository;
import propra2.model.User;

import java.util.Optional;

@Controller
public class SharingIsCaringController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/product/{name}")
    List<Product> searchForProducts(String name){
        List<Product> resultList = productRepository.findByTitle(name);
        return resultList;
    }

    @PostMapping("product/{id}")
    Product getProductInformationById(Long id){
        return productRepository.findById(id).get();
    }


    @PostMapping("/") 
    public boolean userExists(final String username){
        if(userRepository.findByUsername(username).isPresent())
            return true;
    throw new IllegalArgumentException();
    }

    @GetMapping("/profile/{customerId}")
    public User getUserDataById(@PathVariable Long customerId){
        Optional<User> user = userRepository.findById(customerId);
        return user.get();
    } 
  
    @PostMapping("/profile/{customerId}")
    public void updateUserData(@PathVariable Long customerId, @RequestBody User user){
        userRepository.save(user);
    }
}
