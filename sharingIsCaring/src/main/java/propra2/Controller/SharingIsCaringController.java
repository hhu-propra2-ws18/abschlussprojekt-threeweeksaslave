package propra2.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import propra2.Security.service.RegistrationService;
import propra2.Security.validator.CustomerValidator;
import propra2.database.Customer;
import propra2.database.OrderProcess;
import propra2.database.Product;
import propra2.handler.OrderProcessHandler;
import propra2.handler.UserHandler;
import propra2.model.Address;
import propra2.model.UserRegistration;
import propra2.repositories.CustomerRepository;
import propra2.repositories.OrderProcessRepository;
import propra2.repositories.ProductRepository;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
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
    private UserHandler userHandler;

    @Autowired
    private CustomerValidator customerValidator;

    @Autowired
    private RegistrationService registrationService;

    public SharingIsCaringController() {
        orderProcessHandler = new OrderProcessHandler();
        userHandler = new UserHandler();
    }


    /*********************************************************************************
        LOGIN AND REGISTRATION
     **********************************************************************************/
    @GetMapping("/")
    public String start() {
        if (!customerRepository.findByUsername("admin").isPresent()) {
            UserRegistration admin = new UserRegistration();
            admin.setUserName("admin");
            admin.setEmailAddress("admin@admin.de");
            admin.setPassword("adminPass");
            admin.setPasswordConfirm("adminPass");
            registrationService.saveCredentials(admin);
        }

        return "start";
    }

    @PostMapping("/")
    public boolean userExists(final String username) {
        if (customerRepository.findByUsername(username).isPresent())
            return true;
        throw new IllegalArgumentException();
    }

    /**
     * homepage from a specific customer
     *
     * @param user
     * @param model
     * @return home template
     */
    @GetMapping("/home")
    public String home(Principal user, Model model) {
        Customer customer = customerRepository.findByUsername(user.getName()).get();
        model.addAttribute("user", customer);
        return "home";
    }

    /**
     * registration
     *
     * @return registration template
     */
    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    /**
     * check if registration data is valid, create new ProPay Account, registrate and save Customer in DB
     *
     * @param user
     * @param bindingResult
     * @param model
     * @return if request failed redirect to registration, otherwise direct to login
     */
    @PostMapping("/registration")
    public String createUser(UserRegistration user, BindingResult bindingResult, Model model) {
        customerValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }
        registrationService.saveCredentials(user);

        return "redirect:/home";
    }


    /*********************************************************************************
        PRODUCTS
     **********************************************************************************/
    @GetMapping("/products")
    public String showProducts(Model model, Principal user) {
        Customer customer = customerRepository.findByUsername(user.getName()).get();
        model.addAttribute("user", customer);

        return "productsBase";
    }

    @GetMapping("/searchProducts")
    public String searchProducts(@RequestParam final String query, final Model model, Principal user) {
        Customer customer = customerRepository.findByUsername(user.getName()).get();
        model.addAttribute("user", customer);

        model.addAttribute("products", this.productRepository
                .findAllByTitleContainingOrDescriptionContaining(query, query));
        model.addAttribute("query", query);


        return "productsSearch";
    }

    @GetMapping("/product")
    public String getProduct(Principal user, Model model) {
        Customer customer = customerRepository.findByUsername(user.getName()).get();
        model.addAttribute("user", customer);

        return "addProduct";
    }

    @PostMapping("/product")
    public String createProduct(final Product newProduct) {
        if (newProduct.allValuesSet()) {
            productRepository.save(newProduct);
        }
        return "redirect:/home";
    }

    @PostMapping("/product/{name}")
    List<Product> searchForProducts(String name) {
        List<Product> resultList = productRepository.findByTitle(name);
        return resultList;
    }

    @PostMapping("/product/{id}")
    Product getProductInformationById(Long id) {
        return productRepository.findById(id).get();
    }


    /*********************************************************************************
        PROFILE
     **********************************************************************************/

    /**
     * show profile data
     *
     * @param model
     * @return profile template
     */
    @GetMapping("/profile")
    public String getUserDataById(Principal user, Model model) {
        Long loggedInId = getUserId(user);
        Optional<Customer> customer = customerRepository.findById(loggedInId);
        model.addAttribute("user", customer.get());
        return "profile";
    }

    private Long getUserId(Principal user) {
        String username = user.getName();
        Long id = customerRepository.findByUsername(username).get().getCustomerId();
        return id;
    }

    /**
     * direct to profileUpdate
     *
     * @param model
     * @return profileUpdate template
     */
    @GetMapping("/profile/update")
    public String getUpdateUserData(Principal user, Model model) {
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepository.findById(userId);
        model.addAttribute("user", customer.get());
        return "profileUpdate";
    }

    /**
     * update profile data changes
     *
     * @param address
     * @param model
     * @return profile template
     */
    @PostMapping("/profile/update")
    public String updateUserData(Principal user, Address address, Model model, String mail) {
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepository.findById(userId);
        customer.get().setAddress(address);
        customer.get().setMail(mail);
        customerRepository.save(customer.get());
        model.addAttribute("user", customer);
        return "redirect:/profile";
    }


    /*********************************************************************************
        ORDERS
     **********************************************************************************/
    @GetMapping("/offers/{customerId}")
    public List<Product> getOffers(@PathVariable Long customerId) {
        List<Product> products = productRepository.findByOwnerId(customerId);
        return products;
    }

    @GetMapping("/orders/{customerId}")
    public List<Product> getOrders(@PathVariable Long customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        List<Product> products = productRepository.findAllById(customer.get().getBorrowedProductIds());
        return products;
    }

    @PostMapping("/orderProcess")
    public String addOrderProcess(OrderProcess newOrderProcess) {
        if (newOrderProcess.allValuesSet()) {
            orderProcessRepository.save(newOrderProcess);
        }
        return "";
    }
  
    @PostMapping("/orderProcess/{id}")
    public void updateOrderProcess(@PathVariable Long id, @RequestBody OrderProcess orderProcess) throws IOException {
        orderProcessHandler.updateOrderProcess(orderProcess, orderProcessRepository);
    }


    /*********************************************************************************
        REQUESTS
     **********************************************************************************/
    @GetMapping("/requests/{id}")
    public String showRequests(final Model model, @PathVariable final Long id) {
        List<OrderProcess> owner = orderProcessRepository.findAllByOwnerId(id);
        List<OrderProcess> borrower = orderProcessRepository.findAllByRequestId(id);
        Optional<Customer> user = customerRepository.findById(id);
        model.addAttribute("user", user.get());
        model.addAttribute("owner", owner);
        model.addAttribute("borrower", borrower);
        return "requests";
    }

    @GetMapping("/requests/detailsBorrower/{processId}")
    public String showRequestBorrowerDetails(@PathVariable Long processId, Model model) {
        Optional<OrderProcess> process = orderProcessRepository.findById(processId);
        Product product = process.get().getProduct();
        model.addAttribute("process", process.get());
        model.addAttribute("owner", customerRepository.findById(process.get().getOwnerId()));
        model.addAttribute("product", product);
        return "requestDetailsBorrower";
    }
      
    @GetMapping("/requests/detailsOwner/{processId}")
    public String showRequestOwnerDetails(@PathVariable Long processId, Model model) {
        Optional<OrderProcess> process = orderProcessRepository.findById(processId);
        model.addAttribute("process", process.get());
        model.addAttribute("borrower", customerRepository.findById(process.get().getRequestId()));
        return "requestDetailsOwner";
    }
  
}
