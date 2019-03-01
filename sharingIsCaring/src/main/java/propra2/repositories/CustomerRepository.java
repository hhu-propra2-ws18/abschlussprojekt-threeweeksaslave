package propra2.repositories;

import org.springframework.data.repository.CrudRepository;
import propra2.database.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer,Long> {

    List<Customer> findAll();
    Optional<Customer> findByUsername(String username);
    Optional<Customer> findByMail(String mail);
    Optional<Customer> findById(Long id);
}
