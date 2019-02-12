package propra2.repositories;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.model.Product;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    public void productRepositoryTestFindById() {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("product");

        productRepository.save(product);

        Optional<Product> productOptional = productRepository.findById(1L);

        Assertions.assertThat(productOptional.get().getTitle()).isEqualTo("product");

        productRepository.delete(product);
    }
}
