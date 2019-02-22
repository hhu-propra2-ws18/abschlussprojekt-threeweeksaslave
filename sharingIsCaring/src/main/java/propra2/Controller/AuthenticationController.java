package propra2.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import propra2.Security.service.RegistrationService;
import propra2.Security.validator.CustomerValidator;
import propra2.database.Customer;
import propra2.database.Notification;
import propra2.model.UserRegistration;
import propra2.repositories.CustomerRepository;
import propra2.repositories.NotificationRepository;

import java.io.File;
import java.net.URL;
import java.security.Principal;
import java.util.List;

@Controller
public class AuthenticationController {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private CustomerValidator customerValidator;

    @Autowired
    private NotificationRepository notificationRepo;

    @Autowired
    private RegistrationService registrationService;


    @GetMapping("/")
    public String start() {
        if (!customerRepo.findByUsername("admin").isPresent()) {
            UserRegistration admin = new UserRegistration();
            admin.setUserName("admin");
            admin.setEmailAddress("admin@admin.de");
            admin.setPassword("adminPass");
            admin.setPasswordConfirm("adminPass");
            admin.setRole("ADMIN");
            registrationService.saveCredentials(admin);
        }

        return "start";
    }

    /**
     * check if user exists
     *
     * @param username
     * @return
     */
    @PostMapping("/")
    public boolean userExists(final String username) {
        if (customerRepo.findByUsername(username).isPresent())
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
        Customer customer = customerRepo.findByUsername(user.getName()).get();
        List<Notification> notifications = notificationRepo.findAllByBorrowerId(customer.getCustomerId());
        model.addAttribute("notifications", notifications);
        model.addAttribute("user", customer);
        boolean admin = false;
        if (customer.getRole().equals("ADMIN")) {
            admin = true;
        }
        model.addAttribute("admin", admin);
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

        user.setRole("ROLE_USER");
        registrationService.saveCredentials(user);

        return "redirect:/home";
    }
}
