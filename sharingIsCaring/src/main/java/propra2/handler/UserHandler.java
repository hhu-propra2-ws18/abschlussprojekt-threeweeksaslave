package propra2.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import propra2.database.Customer;
import propra2.database.Transaction;
import propra2.model.ProPayAccount;
import propra2.model.TransactionType;
import propra2.repositories.CustomerRepository;
import propra2.repositories.TransactionRepository;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserHandler {
    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private TransactionRepository transactionRepository;

    public Customer rechargeCredit(Customer customer, int amount){

       Mono<ProPayAccount> account =  WebClient.create().post().uri(builder ->
                builder
                        .path("localhost:8888/account/" + customer.getUsername())
                        .query("amount=" + amount)
                        .build())
               .retrieve()
               .bodyToMono(ProPayAccount.class);

        customer.setProPay(account.block());
        return customer;
    }

    @Transactional
    public ProPayAccount getProPayAccount(String username){
        ProPayAccount proPayAccount = getEntity(ProPayAccount.class,username);

        return proPayAccount;
    }

    private static <T> T getEntity(final Class<T> type, String username) {
        try {
            final Mono<T> mono = WebClient
                    .create()
                    .get()
                    .uri(builder ->
                            builder
                                    .path("localhost:8888/account/" + username)
                                    .build())
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .retrieve()
                    .bodyToMono(type);
            return mono.block();
        }
        catch(Exception e){
            return null;
        }
    }

    public void saveTransaction(double amount, TransactionType transactionType, String userName){
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setUserName(userName);
        transaction.setDate(new java.sql.Date(System.currentTimeMillis()));

        transactionRepository.save(transaction);
    }
    
    @Scheduled(fixedRate = 60000)
    public void syncProPayAcounts(){
        List<Customer> customers = customerRepo.findAll();

        for (Customer customer : customers) {
            customer.setProPay(getProPayAccount(customer.getUsername()));
            customerRepo.save(customer);
        }
    }
}
