package propra2.repositories;

import org.springframework.data.repository.CrudRepository;
import propra2.database.Customer;
import propra2.database.OrderProcess;

import java.util.List;
import java.util.Optional;

public interface OrderProcessRepository extends CrudRepository<OrderProcess, Long> {
    List<OrderProcess> findAll();
    List<OrderProcess> findAllByOwnerId(Long id);
    List<OrderProcess> findAllByRequestId(Long id);
    Optional<Customer> findByOwnerId(Long id);
    Optional<Customer> findByRequestId(Long id);
}
