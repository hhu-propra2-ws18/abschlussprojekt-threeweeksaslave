package propra2.database;

import lombok.Data;
import propra2.model.TransactionType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Transaction {

    @GeneratedValue
    @Id
    Long id;

    String userName;
    int amount;

    TransactionType transactionType;
}
