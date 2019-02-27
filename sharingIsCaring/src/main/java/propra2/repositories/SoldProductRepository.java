package propra2.repositories;

import org.springframework.data.repository.CrudRepository;
import propra2.database.Product;

public interface SoldProductRepository extends CrudRepository<Product, Long> {

}
