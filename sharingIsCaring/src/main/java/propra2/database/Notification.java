package propra2.database;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Notification {
    @Id
    @GeneratedValue
    private Long id;

    String notification;
    Long borrowerId;
    Long processId;
}
