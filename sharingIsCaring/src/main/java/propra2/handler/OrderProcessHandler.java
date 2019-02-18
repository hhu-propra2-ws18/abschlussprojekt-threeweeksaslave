package propra2.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import propra2.database.Customer;
import propra2.database.OrderProcess;
import propra2.model.ProPayAccount;
import propra2.model.Reservation;
import propra2.repositories.CustomerRepository;
import propra2.repositories.OrderProcessRepository;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class OrderProcessHandler {

    @Autowired
    CustomerRepository customerRepository;


    @Autowired
    UserHandler userHandler;

    public void updateOrderProcess(OrderProcess orderProcess, OrderProcessRepository orderProcessRepository) throws IOException {
        OrderProcess oldOrderProcess = orderProcessRepository.findById(orderProcess.getId()).get();

        if(!(oldOrderProcess.getMessages() == null))
        {
            List<String> messages  = oldOrderProcess.getMessages();
            orderProcess.addMessages(messages);
        }


        Customer rentingAccount = customerRepository.findById(orderProcess.getRequestId()).get();
        Customer ownerAccount = customerRepository.findById(orderProcess.getOwnerId()).get();

        //Customer rentingAccount = orderProcessRepository.findByRequestId(orderProcess.getRequestId()).get();
        //Customer ownerAccount = orderProcessRepository.findByOwnerId(orderProcess.getOwnerId()).get();
        Mono<ProPayAccount> account;
        switch (orderProcess.getStatus()) {
            case DENIED:
                orderProcessRepository.save(orderProcess);
                break;
            case ACCEPTED:
                Integer deposit = orderProcess.getProduct().getDeposit();
                //Propay Kautionsbetrag blocken
                Mono<Reservation> reservation =  WebClient.create().post().uri(builder ->
                        builder
                                .path("localhost:8888/reservation/reserve/" + rentingAccount.getUsername() + "/" + ownerAccount.getUsername())
                                .query("amount=" + deposit)
                                .build())
                        .retrieve()
                        .bodyToMono(Reservation.class);

                rentingAccount.setProPay(userHandler.getProPayAccount(rentingAccount.getUsername()));
                orderProcess.setReservationId(reservation.block().getId());
                orderProcessRepository.save(orderProcess);
                break;
            case FINISHED:
                //Kaution wird wieder freigegeben
                account =  WebClient.create().post().uri(builder ->
                        builder
                                .path("localhost:8888/reservation/release/" + rentingAccount)
                                .build())
                        .retrieve()
                        .bodyToMono(ProPayAccount.class);

                rentingAccount.setProPay(account.block());
                orderProcessRepository.save(orderProcess);
                break;
            case RETURNED:
                //TODO: Tagessatz wird nicht mehr abgerechnet
                break;
            case CONFLICT:
                //TODO: Konfliktlöser
                break;
            case PUNISHED:
                int reservationId = orderProcess.getReservationId();
                account =  WebClient.create().post().uri(builder ->
                        builder
                                .path("localhost:8888/reservation/punish/" + rentingAccount)
                                .query("reservationId=" + reservationId)
                                .build())
                        .retrieve()
                        .bodyToMono(ProPayAccount.class);

                rentingAccount.setProPay(account.block());
                orderProcessRepository.save(orderProcess);
                break;
            default:
                throw new IllegalArgumentException("Bad Request: Unknown Process Status");
        }
    }
}
