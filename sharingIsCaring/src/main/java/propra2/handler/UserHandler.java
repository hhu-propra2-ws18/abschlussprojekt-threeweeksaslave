package propra2.handler;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import propra2.database.Customer;
import propra2.model.ProPayAccount;
import reactor.core.publisher.Mono;

public class UserHandler {

    public Customer rechargeCredit(Customer customer, int amount){
        System.out.println(amount);
        WebClient.create().post().uri(builder ->
                builder
                        .path("localhost:8888/account/" + customer.getUsername())
                        .query("amount=" + amount)
                        .build());

        ProPayAccount proPayAccount = getEntity(ProPayAccount.class,customer.getUsername());
        customer.setProPay(proPayAccount);
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
