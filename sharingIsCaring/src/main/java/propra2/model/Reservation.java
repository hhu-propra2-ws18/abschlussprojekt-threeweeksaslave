package propra2.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Reservation implements Serializable {
    private double amount;
    private int id;
}
