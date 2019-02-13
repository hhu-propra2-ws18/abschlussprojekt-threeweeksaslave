package propra2.repositories;

import org.springframework.data.repository.CrudRepository;
import propra2.database.Product;

import java.util.List;


public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findByTitle(String title);
    List<Product> findByOwnerId(Long id);
    List<Product> findAllById(Iterable<Long> id);
}
