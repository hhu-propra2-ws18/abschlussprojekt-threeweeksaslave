package propra2.model;

import lombok.Data;

import javax.persistence.Lob;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProPayAccount {
    private String account;
    private double amount;

    @Lob
    private List<Reservation> reservations = new ArrayList<>();
}
