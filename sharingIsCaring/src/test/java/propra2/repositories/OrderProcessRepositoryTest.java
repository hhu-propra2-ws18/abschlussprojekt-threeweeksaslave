package propra2.repositories;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.database.OrderProcess;

@RunWith(SpringRunner.class)
@DataJpaTest
public class OrderProcessRepositoryTest {

    @Autowired
    OrderProcessRepository orderProcessRepository;

    @Test
    public void findAllIsEmpty(){
        Assert.assertEquals(true, orderProcessRepository.findAll().isEmpty());
    }

    @Test
    public void findAllContainsListWithSizeOne(){
        OrderProcess orderProcess = new OrderProcess();
        orderProcess.setId(1L);
        orderProcessRepository.save(orderProcess);
        Assert.assertEquals(1, orderProcessRepository.findAll().size());
    }
}
