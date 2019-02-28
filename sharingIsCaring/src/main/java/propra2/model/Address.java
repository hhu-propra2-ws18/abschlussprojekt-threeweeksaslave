package propra2.model;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class Address {

    private String street;
    private int houseNumber;
    private int postcode;
    private String city;
}
