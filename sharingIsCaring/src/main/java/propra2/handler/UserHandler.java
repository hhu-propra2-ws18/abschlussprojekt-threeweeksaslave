package propra2.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import propra2.database.Customer;
import propra2.model.ProPayAccount;
import propra2.model.UserRegistration;
import propra2.repositories.CustomerRepository;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Optional;

public class UserHandler {
    @Autowired
    private CustomerRepository customerRepo;

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
}
