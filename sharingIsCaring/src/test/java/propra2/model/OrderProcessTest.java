package propra2.model;

import org.junit.Assert;
import org.junit.Test;

public class OrderProcessTest {

    @Test
    public void allValuesSetFalse(){
        OrderProcess orderProcess = new OrderProcess();
        orderProcess.setId(10L);
        orderProcess.setOwnerId(9L);
        orderProcess.setRequestId(4L);

        Assert.assertEquals(false, orderProcess.allValuesSet());
    }

    @Test
    public void allValuesSetTrue(){
        OrderProcess orderProcess = new OrderProcess();
        orderProcess.setId(10L);
        orderProcess.setOwnerId(9L);
        orderProcess.setRequestId(4L);
        orderProcess.setStatus(OrderProcessStatus.ACCEPTED);

        Assert.assertEquals(true, orderProcess.allValuesSet());
    }
}
