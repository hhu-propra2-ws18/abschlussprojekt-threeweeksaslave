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
        orderProcess = orderProcessRepository.save(orderProcess);
        Assert.assertEquals(1, orderProcessRepository.findAll().size());
    }

    @Test
    public void findAllByOwnerIdForOneProcess(){
        OrderProcess orderProcess1 = new OrderProcess();
        orderProcess1.setOwnerId(30L);

        OrderProcess orderProcess2 = new OrderProcess();
        orderProcess2.setOwnerId(60L);

        orderProcessRepository.save(orderProcess1);
        orderProcessRepository.save(orderProcess2);

        Assert.assertEquals(1, orderProcessRepository.findAllByOwnerId(30L).size());
    }

    @Test
    public void findAllByRequestIdForOneProcess(){
        OrderProcess orderProcess1 = new OrderProcess();
        orderProcess1.setRequestId(50L);

        OrderProcess orderProcess2 = new OrderProcess();
        orderProcess2.setRequestId(80L);

        orderProcessRepository.save(orderProcess1);
        orderProcessRepository.save(orderProcess2);

        Assert.assertEquals(1, orderProcessRepository.findAllByRequestId(80L).size());
    }
}
