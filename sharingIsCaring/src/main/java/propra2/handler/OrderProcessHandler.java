package propra2.handler;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import propra2.Controller.RequestController;
import propra2.database.Customer;
import propra2.database.Message;
import propra2.database.OrderProcess;
import propra2.database.Product;
import propra2.model.OrderProcessStatus;
import propra2.model.ProPayAccount;
import propra2.model.Reservation;
import propra2.model.TransactionType;
import propra2.repositories.CustomerRepository;
import propra2.repositories.OrderProcessRepository;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public class OrderProcessHandler {
    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private OrderProcessRepository orderProcessRepo;

    @Autowired
    private UserHandler userHandler;

    @Autowired
    private RequestController requestController;


    public boolean updateOrderProcess(ArrayList<Message> oldMessages, OrderProcess orderProcess) {

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
                return acceptProcess(orderProcess, rentingAccount, ownerAccount);
            case FINISHED:
                return finished(orderProcess, rentingAccount);
            case CONFLICT:
                orderProcessRepo.save(orderProcess);
                break;
            case PUNISHED:
                return punished(orderProcess, rentingAccount);
            case CANCELED:
                cancelOrder(orderProcess);
                break;
            default:
                throw new IllegalArgumentException("Bad Request: Unknown Process Status");
        }
        return false;
    }

    private boolean acceptProcess(OrderProcess orderProcess, Customer rentingAccount, Customer ownerAccount) {
        Integer deposit = orderProcess.getProduct().getDeposit();
        //Propay Kautionsbetrag blocken
        try {
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

            if(userHandler.getProPayAccount(rentingAccount.getUsername())!=null) {
                rentingAccount.setProPay(userHandler.getProPayAccount(rentingAccount.getUsername()));
            }
            orderProcess.setReservationId(reservation.block().getId());
            orderProcessRepo.save(orderProcess);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    private boolean finished(OrderProcess orderProcess, Customer rentingAccount) {
        //Kaution wird wieder freigegeben
        try {
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean punished(OrderProcess orderProcess, Customer rentingAccount) {
        int reservationId = orderProcess.getReservationId();
        Customer requester = customerRepo.findById(orderProcess.getRequestId()).get();
        double amount = requester.getProPay().findReservationById(reservationId).getAmount();
        try {
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
            Customer ownerAccount = customerRepo.findById(orderProcess.getOwnerId()).get();
            if(amount>0){
                userHandler.saveTransaction(amount, TransactionType.DEPOSITCHARGE, rentingAccount.getUsername());
                userHandler.saveTransaction(amount, TransactionType.RECEIVEDDEPOSIT, ownerAccount.getUsername());
            }
            return true;
        }catch(Exception e) {return false;}

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
                	if(!(process.getStatus() == OrderProcessStatus.DENIED ||
							process.getStatus() == OrderProcessStatus.PUNISHED ||
							process.getStatus() == OrderProcessStatus.FINISHED))
                    available = false;
                    break;
                }
            }
        }

        return available;
    }

    public boolean correctDates(Date from, Date to) {
        Date today = new Date(1);
        today.setDate(LocalDate.now().getDayOfMonth());
        today.setMonth(LocalDate.now().getMonthValue()-1);
        today.setYear(LocalDate.now().getYear()-1900);
        //case today until today
        if(from.toString().equals(to.toString()) && from.toString().equals(today.toString())){
            return true;
        }
        //case to is before today
        if(to.before(today)){
            return false;
        }
        //case future rent for single day
        if(from.toString().equals(to.toString()) && today.before(from)){
            return true;
        }
        return from.before(to);
    }

    public boolean payDailyFee(OrderProcess orderProcess) {
        double dailyFee = orderProcess.getProduct().getTotalDailyFee(orderProcess.getFromDate());
        String rentingAccount = customerRepo.findById(orderProcess.getRequestId()).get().getUsername();
        String ownerAccount = customerRepo.findById(orderProcess.getOwnerId()).get().getUsername();
        try {
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

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void cancelOrder(OrderProcess orderProcess){
        Customer rentingAccount = customerRepo.findById(orderProcess.getRequestId()).get();
        finished(orderProcess, rentingAccount);
        orderProcessRepo.delete(orderProcess);
    }
}
