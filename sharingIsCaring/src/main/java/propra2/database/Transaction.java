package propra2.database;

import lombok.Data;
import propra2.model.TransactionType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Date;

@Data
@Entity
public class Transaction {

    @GeneratedValue
    @Id
    Long id;

    String userName;
    double amount;

    Date date;
    TransactionType transactionType;
}
