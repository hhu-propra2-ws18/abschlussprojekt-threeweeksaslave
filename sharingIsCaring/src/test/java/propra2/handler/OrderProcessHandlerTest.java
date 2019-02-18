package propra2.handler;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.database.Customer;
import propra2.database.OrderProcess;
import propra2.model.OrderProcessStatus;
import propra2.repositories.CustomerRepository;
import propra2.repositories.OrderProcessRepository;

import java.io.IOException;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class OrderProcessHandlerTest {

    private OrderProcessHandler orderProcessHandler;

    @Autowired
    OrderProcessRepository orderProcessRepository;

    @Autowired
    CustomerRepository customerRepository;

    public OrderProcessHandlerTest() {

        orderProcessHandler = new OrderProcessHandler();
    }

    @Test
    public void updateOrderProcessTest() throws IOException {
        OrderProcess orderProcess = new OrderProcess();
        orderProcess.setOwnerId(5678L);
        orderProcess.setRequestId(3456L);
        orderProcess.setStatus(OrderProcessStatus.PENDING);

        Customer customer1 = new Customer();
        customer1.setCustomerId(5678L);
        Customer customer2 = new Customer();
        customer2.setCustomerId(3456L);

        customerRepository.save(customer1);
        customerRepository.save(customer2);

        orderProcess = orderProcessRepository.save(orderProcess);

        orderProcess.setStatus(OrderProcessStatus.DENIED);

        orderProcessHandler.updateOrderProcess(orderProcess, orderProcessRepository, customerRepository);

        Optional<OrderProcess> expectedOrderProcess = orderProcessRepository.findById(orderProcess.getId());

        Assertions.assertThat(expectedOrderProcess.get().getStatus().toString()).isEqualTo("DENIED");
    }



}

