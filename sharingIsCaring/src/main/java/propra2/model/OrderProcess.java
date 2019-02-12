package propra2.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class OrderProcess {

    @GeneratedValue
    @Id
    Long id;

    Long ownerId;
    Long requestId;

    OrderProcessStatus status;
}
