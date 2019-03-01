package propra2.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import propra2.database.Customer;
import propra2.database.OrderProcess;
import propra2.database.Product;
import propra2.handler.OrderProcessHandler;
import propra2.model.Message;
import propra2.model.OrderProcessStatus;
import propra2.repositories.CustomerRepository;
import propra2.repositories.OrderProcessRepository;
import propra2.repositories.ProductRepository;

import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderProcessController {
    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private OrderProcessRepository orderProcessRepo;

    @Autowired
    private OrderProcessHandler orderProcessHandler;


    /**
     * get page to start an orderProcess
     *
     * @param id
     * @param user
     * @param model
     * @param notEnoughMoney
     * @param incorrectDates
     * @param ownProduct
     * @param availability
     * @return
     */
    @GetMapping("/product/{id}/orderProcess")
    public String startOrderProcess(@PathVariable Long id, final Principal user, Model model, boolean notEnoughMoney, boolean incorrectDates, boolean ownProduct, boolean availability) {
        Customer customer = customerRepo.findByUsername(user.getName()).get();
        Product product = productRepo.findById(id).get();
        model.addAttribute("product", product);
        model.addAttribute("user", customer);
        model.addAttribute("notEnoughMoney", notEnoughMoney);
        model.addAttribute("incorrectDates", incorrectDates);
        model.addAttribute("ownProduct", ownProduct);
        model.addAttribute("availability", availability);
        boolean admin = false;
        if (customer.getRole().equals("ADMIN")) {
            admin = true;
        }
        model.addAttribute("admin", admin);
        return "orderProcess";
    }

    /**
     * start an orderProcess, check if customer has enough money and if the period is correct
     *
     * @param id
     * @param message
     * @param from
     * @param to
     * @param user
     * @param model
     * @return
     */
    @PostMapping("/product/{id}/orderProcess")
    public String postOrderProcess(@PathVariable Long id, String message, String from, String to, final Principal user, Model model) {
        Customer customer = customerRepo.findByUsername(user.getName()).get();
        Product product = productRepo.findById(id).get();
        double totalAmount = product.getExpectedTotalAmount(Date.valueOf(from), Date.valueOf(to));
        List<OrderProcess> orderProcessesOfRequester = orderProcessRepo.findAllByRequestId(customer.getCustomerId());

        if (!customer.hasEnoughMoney(totalAmount, orderProcessesOfRequester)) {
            return startOrderProcess(id, user, model, true, false, false, false);
        }

        if (product.getOwner().getCustomerId().equals(customer.getCustomerId())) {
            return startOrderProcess(id, user, model, false, false, true, false);
        }
        if (!product.isForSale()) {
            if (!orderProcessHandler.correctDates(Date.valueOf(from), Date.valueOf(to))) {
                return startOrderProcess(id, user, model, false, true, false, false);
            }

            if (!orderProcessHandler.checkAvailability(orderProcessRepo, product, from, to)) {
                return startOrderProcess(id, user, model, false, false, false, true);
            }
        }

        OrderProcess orderProcess = new OrderProcess();
        orderProcess.setOwnerId(product.getOwner().getCustomerId());

        orderProcess.setRequestId(customer.getCustomerId());

        orderProcess.setProduct(product);
        ArrayList<Message> messages = new ArrayList<>();
        Message newMessage = orderProcess.createMessage(user, message);
        messages.add(newMessage);
        orderProcess.setMessages(messages);

        orderProcess.setFromDate(Date.valueOf(from));
        orderProcess.setToDate(Date.valueOf(to));

        orderProcess.setStatus(OrderProcessStatus.PENDING);

        orderProcessRepo.save(orderProcess);

        return "redirect:/home";
    }

}
