package propra2.handler;

import org.springframework.web.reactive.function.client.WebClient;
import propra2.database.Customer;
import propra2.database.OrderProcess;
import propra2.model.ProPayAccount;
import propra2.model.Reservation;
import propra2.repositories.CustomerRepository;
import propra2.repositories.OrderProcessRepository;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Optional;

public class OrderProcessHandler {
    private UserHandler userHandler;

    public OrderProcessHandler() {
        this.userHandler = new UserHandler();
    }

    public void updateOrderProcess(OrderProcess orderProcess, OrderProcessRepository orderProcessRepository, CustomerRepository customerRepository) {
        OrderProcess oldOrderProcess = orderProcessRepository.findById(orderProcess.getId()).get();

        if(!(oldOrderProcess.getMessages() == null))
        {
            ArrayList<String> messages  = oldOrderProcess.getMessages();
            orderProcess.addMessages(messages);
        }

        Optional<Customer> rentingAccount = customerRepository.findById(oldOrderProcess.getRequestId());
        Optional<Customer> ownerAccount = customerRepository.findById(orderProcess.getOwnerId());
        
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
                                .path("localhost:8888/reservation/reserve/" + rentingAccount.get().getUsername() + "/" + ownerAccount.get().getUsername())
                                .query("amount=" + deposit)
                                .build())
                        .retrieve()
                        .bodyToMono(Reservation.class);

                rentingAccount.get().getProPay().addReservation(reservation.block());
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

                rentingAccount.get().setProPay(account.block());
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

                rentingAccount.get().setProPay(account.block());
                orderProcessRepository.save(orderProcess);
                break;
            default:
                throw new IllegalArgumentException("Bad Request: Unknown Process Status");
        }
    }
}
