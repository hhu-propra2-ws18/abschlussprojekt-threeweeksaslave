package propra2.database;

import lombok.Data;
import propra2.model.Message;
import propra2.model.OrderProcessStatus;

import javax.persistence.*;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.sql.Date;

@Data
@Entity
public class OrderProcess {

    @GeneratedValue
    @Id
    Long id;

    @ManyToOne
    Product product;

    Long ownerId;
    Long requestId;

    int reservationId;

    @Lob
    ArrayList<Message> messages;

    OrderProcessStatus status;

    private Date fromDate;
    private Date toDate;

    public void addMessages(ArrayList<Message> list){
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

    public Message createMessage(Principal user, String stringMessage){
        Message message = new Message();
        message.setMessage(stringMessage);
        message.setDate(new java.sql.Date(System.currentTimeMillis()));
        message.setAuthor(user.getName());

        return message;
    }

    public boolean isCancelable() {
        Date today = new java.sql.Date(System.currentTimeMillis());
        if(ChronoUnit.DAYS.between(LocalDate.parse(fromDate.toString()), LocalDate.parse(today.toString()))<0) return true;
        return false;
    }
}
