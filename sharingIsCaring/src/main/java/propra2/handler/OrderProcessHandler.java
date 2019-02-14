package propra2.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import propra2.database.OrderProcess;
import propra2.repositories.CustomerRepository;
import propra2.repositories.OrderProcessRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class OrderProcessHandler {

    @Autowired
    CustomerRepository customerRepository;

    public void updateOrderProcess(OrderProcess orderProcess, OrderProcessRepository orderProcessRepository) throws IOException {
        OrderProcess oldOrderProcess = orderProcessRepository.findById(orderProcess.getId()).get();
        List<String> messages  = oldOrderProcess.getMessages();
        orderProcess.addMessages(messages);

        String rentingAccount = customerRepository.findById(orderProcess.getRequestId()).get().getUsername();
        String ownerAccount = customerRepository.findById(orderProcess.getOwnerId()).get().getUsername();
        switch (orderProcess.getStatus()) {
            case DENIED:
                orderProcessRepository.save(orderProcess);
                break;
            case ACCEPTED:
                orderProcessRepository.save(orderProcess);
                Integer deposit = orderProcess.getProduct().getDeposit();
                //Propay Kautionsbetrag blocken
                WebClient.create().post().uri(builder ->
                        builder
                                .path("localhost:8888/reservation/reserve/" + rentingAccount + "/" + ownerAccount)
                                .query(deposit.toString())
                                .build());
                break;
            case FINISHED:
                orderProcessRepository.save(orderProcess);
                //Kaution wird wieder freigegeben
                WebClient.create().post().uri(builder ->
                        builder
                                .path("localhost:8888/reservation/release/" + rentingAccount)
                                .build());
                //TODO: Konfliktlöser
                break;
            default:
                throw new IllegalArgumentException("Bad Request: Unknown Process Status");
        }
    }
}
