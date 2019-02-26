package propra2.repositories;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.database.Transaction;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    TransactionRepository transactionRepository;

    @Test
    public void testFindById(){
        Transaction transaction = new Transaction();
        transaction.setUserName("userName");

        transaction = transactionRepository.save(transaction);

        Optional<Transaction> transactionOptional = transactionRepository.findById(transaction.getId());

        Assert.assertEquals("userName", transactionOptional.get().getUserName());

    }

    @Test
    public void testFindAllByUserName(){
        Transaction transaction1 = new Transaction();
        transaction1.setUserName("uN");
        transaction1.setAmount(40);

        Transaction transaction2 = new Transaction();
        transaction2.setUserName("uN");
        transaction2.setAmount(10);

        Transaction transaction3 = new Transaction();
        transaction3.setUserName("name");
        transaction3.setAmount(5);

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);

        List<Transaction> transactionList = transactionRepository.findAllByUserName("uN");

        Assert.assertEquals(2, transactionList.size());
        Assert.assertEquals(40, transactionList.get(0).getAmount(), 0.1);
        Assert.assertEquals(10, transactionList.get(1).getAmount(), 0.1);
    }
}
