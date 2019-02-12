package propra2.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import propra2.Security.CustomerValidator;
import propra2.model.Customer;
import propra2.repositories.CustomerRepository;
import propra2.model.Product;
import propra2.repositories.ProductRepository;
import java.util.List;

import java.util.Optional;

@Controller
public class SharingIsCaringController {

    @Autowired
    ProductRepository productRepository;
    
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    private CustomerValidator customerValidator;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("customerForm", new Customer());
        return "registration";
    }

    @PostMapping("/registration")
    public String createUser(@ModelAttribute("customerForm") Customer newCustomer, BindingResult bindingResult, Model model) {
        customerValidator.validate(newCustomer, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        customerRepository.save(newCustomer);
        return "login";
    }

    @PostMapping("/product")
    String createProduct(Product newProduct) {
        Customer owner = customerRepository.findById(newProduct.getOwner().getCustomerId()).get();
        owner.addProductToLend(newProduct);
        customerRepository.save(owner);
        productRepository.save(newProduct);

        return "";
    }

    @PostMapping("/product/{name}")
    List<Product> searchForProducts(String name){
        List<Product> resultList = productRepository.findByTitle(name);
        return resultList;
    }

    @PostMapping("/product/{id}")
    Product getProductInformationById(Long id){
        return productRepository.findById(id).get();
    }


    @PostMapping("/") 
    public boolean userExists(final String username){
        if(customerRepository.findByUsername(username).isPresent())
            return true;
    throw new IllegalArgumentException();
    }

    @GetMapping("/profile/{customerId}")
    public Customer getUserDataById(@PathVariable Long customerId){
        Optional<Customer> user = customerRepository.findById(customerId);
        return user.get();
    } 
  
    @PostMapping("/profile/{customerId}")
    public void updateUserData(@PathVariable Long customerId, @RequestBody Customer customer){
        customerRepository.save(customer);
    }
}
