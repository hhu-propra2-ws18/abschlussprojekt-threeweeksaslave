package propra2.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import propra2.database.Customer;
import propra2.database.Transaction;
import propra2.handler.UserHandler;
import propra2.model.TransactionType;
import propra2.repositories.CustomerRepository;
import propra2.repositories.TransactionRepository;

import java.security.Principal;
import java.util.List;

@Controller
public class ProPayController {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private UserHandler userHandler;


    /**
     * get template to recharge Credit
     *
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/rechargeCredit")
    public String getRechargeCredit(Principal user, Model model) {
        Customer customer = customerRepo.findByUsername(user.getName()).get();
        model.addAttribute("user", customer);
        boolean admin = false;
        if (customer.getRole().equals("ADMIN")) {
            admin = true;
        }
        model.addAttribute("admin", admin);
        return "rechargeCredit";
    }

    /**
     * send amount to ProPayAccount and save Transaction in db
     *
     * @param user
     * @param amount
     * @param iban
     * @param model
     * @return
     */
    @PostMapping("/rechargeCredit")
    public String rechargeCredit(Principal user, int amount, String iban, Model model) {
        if (amount == 0 || iban == null) {
            return "redirect:/rechargeCredit";
        }
        Customer customer = customerRepo.findByUsername(user.getName()).get();
        Customer customer1 = userHandler.rechargeCredit(customer, amount);
        userHandler.saveTransaction(amount, TransactionType.RECHARGE, customer.getUsername());
        customerRepo.save(customer1);
        model.addAttribute("user", customer);
        return "redirect:/profile";
    }

    /**
     * get overview of transactions for a specific user
     *
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/transactions")
    public String getTransactions(Principal user, Model model) {
        List<Transaction> transactions = transactionRepo.findAllByUserName(user.getName());
        Customer customer = customerRepo.findByUsername(user.getName()).get();
        model.addAttribute("user", customer);
        model.addAttribute("transactions", transactions);
        if(transactions.size()==0){
            model.addAttribute("transactionsExist", false);
        }else{
            model.addAttribute("transactionsExist", true);
        }
        
        boolean admin = false;
        if (customer.getRole().equals("ADMIN")) {
            admin = true;
        }
        model.addAttribute("admin", admin);
        return "transactions";
    }
}
