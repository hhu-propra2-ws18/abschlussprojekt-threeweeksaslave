package propra2.model;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
public class Reservation {
    private double amount;
    private int id;
}
