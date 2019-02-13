package propra2.database;

import lombok.Data;
import propra2.model.OrderProcessStatus;

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

    public boolean allValuesSet() {
        if(this.getId() == null||
                this.getOwnerId() == null ||
                this.getRequestId() == null ||
                this.getStatus() == null) {

            return false;
        }
        return true;
    }
}
