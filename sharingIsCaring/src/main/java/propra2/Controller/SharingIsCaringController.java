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
import propra2.database.Transaction;
import propra2.handler.OrderProcessHandler;
import propra2.handler.SearchProductHandler;
import propra2.handler.UserHandler;
import propra2.model.*;
import propra2.repositories.CustomerRepository;
import propra2.repositories.OrderProcessRepository;
import propra2.repositories.ProductRepository;
import propra2.repositories.TransactionRepository;

import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
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
    private SearchProductHandler searchProductHandler;

    @Autowired
    private CustomerValidator customerValidator;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    TransactionRepository transactionRepository;

    public SharingIsCaringController() {
        orderProcessHandler = new OrderProcessHandler();
        userHandler = new UserHandler();
        searchProductHandler = new SearchProductHandler();
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

    /**
     * check if user exists
     * @param username
     * @return
     */
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

    /**
     * return base template of product poverview
     * @param model
     * @param user
     * @return
     */
    @GetMapping("/products")
    public String showProducts(Model model, Principal user) {
        Customer customer = customerRepository.findByUsername(user.getName()).get();
        model.addAttribute("user", customer);

        return searchProducts("","all",model,user);
    }

    @GetMapping("/owner/{customerId}")
    public String searchForOwner(@PathVariable Long customerId, Model model, Principal user) {
            Customer owner = customerRepository.findById(customerId).get();
            model.addAttribute("owner", owner);
            Customer customer = customerRepository.findByUsername(user.getName()).get();
            model.addAttribute("user", customer);
            return "customer";
    }

  /**
   * return template for product overview with a list of specific products
   * @param query
   * @param model
   * @param user
   * @return
   */
	@GetMapping("/searchProducts")
	public String searchProducts(@RequestParam final String query, String filter, final Model model, Principal user){
		Customer customer = customerRepository.findByUsername(user.getName()).get();
		List<Product> products = this.searchProductHandler.getSearchedProducts(query, filter, customer, this.productRepository);
		model.addAttribute("user", customer);
		model.addAttribute("query",query);
		model.addAttribute("products", products);
		model.addAttribute("filter",filter);
		return "productsSearch";
	}
    


    /**
     * get template to create a new product
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/product")
    public String getProduct(Principal user, Model model) {
        Customer customer = customerRepository.findByUsername(user.getName()).get();
        model.addAttribute("user", customer);

        return "addProduct";
    }

    @PostMapping("/product")
    public String createProduct(Principal user, final Product product, final Address address, final Model model) {
		Long loggedInId = getUserId(user);
		Optional<Customer> customer = customerRepository.findById(loggedInId);
		model.addAttribute("user", user);

		if(customer.isPresent()){
			product.setOwner(customer.get());
		}

        //product.setOwnerId(loggedInId);
        product.setAvailable(true);
        product.setAddress(address);
        //TODO set borrowed until

        if (product.allValuesSet()) {
            productRepository.save(product);
        }
        return "redirect:/home";
    }

    /**
     * return a list of products with a specific title
     * @param name
     * @return
     */
    @PostMapping("/product/{name}")
    public List<Product> searchForProducts(String name) {
        List<Product> resultList = productRepository.findByTitle(name);
        return resultList;
    }

    @GetMapping("/product/{id}")
    public String getProductDetails(@PathVariable Long id, final Principal user, final Model model) {
        Long loggedInId = getUserId(user);
        Customer customer = customerRepository.findById(loggedInId).get();
        model.addAttribute("user", customer);

        Product product = productRepository.findById(id).get();
        Customer owner = product.getOwner();
        model.addAttribute("product", product);
        model.addAttribute("owner", owner);
        return "productDetails";
    }


    @GetMapping("/product/availability/{id}")
    public String getAvailability(Principal user, Model model, @PathVariable Long id){
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepository.findById(userId);
        Product product = productRepository.findById(id).get();
        model.addAttribute("product", product);
        model.addAttribute("user", customer.get());
        model.addAttribute("available", true);
        return "productAvailability";

    }

    @PostMapping("/product/availability/{id}")
    public String checkAvailability(Principal user, Model model, @PathVariable Long id, String from, String to){
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepository.findById(userId);
        Product product = productRepository.findById(id).get();

        boolean available = orderProcessHandler.checkAvailability(orderProcessRepository, product, from, to);

        model.addAttribute("product", product);
        model.addAttribute("user", customer.get());
        model.addAttribute("available", available);
        return "productAvailability";

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
        Customer customer = customerRepository.findById(loggedInId).get();
        ProPayAccount newProPayAcc = userHandler.getProPayAccount(customer.getUsername());
        customer.setProPay(newProPayAcc);
        customerRepository.save(customer);
        model.addAttribute("user", customer);
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
        model.addAttribute("user", customer.get());
        return "redirect:/profile";
    }


    /*********************************************************************************
        ProPayAccount
     **********************************************************************************/

    /**
     * get template to recharge Credit
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/rechargeCredit")
    public String getRechargeCredit(Principal user, Model model){
        Customer customer = customerRepository.findByUsername(user.getName()).get();
        model.addAttribute("user", customer);
        return "rechargeCredit";
    }

    /**
     * send amount to ProPayAccount and save Transaction in db
     * @param user
     * @param amount
     * @param iban
     * @param model
     * @return
     */
    @PostMapping("/rechargeCredit")
    public String rechargeCredit(Principal user, int amount, String iban,Model model){
        if(amount==0 || iban == null){
            return "redirect:/rechargeCredit";
        }
        Customer customer = customerRepository.findByUsername(user.getName()).get();
        Customer customer1 = userHandler.rechargeCredit(customer, amount);
        userHandler.saveTransaction(amount, TransactionType.PREPAYMENTINPUT, customer.getUsername(), transactionRepository);
        customerRepository.save(customer1);
        model.addAttribute("user", customer);
        return "redirect:/profile";
    }

    /**
     * get overview of transactions for a specific user
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/transactions")
    public String getTransactions(Principal user, Model model){
        List<Transaction> transactions = transactionRepository.findAllByUserName(user.getName());
        Customer customer = customerRepository.findByUsername(user.getName()).get();
        model.addAttribute("user", customer);
        model.addAttribute("transactions", transactions);
        return "transactions";
    }


    /*********************************************************************************
        ORDERS
     **********************************************************************************/

    @GetMapping("/product/{id}/orderProcess")
    public String startOrderProcess(@PathVariable Long id, final Principal user, Model model, boolean notEnoughMoney, boolean incorrectDates, boolean ownProduct, boolean availability){
        Customer customer = customerRepository.findByUsername(user.getName()).get();
        Product product = productRepository.findById(id).get();
        model.addAttribute("product", product);
        model.addAttribute("user", customer);
        model.addAttribute("notEnoughMoney", notEnoughMoney);
        model.addAttribute("incorrectDates", incorrectDates);
        model.addAttribute("ownProduct", ownProduct);
        model.addAttribute("availability", availability);
        return "orderProcess";
    }

    @PostMapping("/product/{id}/orderProcess")
    public String postOrderProcess(@PathVariable Long id, String message, String from, String to, final Principal user, Model model) throws ParseException {
        Customer customer = customerRepository.findByUsername(user.getName()).get();
        Product product = productRepository.findById(id).get();
        double totalAmount = product.getTotalAmount(java.sql.Date.valueOf(from), java.sql.Date.valueOf(to));

        if (!customer.hasEnoughMoney(totalAmount)) {
            return startOrderProcess(id, user, model, true, false, false, false);
        }

        if (!orderProcessHandler.correctDates(java.sql.Date.valueOf(from), java.sql.Date.valueOf(to))) {
            return startOrderProcess(id, user, model, false, true, false, false);
        }

        if(!orderProcessHandler.checkAvailability(orderProcessRepository, product, from, to)){
            return startOrderProcess(id, user, model, false, false, false, true);
        }

        if(product.getOwner().getCustomerId().equals(customer.getCustomerId())){
            return startOrderProcess(id, user, model, false, false, true, false);
        }

        OrderProcess orderProcess = new OrderProcess();
        orderProcess.setOwnerId(product.getOwner().getCustomerId());

        orderProcess.setRequestId(customer.getCustomerId());

        orderProcess.setProduct(product);
        ArrayList<String> messages = new ArrayList<>();
        messages.add(message);
        orderProcess.setMessages(messages);

        orderProcess.setFromDate(java.sql.Date.valueOf(from));
        orderProcess.setToDate(java.sql.Date.valueOf(to));

        orderProcess.setStatus(OrderProcessStatus.PENDING);

        orderProcessRepository.save(orderProcess);

        return "redirect:/home";
    }

    @GetMapping("/offers/{customerId}")
    public List<Product> getOffers(@PathVariable Long customerId) {
    	Optional<Customer> customer = customerRepository.findById(customerId);
        List<Product> products = productRepository.findByOwner(customer.get());
        return products;
    }

    @GetMapping("/orders/{customerId}")
    public List<Product> getOrders(@PathVariable Long customerId) {
        Customer customer = customerRepository.findById(customerId).get();
        List<Product> products = customer.getBorrowedProducts();
        return products;
    }

    @PostMapping("/orderProcess")
    public String addOrderProcess(OrderProcess newOrderProcess) {
        if (newOrderProcess.allValuesSet()) {
            orderProcessRepository.save(newOrderProcess);
        }
        return "";
    }


    /*********************************************************************************
        REQUESTS
     **********************************************************************************/

    @GetMapping("/requests")
    public String showRequests(Principal user, final Model model) {
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepository.findById(userId);

        List<OrderProcess> ownerOrderProcesses = orderProcessRepository.findAllByOwnerId(userId);
        List<OrderProcess> borrower = orderProcessRepository.findAllByRequestId(userId);
        model.addAttribute("user", customer.get());
        model.addAttribute("ownerOrderProcesses", ownerOrderProcesses);
        model.addAttribute("borrower", borrower);
        return "requests";
    }

    @GetMapping("/requests/detailsBorrower/{processId}")
    public String showRequestBorrowerDetails(@PathVariable Long processId, Principal user, final Model model) {
        Long userId = getUserId(user);
        Customer customer = customerRepository.findById(userId).get();

        Optional<OrderProcess> process = orderProcessRepository.findById(processId);
        Product product = process.get().getProduct();
        Long ownerId = process.get().getOwnerId();
        Customer owner = customerRepository.findById(ownerId).get();

        model.addAttribute("owner", owner);
        model.addAttribute("product", product);
        model.addAttribute("process", process.get());
        model.addAttribute("user", customer);
        return "requestDetailsBorrower";
    }

    @RequestMapping(value="/requests/detailsBorrower/{processId}", method=RequestMethod.POST, params="action=delete")
    public String deleteByBorrower(@PathVariable Long processId) {
        OrderProcess orderProcess = orderProcessRepository.findById(processId).get();
        orderProcessRepository.delete(orderProcess);

        return "redirect:/requests";
    }

    @RequestMapping(value="/requests/detailsBorrower/{processId}", method=RequestMethod.POST, params="action=return")
    public String returnProduct(@PathVariable Long processId, Principal user) {
        Customer customer = customerRepository.findByUsername(user.getName()).get();
        OrderProcess orderProcess = orderProcessRepository.findById(processId).get();
        orderProcess.setStatus(OrderProcessStatus.RETURNED);
        orderProcessRepository.save(orderProcess);

        Product product = productRepository.findById(orderProcess.getProduct().getId()).get();
        product.setAvailable(true);
        productRepository.save(product);

        orderProcessHandler.payDailyFee(orderProcess, customerRepository);

        return "redirect:/requests";
    }


    @GetMapping("/requests/detailsOwner/{processId}")
    public String showRequestOwnerDetails(@PathVariable Long processId, Principal user, final Model model) {
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepository.findById(userId);
        Optional<OrderProcess> process = orderProcessRepository.findById(processId);

        model.addAttribute("user", customer);
        model.addAttribute("product", process.get().getProduct());
        model.addAttribute("process", process.get());
        model.addAttribute("borrower", customerRepository.findById(process.get().getRequestId()).get());
        return "requestDetailsOwner";
    }

    @RequestMapping(value="/requests/detailsOwner/{processId}", method=RequestMethod.POST, params="action=acceptProcess")
    public String accept(String message, @PathVariable Long processId) {
        OrderProcess orderProcess = orderProcessRepository.findById(processId).get();
        orderProcess.setStatus(OrderProcessStatus.ACCEPTED);
        ArrayList<String> oldMessages = orderProcess.getMessages();
        ArrayList<String> messages = new ArrayList<>();
        messages.add(message);
        orderProcess.setMessages(messages);

        Product product = productRepository.findById(orderProcess.getProduct().getId()).get();
        product.setAvailable(false);
        product.setBorrowedUntil(orderProcess.getToDate());

        productRepository.save(product);


        orderProcessHandler.updateOrderProcess(oldMessages, orderProcess, orderProcessRepository, customerRepository);

        return "redirect:/requests";
    }

    @RequestMapping(value="/requests/detailsOwner/{processId}", method=RequestMethod.POST, params="action=acceptReturn")
    public String finishProcess(@PathVariable Long processId) {
        OrderProcess orderProcess = orderProcessRepository.findById(processId).get();
        orderProcess.setStatus(OrderProcessStatus.FINISHED);
        orderProcessRepository.save(orderProcess);

        return "redirect:/requests";
    }

    @RequestMapping(value="/requests/detailsOwner/{processId}", method=RequestMethod.POST, params="action=deleteProcess")
    public String deleteByOwner(@PathVariable Long processId) {
        OrderProcess orderProcess = orderProcessRepository.findById(processId).get();
        orderProcessRepository.delete(orderProcess);

        return "redirect:/requests";
    }

    @RequestMapping(value="/requests/detailsOwner/{processId}", method=RequestMethod.POST, params="action=deny")
    public String deny(String message, @PathVariable Long processId) {
        OrderProcess orderProcess = orderProcessRepository.findById(processId).get();
        orderProcess.setStatus(OrderProcessStatus.DENIED);
        ArrayList<String> oldMessages = orderProcess.getMessages();
        ArrayList<String> messages = new ArrayList<>();
        messages.add(message);
        orderProcess.setMessages(messages);

        orderProcessHandler.updateOrderProcess(oldMessages, orderProcess, orderProcessRepository, customerRepository);

        return "redirect:/requests";
    }

}
