package propra2.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import propra2.database.ProductRepository;
import propra2.database.UserRepository;
import propra2.model.Product;

import java.util.List;

@Controller
public class SharingIsCaringController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping
    List<Product> searchForProducts(String productName){
        List<Product> resultList = productRepository.findByTitle(productName);
        return resultList;
    }

    @PostMapping
    Product getProductInformationById(Long id){
        return productRepository.findById(id).get();
    }
}
