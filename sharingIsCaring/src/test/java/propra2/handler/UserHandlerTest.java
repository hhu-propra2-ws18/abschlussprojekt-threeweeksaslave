package propra2.handler;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.database.Transaction;
import propra2.model.TransactionType;
import propra2.repositories.TransactionRepository;

import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserHandlerTest {

    @Autowired
    TransactionRepository transactionRepository;

    private UserHandler userHandler;

    @Before
    public void setup(){
      userHandler = new UserHandler();
    }

    @Test
    public void testSaveTransaction(){
        int amount = 100;
        TransactionType transactionType = TransactionType.RECHARGE;
        String userName = "userName";
        userHandler.transactionRepository = this.transactionRepository;
        userHandler.saveTransaction(amount, transactionType, userName);

        List<Transaction> transactions = transactionRepository.findAllByUserName("userName");

        Assert.assertEquals(1, transactions.size());
        Assert.assertEquals(TransactionType.RECHARGE, transactions.get(0).getTransactionType());
    }
}
