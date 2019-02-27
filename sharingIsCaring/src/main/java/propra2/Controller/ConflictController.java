package propra2.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import propra2.database.Customer;
import propra2.database.OrderProcess;
import propra2.handler.OrderProcessHandler;
import propra2.handler.UserHandler;
import propra2.model.OrderProcessStatus;
import propra2.model.ProPayAccount;
import propra2.model.TransactionType;
import propra2.repositories.CustomerRepository;
import propra2.repositories.OrderProcessRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ConflictController {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private OrderProcessRepository orderProcessRepo;

    @Autowired
    private OrderProcessHandler orderProcessHandler;

    @Autowired
    private UserHandler userHandler;


    /**
     * get overview of all conflicts
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/conflicts")
    public String getConflicts(Principal user, Model model){
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepo.findById(userId);
        List<OrderProcess> processes = orderProcessRepo.findByStatus(OrderProcessStatus.CONFLICT);
        model.addAttribute("processes", processes);
        boolean admin = false;
        if(customer.get().getRole().equals("ADMIN")){
            admin = true;
        }
        model.addAttribute("admin", admin);
        return "conflict";
    }

    private Long getUserId(Principal user) {
        String username = user.getName();
        Optional<Customer> customer = customerRepo.findByUsername(username);
        Long id = customer.get().getCustomerId();
        return id;
    }

    /**
     * get Details to a specific conflict
     * @param processId
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/conflicts/details/{processId}")
    public String showConflictDetails(@PathVariable Long processId, Principal user, final Model model) {
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepo.findById(userId);
        Optional<OrderProcess> process = orderProcessRepo.findById(processId);

        model.addAttribute("user", customer.get());
        model.addAttribute("product", process.get().getProduct());
        model.addAttribute("process", process.get());
        model.addAttribute("owner", customerRepo.findById(process.get().getOwnerId()).get());
        model.addAttribute("borrower", customerRepo.findById(process.get().getRequestId()).get());
        boolean admin = false;
        if(customer.get().getRole().equals("ADMIN")){
            admin = true;
        }
        model.addAttribute("admin", admin);
        return "conflictDetails";
    }

    /**
     * confirm Conflict -> the caution is send to the owner
     * @param processId
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value="/conflicts/details/{processId}", method= RequestMethod.POST, params="action=confirm")
    public String confirmConflict(@PathVariable Long processId, Model model, Principal user) {
        OrderProcess orderProcess = orderProcessRepo.findById(processId).get();
        orderProcess.setStatus(OrderProcessStatus.PUNISHED);

        Customer rentingAccount = customerRepo.findById(orderProcess.getRequestId()).get();
        ProPayAccount proPayAccount = rentingAccount.getProPay();

        int reservationId = orderProcess.getReservationId();
        double amount = proPayAccount.findReservationById(reservationId).getAmount();

        boolean successful = orderProcessHandler.updateOrderProcess(new ArrayList<>(), orderProcess);
        if(successful){
            Customer ownerAccount = customerRepo.findById(orderProcess.getOwnerId()).get();

            if(amount>0){
                userHandler.saveTransaction(amount, TransactionType.DEPOSITCHARGE, rentingAccount.getUsername());
                userHandler.saveTransaction(amount, TransactionType.RECEIVEDDEPOSIT, ownerAccount.getUsername());
            }
            return "redirect:/conflicts";
        }else{

            orderProcess.setStatus(OrderProcessStatus.CONFLICT);
            rentingAccount.setProPay(proPayAccount);
            customerRepo.save(rentingAccount);
            orderProcessRepo.save(orderProcess);


            model.addAttribute("note", "Sorry, connection to your ProPayAccount failed. Please try it again later.");
            return getConflicts(user, model);
        }

    }

    /**
     * reject conflict -> the caution will be released
     * @param processId
     * @return
     */
    @RequestMapping(value="/conflicts/details/{processId}", method=RequestMethod.POST, params="action=reject")
    public String rejectConflict(@PathVariable Long processId) {
        OrderProcess orderProcess = orderProcessRepo.findById(processId).get();
        orderProcess.setStatus(OrderProcessStatus.FINISHED);
        orderProcessHandler.updateOrderProcess(new ArrayList<>(), orderProcess);

        return "redirect:/conflicts";
    }
}
