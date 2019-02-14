package propra2.database;

import lombok.Data;
import propra2.model.OrderProcessStatus;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@Data
@Entity
public class OrderProcess {

    @GeneratedValue
    @Id
    Long id;

    Product product;
    Long ownerId;
    Long requestId;

    List<String> messages;

    OrderProcessStatus status;

    public void addMessages(List<String> list){
        list.addAll(messages);
        this.messages = list;
    }

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
