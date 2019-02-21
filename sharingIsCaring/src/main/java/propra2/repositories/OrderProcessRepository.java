package propra2.repositories;

import org.springframework.data.repository.CrudRepository;
import propra2.database.Customer;
import propra2.database.OrderProcess;
import propra2.database.Product;
import propra2.model.OrderProcessStatus;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface OrderProcessRepository extends CrudRepository<OrderProcess, Long> {
    List<OrderProcess> findAll();
    @Transactional
    List<OrderProcess> findAllByOwnerId(Long id);
    @Transactional
    List<OrderProcess> findAllByRequestId(Long id);
    Optional<Customer> findByOwnerId(Long id);
    Optional<Customer> findByRequestId(Long id);

    @Transactional
    List<OrderProcess> findByProduct(Product product);

    @Transactional
    List<OrderProcess> findByStatus(OrderProcessStatus status);
}
