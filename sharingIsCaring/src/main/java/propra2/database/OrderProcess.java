package propra2.database;

import lombok.Data;
import org.apache.tomcat.util.digester.ArrayStack;
import propra2.model.OrderProcessStatus;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

@Data
@Entity
public class OrderProcess {

    @GeneratedValue
    @Id
    Long id;

    @OneToOne
    Product product;

    Long ownerId;
    Long requestId;

    int reservationId;

    @Lob
    ArrayList<String> messages;

    OrderProcessStatus status;

    private Date fromDate;
    private Date toDate;

    public void addMessages(ArrayList<String> list){
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
