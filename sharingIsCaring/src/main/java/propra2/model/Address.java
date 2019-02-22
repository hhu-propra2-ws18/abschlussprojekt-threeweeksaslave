package propra2.model;

import lombok.Data;
import reactor.util.annotation.Nullable;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Data
@Embeddable
public class Address {

    private String street;
    private int houseNumber;
    private int postcode;
    private String city;
}
