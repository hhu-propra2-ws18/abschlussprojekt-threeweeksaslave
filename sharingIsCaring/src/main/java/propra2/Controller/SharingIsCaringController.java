package propra2.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import propra2.handler.OrderProcessHandler;
import propra2.model.Customer;
import propra2.model.OrderProcess;
import propra2.repositories.CustomerRepository;
import propra2.model.Product;
import propra2.repositories.OrderProcessRepository;
import propra2.repositories.ProductRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Controller
public class SharingIsCaringController {

    @Autowired
    ProductRepository productRepository;
    
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    OrderProcessRepository orderProcessRepository;

    private OrderProcessHandler orderProcessHandler;

    public SharingIsCaringController() {
        orderProcessHandler = new OrderProcessHandler();
    }


    @PostMapping("/registration")
    String createUser(Customer newCustomer){
        customerRepository.save(newCustomer);
        return "";
    }

    @PostMapping("/product")
    String createProduct(Product newProduct) {
        if(newProduct.allValuesSet()){
            productRepository.save(newProduct);
        }

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

    @PostMapping("/orderProcess/{id}")
    public void updateOrderProcess(@PathVariable Long id, @RequestBody OrderProcess orderProcess){
        orderProcessHandler.updateOrderProcess(orderProcess, orderProcessRepository);
    }
}
