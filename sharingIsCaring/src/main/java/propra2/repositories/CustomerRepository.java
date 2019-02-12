package propra2.repositories;

import propra2.model.Customer;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.Lob;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer,Long> {

    List<Customer> findAll();
    Optional<Customer> findByUsername(String username);
}
