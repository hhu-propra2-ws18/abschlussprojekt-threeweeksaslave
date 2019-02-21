package propra2.database;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import propra2.model.OrderProcessStatus;

public class OrderProcessTest {

    private OrderProcess orderProcess;

    @Before
    public void setup(){
        orderProcess = new OrderProcess();
    }

    @Test
    public void testAllValuesSetFalse(){
        orderProcess.setStatus(OrderProcessStatus.FINISHED);
        Assert.assertEquals(false, orderProcess.allValuesSet());
    }

    @Test
    public void testAllValuesSetTrue(){
        orderProcess.setStatus(OrderProcessStatus.CONFLICT);
        orderProcess.setRequestId(1L);
        orderProcess.setOwnerId(2L);
        orderProcess.setId(3L);

        Assert.assertEquals(true, orderProcess.allValuesSet());
    }
}
