package propra2.database;

import lombok.Data;
import propra2.model.Message;
import propra2.model.OrderProcessStatus;

import javax.persistence.*;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

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

    public void addMessages(ArrayList<Message> list) {
        list.addAll(messages);
        this.messages = list;
    }

    public boolean allValuesSet() {
        return this.getId() != null &&
                this.getOwnerId() != null &&
                this.getRequestId() != null &&
                this.getStatus() != null;
    }

    public Message createMessage(Principal user, String stringMessage) {
        Message message = new Message();
        message.setMessage(stringMessage);
        message.setDate(new java.sql.Date(System.currentTimeMillis()));
        message.setAuthor(user.getName());

        return message;
    }

    public boolean isCancelable() {
        Date today = new java.sql.Date(System.currentTimeMillis());
        return ChronoUnit.DAYS.between(LocalDate.parse(fromDate.toString()), LocalDate.parse(today.toString())) < 0;
    }
}
