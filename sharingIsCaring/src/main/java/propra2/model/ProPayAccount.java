package propra2.model;

import lombok.Data;
import propra2.database.OrderProcess;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.util.ArrayList;
import java.util.List;

import static propra2.model.OrderProcessStatus.ACCEPTED;
import static propra2.model.OrderProcessStatus.PENDING;

@Data
@Embeddable
public class ProPayAccount {
    private String account;
    private double amount;

    @Embedded
    @ElementCollection
    private List<Reservation> reservations;

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public int getAvailableAmount(List<OrderProcess> orderProcessList){
        int result= (int) amount;
        for(Reservation reservation : reservations){
            result -= reservation.getAmount();
        }
        for(OrderProcess orderProcess : orderProcessList){
            if(orderProcess.getStatus()==PENDING || orderProcess.getStatus()==ACCEPTED){
                result -= orderProcess.getProduct().getTotalDailyFee(orderProcess.getFromDate());
            }
        }
        return result;
    }

    public void setReservations(ArrayList<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Reservation findReservationById(int reservationId) {
        for(Reservation reservation : reservations){
            if(reservation.getId() == reservationId) return reservation;
        }

        return null;
    }
}
