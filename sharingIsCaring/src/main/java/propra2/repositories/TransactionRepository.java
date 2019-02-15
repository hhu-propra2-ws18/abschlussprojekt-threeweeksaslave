package propra2.repositories;

import org.springframework.data.repository.CrudRepository;
import propra2.database.Transaction;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    List<Transaction> findAll();
    List<Transaction> findAllByUserName(String userName);
}
