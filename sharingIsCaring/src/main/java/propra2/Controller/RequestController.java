package propra2.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import propra2.database.*;
import propra2.handler.OrderProcessHandler;
import propra2.handler.UserHandler;
import propra2.model.OrderProcessStatus;
import propra2.model.TransactionType;
import propra2.repositories.*;

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

    @GetMapping("/requests")
    public String showRequests(Principal user, final Model model) {
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepo.findById(userId);

        List<OrderProcess> ownerOrderProcesses = orderProcessRepo.findAllByOwnerId(userId);
        List<OrderProcess> borrowerOrderProcesses = orderProcessRepo.findAllByRequestId(userId);
        model.addAttribute("user", customer.get());
        model.addAttribute("ownerOrderProcesses", ownerOrderProcesses);
        model.addAttribute("borrower", borrowerOrderProcesses);
        if(ownerOrderProcesses.size()==0){
            model.addAttribute("lendProductsExist", false);
        }else{
            model.addAttribute("lendProductsExist", true);
        }

        if(borrowerOrderProcesses.size()==0){
            model.addAttribute("borrowerExist", false);
        }else{
            model.addAttribute("borrowerExist", true);
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

    @RequestMapping(value="/requests/detailsBorrower/{processId}", method=RequestMethod.POST, params="action=cancel")
    public String cancelOrder(@PathVariable Long processId, Principal user){
        OrderProcess orderProcess = orderProcessRepo.findById(processId).get();
        orderProcess.setStatus(OrderProcessStatus.CANCELED);
        orderProcessHandler.updateOrderProcess(new ArrayList<>(), orderProcess);

        return "requests";
    }

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

    @RequestMapping(value="/requests/detailsBorrower/{processId}", method= RequestMethod.POST, params="action=delete")
    public String deleteByBorrower(@PathVariable Long processId) {
        OrderProcess orderProcess = orderProcessRepo.findById(processId).get();
        orderProcessRepo.delete(orderProcess);

        return "redirect:/requests";
    }

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
        }else{
            model.addAttribute("note", "Sorry, your request failed please try it again later!");
            showRequests(user, model);
        }

        return "redirect:/requests";
    }

    @GetMapping("/requests/detailsOwner/{processId}")
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
        return "requestDetailsOwner";
    }

    @RequestMapping(value="/requests/detailsOwner/{processId}", method=RequestMethod.POST, params="action=acceptProcess")
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
        }else{
            orderProcess.setStatus(OrderProcessStatus.PENDING);
            orderProcess.setMessages(oldMessages);
            orderProcessRepo.save(orderProcess);
            model.addAttribute("note", "Sorry, your request failed. Please try it again later.");
            showRequests(user, model);
        }

        return "redirect:/requests";
    }

    @RequestMapping(value="/requests/detailsOwner/{processId}", method=RequestMethod.POST, params="action=acceptReturn")
    public String finishProcess(@PathVariable Long processId) {
        OrderProcess orderProcess = orderProcessRepo.findById(processId).get();
        orderProcess.setStatus(OrderProcessStatus.FINISHED);

        orderProcessHandler.updateOrderProcess(orderProcess.getMessages(), orderProcess);

        return "redirect:/requests";
    }

    @RequestMapping(value="/requests/detailsOwner/{processId}", method=RequestMethod.POST, params="action=appeal")
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

    @RequestMapping(value="/requests/detailsOwner/{processId}", method=RequestMethod.POST, params="action=deleteProcess")
    public String deleteByOwner(@PathVariable Long processId) {
        OrderProcess orderProcess = orderProcessRepo.findById(processId).get();
        orderProcessRepo.delete(orderProcess);

        return "redirect:/requests";
    }

    @RequestMapping(value="/requests/detailsOwner/{processId}", method=RequestMethod.POST, params="action=deny")
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
