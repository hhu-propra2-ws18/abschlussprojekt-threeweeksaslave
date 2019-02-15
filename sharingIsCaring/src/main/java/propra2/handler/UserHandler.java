package propra2.handler;

import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import propra2.database.Customer;
import propra2.model.ProPayAccount;
import reactor.core.publisher.Mono;

public class UserHandler {

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
