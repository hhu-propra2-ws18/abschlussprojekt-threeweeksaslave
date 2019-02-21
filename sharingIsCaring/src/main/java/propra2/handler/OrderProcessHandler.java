package propra2.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import propra2.database.Customer;
import propra2.database.OrderProcess;
import propra2.database.Product;
import propra2.model.ProPayAccount;
import propra2.model.Reservation;
import propra2.repositories.CustomerRepository;
import propra2.repositories.OrderProcessRepository;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderProcessHandler {
    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private OrderProcessRepository orderProcessRepo;

    @Autowired
    private UserHandler userHandler;


    public void updateOrderProcess(ArrayList<String> oldMessages, OrderProcess orderProcess) {

        if (!(oldMessages == null)) {
            orderProcess.addMessages(oldMessages);
        }

        Customer rentingAccount = customerRepo.findById(orderProcess.getRequestId()).get();
        Customer ownerAccount = customerRepo.findById(orderProcess.getOwnerId()).get();

        switch (orderProcess.getStatus()) {
            case DENIED:
                orderProcessRepo.save(orderProcess);
                break;
            case ACCEPTED:
                acceptProcess(orderProcess, rentingAccount, ownerAccount);
                break;
            case FINISHED:
                finished(orderProcess, rentingAccount);
                break;
            case RETURNED:
                //TODO: Tagessatz wird nicht mehr abgerechnet
                break;
            case CONFLICT:
                //TODO: Konfliktlöser
                break;
            case PUNISHED:
                punished(orderProcess, rentingAccount);
                break;
            default:
                throw new IllegalArgumentException("Bad Request: Unknown Process Status");
        }
    }

    private void acceptProcess(OrderProcess orderProcess, Customer rentingAccount, Customer ownerAccount) {
        Integer deposit = orderProcess.getProduct().getDeposit();
        //Propay Kautionsbetrag blocken
        Mono<Reservation> reservation = WebClient
                .create()
                .post()
                .uri(builder ->
                        builder.scheme("http")
                                .host("localhost")
                                .port(8888)
                                .path("/reservation/reserve/")
                                .pathSegment(rentingAccount.getUsername())
                                .pathSegment(ownerAccount.getUsername())
                                .queryParam("amount", deposit)
                                .build())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(Reservation.class);

        rentingAccount.setProPay(userHandler.getProPayAccount(rentingAccount.getUsername()));
        orderProcess.setReservationId(reservation.block().getId());
        orderProcessRepo.save(orderProcess);
    }

    private void finished(OrderProcess orderProcess, Customer rentingAccount) {
        //Kaution wird wieder freigegeben
        Mono<ProPayAccount> account = WebClient
                .create()
                .post()
                .uri(builder ->
                        builder.scheme("http")
                                .host("localhost")
                                .port(8888)
                                .path("/reservation/release/" + rentingAccount.getUsername())
                                .queryParam("reservationId", orderProcess.getReservationId())
                                .build())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(ProPayAccount.class);

        rentingAccount.setProPay(account.block());
        orderProcessRepo.save(orderProcess);
    }

    private void punished(OrderProcess orderProcess, Customer rentingAccount) {
        int reservationId = orderProcess.getReservationId();
        Mono<ProPayAccount> account = WebClient.create().post().uri(builder ->
                builder
                        .path("localhost:8888/reservation/punish/" + rentingAccount.getUsername())
                        .queryParam("reservationId", reservationId)
                        .build())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(ProPayAccount.class);

        rentingAccount.setProPay(account.block());
        orderProcessRepo.save(orderProcess);
    }

    public boolean checkAvailability(OrderProcessRepository orderProcessRepository, Product product, String from, String to) {
        List<OrderProcess> processes = orderProcessRepository.findByProduct(product);

        Date checkFrom = java.sql.Date.valueOf(from);
        Date checkTo = java.sql.Date.valueOf(to);

        boolean available = true;

        if (processes.isEmpty()) {
            return true;
        } else {
            for (OrderProcess process : processes) {
                Date dateFrom = process.getFromDate();
                Date dateTo = process.getToDate();
                if (dateFrom.before(checkFrom) && dateTo.before(checkFrom)) {
                } else if (dateFrom.after(checkTo) && dateTo.after(checkTo)) {
                } else {
                    available = false;
                    break;
                }
            }
        }

        return available;
    }

    public boolean correctDates(Date from, Date to) {
        if (from.equals(to)) return true;
        return from.before(to);
    }

    public void payDailyFee(OrderProcess orderProcess, CustomerRepository customerRepository) {
        double dailyFee = orderProcess.getProduct().getTotalDailyFee(orderProcess.getFromDate(), orderProcess.getToDate());
        String rentingAccount = customerRepository.findById(orderProcess.getRequestId()).get().getUsername();
        String ownerAccount = customerRepository.findById(orderProcess.getOwnerId()).get().getUsername();
        Mono<String> response = WebClient
                .create()
                .post()
                .uri(builder ->
                        builder.scheme("http")
                                .host("localhost")
                                .port(8888)
                                .path("/account/" + rentingAccount + "/transfer/" + ownerAccount)
                                .queryParam("amount", dailyFee)
                                .build())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(String.class);
        response.block();
    }
}
