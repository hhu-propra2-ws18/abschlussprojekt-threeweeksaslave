package propra2.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class OrderProcess {

    @GeneratedValue
    @Id
    Long id;

    List<String> messages;

    Long ownerId;
    Long requestId;

    OrderProcessStatus status;

    Long product_id;

    public void addMessages(List<String> messages){
        messages.addAll(this.messages);
        this.messages = messages;
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
