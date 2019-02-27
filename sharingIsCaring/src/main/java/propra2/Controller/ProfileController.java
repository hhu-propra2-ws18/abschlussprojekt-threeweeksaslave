package propra2.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import propra2.database.Customer;
import propra2.handler.UserHandler;
import propra2.model.Address;
import propra2.model.ProPayAccount;
import propra2.repositories.CustomerRepository;

import java.security.Principal;
import java.util.Optional;

@Controller
public class ProfileController {

    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private UserHandler userHandler;

    /**
     * show profile data
     *
     * @param model
     * @return profile template
     */
    @GetMapping("/profile")
    public String getUserDataById(Principal user, Model model) {
        Long loggedInId = getUserId(user);

        Customer customer = customerRepo.findById(loggedInId).get();
        ProPayAccount newProPayAcc = userHandler.getProPayAccount(customer.getUsername());
        if(newProPayAcc!=null){
            customer.setProPay(newProPayAcc);
            customerRepo.save(customer);
        }
        model.addAttribute("user", customer);

         boolean admin = false;
         if (customer.getRole().equals("ADMIN")) {
              admin = true;
            }
        model.addAttribute("admin", admin);
        return "profile";
    }

    private Long getUserId(Principal user) {
        String username = user.getName();
        Optional<Customer> customer = customerRepo.findByUsername(username);
        Long id = customer.get().getCustomerId();
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
        Optional<Customer> customer = customerRepo.findById(userId);
        model.addAttribute("user", customer.get());
        boolean admin = false;
        if (customer.get().getRole().equals("ADMIN")) {
            admin = true;
        }
        model.addAttribute("admin", admin);
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
        Optional<Customer> customer = customerRepo.findById(userId);
        customer.get().setAddress(address);
        customer.get().setMail(mail);
        customerRepo.save(customer.get());
        model.addAttribute("user", customer.get());
        return "redirect:/profile";
    }

    @GetMapping("/faqs")
    public String getFAQs(Principal user, Model model){
        Long userId = getUserId(user);
        Optional<Customer> customer = customerRepo.findById(userId);
        model.addAttribute("user", customer.get());
        return "faqs";
    }
}
