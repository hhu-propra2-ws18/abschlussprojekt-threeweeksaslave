package propra2.repositories;

import org.springframework.data.repository.CrudRepository;
import propra2.model.OrderProcess;

import java.util.List;

public interface OrderProcessRepository extends CrudRepository<OrderProcess, Long> {
    List<OrderProcess> findAll();
}
