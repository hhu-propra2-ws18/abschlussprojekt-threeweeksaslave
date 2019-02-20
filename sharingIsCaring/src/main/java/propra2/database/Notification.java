package propra2.database;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
public class Notification {
    @Id
    @GeneratedValue
    private Long id;

    String notification;
    Long borrowerId;
}
