package propra2.repositories;

import org.springframework.data.repository.CrudRepository;
import propra2.database.OrderProcess;

import java.util.List;

public interface OrderProcessRepository extends CrudRepository<OrderProcess, Long> {
    List<OrderProcess> findAll();
    List<OrderProcess> findAllByOwnerId(Long id);
    List<OrderProcess> findAllByRequestId(Long id);
}
