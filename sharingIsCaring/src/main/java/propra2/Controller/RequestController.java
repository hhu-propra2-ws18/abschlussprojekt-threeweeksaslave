package propra2.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import propra2.database.*;
import propra2.handler.OrderProcessHandler;
import propra2.handler.UserHandler;
import propra2.model.OrderProcessStatus;
import propra2.model.ProPayAccount;
import propra2.model.TransactionType;
import propra2.repositories.*;
import propra2.repositories.ProductRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class RequestController {

    @Autowired
    CustomerRepository customerRepo;

    @Autowired
    OrderProcessRepository orderProcessRepo;

    @Autowired
    ProductRepository productRepo;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    private OrderProcessHandler orderProcessHandler;

    @Autowired
    private UserHandler userHandler;


    /**
     * get an overview of all lend an borrowed products and their orderProcesses
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/requests")
    public String showRequests(Principal user, final Model model) {
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepo.findById(userId);

        List<OrderProcess> ownerOrderProcesses = orderProcessRepo.findAllByOwnerId(userId);
        List<OrderProcess> orderProcesses = orderProcessRepo.findAllByRequestId(userId);
        List<OrderProcess> lenderOrderProcesses = new ArrayList<>();
        List<OrderProcess> sellerOrderProcesses = new ArrayList<>();
        List<OrderProcess> buyerOrderProcesses = new ArrayList<>();
        List<OrderProcess> borrowerOrderProcesses = new ArrayList<>();

        for (OrderProcess orderProcess : orderProcesses) {
            if (orderProcess.getProduct().isForSale()) {
                buyerOrderProcesses.add(orderProcess);
            }
            else {
                borrowerOrderProcesses.add(orderProcess);
            }
        }

        for (OrderProcess orderProcess : ownerOrderProcesses) {
            if (orderProcess.getProduct().isForSale()) {
                sellerOrderProcesses.add(orderProcess);
            }
            else {
                lenderOrderProcesses.add(orderProcess);
            }
        }

        model.addAttribute("user", customer.get());
        model.addAttribute("borrower", borrowerOrderProcesses);
        model.addAttribute("buyer", buyerOrderProcesses);
        model.addAttribute("lender", lenderOrderProcesses);
        model.addAttribute("seller", sellerOrderProcesses);

        if(sellerOrderProcesses.isEmpty()){
            model.addAttribute("soldProductsExist", false);
        }else{
            model.addAttribute("soldProductsExist", true);
        }

        if(borrowerOrderProcesses.isEmpty()) {
            model.addAttribute("borrowerExist", false);
        }else{
            model.addAttribute("borrowerExist", true);
        }

        if (buyerOrderProcesses.isEmpty()){
            model.addAttribute("boughtProductsExist", false);
        } else {
            model.addAttribute("boughtProductsExist", true);
        }

        if (lenderOrderProcesses.isEmpty()){
            model.addAttribute("lentProductsExist", false);
        } else {
            model.addAttribute("lentProductsExist", true);
        }

        boolean admin = false;
        if(customer.get().getRole().equals("ADMIN")){
            admin = true;
        }
        model.addAttribute("admin", admin);

        return "requests";
    }

    private Long getUserId(Principal user) {
        String username = user.getName();
        Long id = customerRepo.findByUsername(username).get().getCustomerId();
        return id;
    }

    /**
     * cancel an orderProcess
     * @param processId
     * @param user
     * @return
     */
    @RequestMapping(value="/requests/detailsBorrower/{processId}", method=RequestMethod.POST, params="action=cancel")
    public String cancelOrder(@PathVariable Long processId, Principal user){
        OrderProcess orderProcess = orderProcessRepo.findById(processId).get();
        orderProcess.setStatus(OrderProcessStatus.CANCELED);
        orderProcessHandler.updateOrderProcess(new ArrayList<>(), orderProcess);

        return "requests";
    }

    /**
     * show details of an orderProcess of a borrowed product
     * @param processId
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/requests/detailsBorrower/{processId}")
    public String showRequestBorrowerDetails(@PathVariable Long processId, Principal user, final Model model) {
        Long userId = getUserId(user);
        Customer customer = customerRepo.findById(userId).get();

        Optional<OrderProcess> process = orderProcessRepo.findById(processId);
        Product product = process.get().getProduct();

        Long ownerId = process.get().getOwnerId();
        Customer owner = customerRepo.findById(ownerId).get();

        model.addAttribute("cancelable", process.get().isCancelable());

        model.addAttribute("owner", owner);
        model.addAttribute("product", product);
        model.addAttribute("process", process.get());
        model.addAttribute("user", customer);
        boolean admin = false;
        if(customer.getRole().equals("ADMIN")){
            admin = true;
        }
        model.addAttribute("admin", admin);
        return "requestDetailsBorrower";
    }

    /**
     * delete an orderProcess
     * @param processId
     * @return
     */
    @RequestMapping(value="/requests/detailsBorrower/{processId}", method= RequestMethod.POST, params="action=delete")
    public String deleteByBorrower(@PathVariable Long processId) {
        OrderProcess orderProcess = orderProcessRepo.findById(processId).get();
        orderProcessRepo.delete(orderProcess);

        return "redirect:/requests";
    }

    /**
     * return a product, dailyFee is payed
     * @param processId
     * @param user
     * @param model
     * @return
     */
    @RequestMapping(value="/requests/detailsBorrower/{processId}", method=RequestMethod.POST, params="action=return")
    public String returnProduct(@PathVariable Long processId, Principal user, Model model) {
        Customer customer = customerRepo.findByUsername(user.getName()).get();
        OrderProcess orderProcess = orderProcessRepo.findById(processId).get();

        boolean successful = orderProcessHandler.payDailyFee(orderProcess);

        if(successful) {
            double dailyFee = orderProcess.getProduct().getTotalDailyFee(orderProcess.getFromDate());
            String rentingAccount = customerRepo.findById(orderProcess.getRequestId()).get().getUsername();
            String ownerAccount = customerRepo.findById(orderProcess.getOwnerId()).get().getUsername();

            if(dailyFee>0){
                userHandler.saveTransaction(dailyFee, TransactionType.DAILYFEEPAYMENT, rentingAccount);
                userHandler.saveTransaction(dailyFee, TransactionType.RECEIVEDDAILYFEE, ownerAccount);
            }

            orderProcess.setStatus(OrderProcessStatus.RETURNED);
            orderProcess.setToDate(new java.sql.Date(System.currentTimeMillis()));
            orderProcessRepo.save(orderProcess);

            Optional<Notification> notification = notificationRepository.findByProcessId(processId);
            if (notification.isPresent()) {
                notificationRepository.delete(notification.get());
            }
            return "redirect:/requests";
        }else{
            model.addAttribute("note", "Sorry, connection to your ProPayAccount failed please try it again later!");
            return showRequests(user, model);
        }
    }

    /**
     * show details of an orderProcess of a lend product
     * @param processId
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/requests/detailsLender/{processId}")
    public String showRequestOwnerDetails(@PathVariable Long processId, Principal user, final Model model) {
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepo.findById(userId);
        Optional<OrderProcess> process = orderProcessRepo.findById(processId);


        model.addAttribute("user", customer);
        model.addAttribute("product", process.get().getProduct());
        model.addAttribute("process", process.get());
        model.addAttribute("borrower", customerRepo.findById(process.get().getRequestId()).get());
        boolean admin = false;
        if(customer.get().getRole().equals("ADMIN")){
            admin = true;
        }
        model.addAttribute("admin", admin);
        return "requestDetailsLender";
    }

    @GetMapping("/requests/detailsSeller/{processId}")
    public String showRequestSellerDetails(@PathVariable Long processId, Principal user, final Model model) {
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepo.findById(userId);
        Optional<OrderProcess> process = orderProcessRepo.findById(processId);


        model.addAttribute("user", customer);
        model.addAttribute("product", process.get().getProduct());
        model.addAttribute("process", process.get());
        model.addAttribute("buyer", customerRepo.findById(process.get().getRequestId()).get());
        boolean admin = false;
        if(customer.get().getRole().equals("ADMIN")){
            admin = true;
        }
        model.addAttribute("admin", admin);
        return "requestDetailsSeller";
    }

    @GetMapping("/requests/detailsBuyer/{processId}")
    public String showRequestBuyerDetails(@PathVariable Long processId, Principal user, final Model model) {
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepo.findById(userId);
        Optional<OrderProcess> process = orderProcessRepo.findById(processId);


        model.addAttribute("user", customer);
        model.addAttribute("product", process.get().getProduct());
        model.addAttribute("process", process.get());
        model.addAttribute("seller", customerRepo.findById(process.get().getOwnerId()).get());
        boolean admin = false;
        if(customer.get().getRole().equals("ADMIN")){
            admin = true;
        }
        model.addAttribute("admin", admin);
        return "requestDetailsBuyer";
    }

    /**
     * as an owner you can accept the orderProcess, the caution is blocked
     * @param message
     * @param processId
     * @param user
     * @param model
     * @return
     */
    @RequestMapping(value="/requests/detailsLender/{processId}", method=RequestMethod.POST, params="action=acceptProcess")
    public String accept(String message, @PathVariable Long processId, Principal user, Model model) {
        OrderProcess orderProcess = orderProcessRepo.findById(processId).get();
        orderProcess.setStatus(OrderProcessStatus.ACCEPTED);
        ArrayList<Message> oldMessages = orderProcess.getMessages();
        ArrayList<Message> messages = new ArrayList<>();
        Message newMessage = orderProcess.createMessage(user, message);
        messages.add(newMessage);
        orderProcess.setMessages(messages);

        Product product = productRepo.findById(orderProcess.getProduct().getId()).get();
        product.setBorrowedUntil(orderProcess.getToDate());

        boolean finishedSuccessful = orderProcessHandler.updateOrderProcess(oldMessages, orderProcess);
        if(finishedSuccessful){
            productRepo.save(product);
            return "redirect:/requests";
        }else{
            orderProcess.setStatus(OrderProcessStatus.PENDING);
            orderProcess.setMessages(oldMessages);
            orderProcessRepo.save(orderProcess);
            model.addAttribute("note", "Sorry, connection to your ProPayAccount failed. Please try it again later.");
            return showRequests(user, model);
        }
    }

    /**
     * As an owner you can accept the return, the caution will be released
     * @param processId
     * @param model
     * @param user
     * @return
     */
     @RequestMapping(value="/requests/detailsLender/{processId}", method=RequestMethod.POST, params="action=acceptReturn")
     public String finishProcess(@PathVariable Long processId, Model model, Principal user) {
        OrderProcess orderProcess = orderProcessRepo.findById(processId).get();
        orderProcess.setStatus(OrderProcessStatus.FINISHED);

        Customer rentingAccount = customerRepo.findById(orderProcess.getRequestId()).get();
        ProPayAccount proPayAccount = rentingAccount.getProPay();

        boolean successful = orderProcessHandler.updateOrderProcess(orderProcess.getMessages(), orderProcess);
        if(successful){
            return "redirect:/requests";
        }else{
            orderProcess.setStatus(OrderProcessStatus.RETURNED);
            rentingAccount.setProPay(proPayAccount);
            customerRepo.save(rentingAccount);
            orderProcessRepo.save(orderProcess);
            model.addAttribute("note", "Sorry, connection to your ProPayAccount failed. Please try it again later.");
            return showRequests(user, model);
        }
    }

    /**
     * as an owner you can deny the return, an admin will get a notification to resolve the conflict
     * @param processId
     * @param message
     * @param user
     * @return
     */
    @RequestMapping(value="/requests/detailsLender/{processId}", method=RequestMethod.POST, params="action=appeal")
    public String appealProcess(@PathVariable Long processId, String message, Principal user) {
        OrderProcess orderProcess = orderProcessRepo.findById(processId).get();
        orderProcess.setStatus(OrderProcessStatus.CONFLICT);
        ArrayList<Message> oldMessages = orderProcess.getMessages();
        ArrayList<Message> messages = new ArrayList<>();
        Message newMessage = orderProcess.createMessage(user, message);
        messages.add(newMessage);
        orderProcess.setMessages(messages);

        orderProcessHandler.updateOrderProcess(oldMessages, orderProcess);

        return "redirect:/requests";
    }

    /**
     * delete orderProcess as an owner
     * @param processId
     * @return
     */
    @RequestMapping(value="/requests/detailsLender/{processId}", method=RequestMethod.POST, params="action=deleteProcess")
    public String deleteByOwner(@PathVariable Long processId) {
        OrderProcess orderProcess = orderProcessRepo.findById(processId).get();
        orderProcessRepo.delete(orderProcess);

        return "redirect:/requests";
    }

    /**
     * deny the orderProcess
     * @param message
     * @param processId
     * @param user
     * @return
     */
    @RequestMapping(value="/requests/detailsLender/{processId}", method=RequestMethod.POST, params="action=deny")
    public String deny(String message, @PathVariable Long processId, Principal user) {
        OrderProcess orderProcess = orderProcessRepo.findById(processId).get();
        orderProcess.setStatus(OrderProcessStatus.DENIED);
        ArrayList<Message> oldMessages = orderProcess.getMessages();
        ArrayList<Message> messages = new ArrayList<>();
        Message newMessage = orderProcess.createMessage(user, message);
        messages.add(newMessage);
        orderProcess.setMessages(messages);

        orderProcessHandler.updateOrderProcess(oldMessages, orderProcess);

        return "redirect:/requests";
    }
}
