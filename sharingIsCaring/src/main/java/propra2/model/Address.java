package propra2.model;

import lombok.Data;

@Data
public class Address {

    private String street;
    private int houseNumber;
    private int postCode;
    private String city;
}
