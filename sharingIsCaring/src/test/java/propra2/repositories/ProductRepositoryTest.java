package propra2.repositories;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.Controller.SharingIsCaringController;
import propra2.model.Customer;
import propra2.model.Product;

import java.util.List;
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

    @Test
    public void productRepositoryTestFindByOwnerId() {

        Customer owner = new Customer();
        owner.setCustomerId(1L);

        Product product1 = new Product();
        product1.setId(40L);
        product1.setTitle("product1");
        product1.setOwnerId(1L);

        Product product2 = new Product();
        product2.setId(70L);
        product2.setTitle("product2");
        product2.setOwnerId(90L);


        productRepository.save(product1);
        productRepository.save(product2);

        List<Product> products = productRepository.findByOwnerId(1L);
        Assertions.assertThat(products.size()).isEqualTo(1);
    }
}
