package propra2.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import propra2.database.Customer;
import propra2.database.OrderProcess;
import propra2.database.Product;
import propra2.handler.OrderProcessHandler;
import propra2.handler.SearchProductHandler;
import propra2.handler.UserHandler;
import propra2.model.Address;
import propra2.model.OrderProcessStatus;
import propra2.model.TransactionType;
import propra2.repositories.CustomerRepository;
import propra2.repositories.OrderProcessRepository;
import propra2.repositories.ProductRepository;
import propra2.repositories.SoldProductRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private OrderProcessHandler orderProcessHandler;

    @Autowired
    private OrderProcessRepository orderProcessRepository;

    @Autowired
    private SearchProductHandler searchProductHandler;

    @Autowired
    private SoldProductRepository soldProductRepo;

    @Autowired
    UserHandler userHandler;

    /**
     * return base template of product poverview
     *
     * @param model
     * @param user
     * @return
     */
    @GetMapping("/products")
    public String showProducts(Model model, Principal user) {
		addUserAndAdmin(user, model);

        return searchProducts("", "all", model, user);
    }

    /**
     * return template for product overview with a list of specific products
     *
     * @param query
     * @param model
     * @param user
     * @return
     */
    @GetMapping("/searchProducts")
    public String searchProducts(@RequestParam final String query, String filter, final Model model, Principal user) {
        Customer customer = customerRepo.findByUsername(user.getName()).get();
        List<Product> products = searchProductHandler.getSearchedProducts(query, filter, customer, productRepo);
        model.addAttribute("user", customer);
        model.addAttribute("query", query);
        model.addAttribute("products", products);
        model.addAttribute("filter", filter);
        boolean admin = false;
        if (customer.getRole().equals("ADMIN")) {
            admin = true;
        }
        model.addAttribute("admin", admin);
        return "productsSearch";
    }

    @GetMapping("/customer/{customerId}")
    public String searchForOwner(@PathVariable Long customerId, Model model, Principal user) {
        Customer owner = customerRepo.findById(customerId).get();
        model.addAttribute("owner", owner);
		addUserAndAdmin(user, model);
        return "customer";
    }

    /**
     * get template to create a new product
     *
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/lend")
    public String getLendProduct(Principal user, final Model model) {
        addUserAndAdmin(user, model);

        Product product = new Product();
        model.addAttribute("product", product);
        return "lend";
    }

    @GetMapping("/sale")
    public String getSaleProduct(Principal user, final Model model) {
        addUserAndAdmin(user, model);

        Product product = new Product();
        model.addAttribute("product",product);

        return "sale";
    }

    @PostMapping("/sale")
    public String createSaleProduct(Principal user, final Product product, final Address address, final Model model) {
        Long loggedInId = getUserId(user);
        Optional<Customer> customer = customerRepo.findById(loggedInId);
        model.addAttribute("user", user);

        if (customer.isPresent()) {
            product.setOwner(customer.get());
        }

        product.setDeposit(0);
        product.setDailyFee(0);
        product.setForSale(true);
        product.setAvailable(true);
        product.setAddress(address);

        if (product.allValuesSetSale()) {
            productRepo.save(product);
        }
        return "addImageToProduct";
    }

    @PostMapping("/lend")
    public String createLendProduct(Principal user, final Product product, final Address address, final Model model) {
        Long loggedInId = getUserId(user);
        Optional<Customer> customer = customerRepo.findById(loggedInId);
        model.addAttribute("user", user);

        if (customer.isPresent()) {
            product.setOwner(customer.get());
        }

        product.setAvailable(true);
        product.setForSale(false);
        product.setAddress(address);

        if (product.allValuesSetRent()) {
            productRepo.save(product);
        }
        return "addImageToProduct";
    }

	@GetMapping("/product/edit/{id}")
	public String editProduct(Principal user, Model model, @PathVariable Long id) {
		addUserAndAdmin(user, model);
		Optional<Product> productOptional = productRepo.findById(id);
		if(productOptional.isPresent()){
		    Product product = productOptional.get();
		    if(product.getOwner().getCustomerId().equals(getUserId(user)) && product.isEditingAllowed(orderProcessRepository)){
				model.addAttribute("product",product);
				return "editProduct";
			}
		}
		return "redirect:/home";
	}

	@PostMapping("/product/edit/{productId}")
	public String saveProduct(Principal user, Product product, final Address address, @PathVariable Long productId) {
		Product oldProduct = productRepo.findById(productId).get();
		Customer owner = oldProduct.getOwner();
		if(owner.getCustomerId().equals(getUserId(user))){
			product.setOwner(owner);
			product.setAvailable(oldProduct.isAvailable());
			product.setForSale(oldProduct.isForSale());
			product.setAddress(address);
			product.setId(productId);
			productRepo.save(product);
			return "editProductImage";
		}
		return "redirect:/home";
	}

    private Long getUserId(Principal user) {
        String username = user.getName();
        Optional<Customer> customer = customerRepo.findByUsername(username);
        Long id = customer.get().getCustomerId();
        return id;
    }

    private void addUserAndAdmin(Principal user, Model model){
		Customer customer = customerRepo.findByUsername(user.getName()).get();
		model.addAttribute("user", customer);
		boolean admin = false;
		if (customer.getRole().equals("ADMIN")) {
			admin = true;
		}
		model.addAttribute("admin", admin);
	}

    /**
     * return a list of products with a specific title
     *
     * @param name
     * @return
     */
    @PostMapping("/product/{name}")
    public List<Product> searchForProducts(String name) {
        List<Product> resultList = productRepo.findByTitle(name);
        return resultList;
    }

    @GetMapping("/productDetails/{id}")
    public String getProductDetails(@PathVariable Long id, final Principal user, final Model model) {
        Long loggedInId = getUserId(user);
        Customer customer = customerRepo.findById(loggedInId).get();
        model.addAttribute("user", customer);

        Product product = productRepo.findById(id).get();
        Customer owner = product.getOwner();
        model.addAttribute("product", product);
        model.addAttribute("owner", owner);
        boolean admin = false;
        if (customer.getRole().equals("ADMIN")) {
            admin = true;
        }
        model.addAttribute("admin", admin);
        model.addAttribute("editable", product.isEditingAllowed(orderProcessRepository));
        return "productDetails";
    }

    @PostMapping("/productDetails/{id}")
    public String buyProduct(@PathVariable Long id, Principal user, Model model) {
        Customer customer = customerRepo.findByUsername(user.getName()).get();
        Product product = productRepo.findById(id).get();
        List<OrderProcess> orderProcessesOfRequester = orderProcessRepository.findAllByRequestId(customer.getCustomerId());

        if (!customer.hasEnoughMoney(product.getSellingPrice(), orderProcessesOfRequester)) {
            model.addAttribute("note", "Please recharge your ProPayAccount to buy this Product!");
            return getProductDetails(id, user, model);
        }

        if (product.getOwner().getCustomerId().equals(customer.getCustomerId())) {
            model.addAttribute("note", "You already own this Product!");
            return getProductDetails(id, user, model);
        }
        OrderProcess orderProcess = new OrderProcess();
        orderProcess.setOwnerId(product.getOwner().getCustomerId());

        orderProcess.setRequestId(customer.getCustomerId());

        Product soldProduct = new Product();
        soldProduct.setTitle(product.getTitle());
        soldProduct.setOwner(product.getOwner());
        soldProduct.setAddress(product.getAddress());
        soldProduct.setForSale(product.isForSale());
        soldProduct.setAvailable(product.isAvailable());
        soldProduct.setDescription(product.getDescription());
        soldProduct.setSellingPrice(product.getSellingPrice());

        orderProcess.setProduct(product);

        orderProcess.setStatus(OrderProcessStatus.SOLD);

        boolean finishedSuccessful = orderProcessHandler.updateOrderProcess(null ,orderProcess);
        if(finishedSuccessful){
            int sellingPrice = orderProcess.getProduct().getSellingPrice();
            if(sellingPrice>0){
                String rentingAccount = customerRepo.findById(orderProcess.getRequestId()).get().getUsername();
                String ownerAccount = customerRepo.findById(orderProcess.getOwnerId()).get().getUsername();
                userHandler.saveTransaction(sellingPrice, TransactionType.BUYPAYMENT, rentingAccount);
                userHandler.saveTransaction(sellingPrice, TransactionType.RECEIVEDBUYPAYMENT, ownerAccount);
            }
            orderProcess.setProduct(soldProduct);
            soldProductRepo.save(soldProduct);
            productRepo.delete(product);
            return "redirect:/home";
        }
        else{
            model.addAttribute("note", "Sorry, connection to your ProPayAccount failed. Please try it again later.");
            return getProductDetails(id, user, model);
        }
    }

    @GetMapping("/product/availability/{id}")
    public String getAvailability(Principal user, Model model, @PathVariable Long id) {
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepo.findById(userId);
        Product product = productRepo.findById(id).get();
        model.addAttribute("product", product);
        model.addAttribute("user", customer.get());
        model.addAttribute("available", true);
        boolean admin = false;
        if (customer.get().getRole().equals("ADMIN")) {
            admin = true;
        }
        model.addAttribute("admin", admin);
        return "productAvailability";
    }

    @PostMapping("/product/availability/{id}")
    public String checkAvailability(Principal user, Model model, @PathVariable Long id, String from, String to) {
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepo.findById(userId);
        Product product = productRepo.findById(id).get();

        boolean available = orderProcessHandler.checkAvailability(orderProcessRepository, product, from, to);

        model.addAttribute("product", product);
        model.addAttribute("user", customer.get());
        model.addAttribute("available", available);
        return "productAvailability";
    }
}
