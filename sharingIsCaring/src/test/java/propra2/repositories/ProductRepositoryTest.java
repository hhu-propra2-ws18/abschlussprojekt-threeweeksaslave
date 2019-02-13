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

import java.util.ArrayList;
import java.util.Arrays;
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
        product1.setId(2L);
        product1.setTitle("product1");
        product1.setOwnerId(1L);

        Product product2 = new Product();
        product2.setId(3L);
        product2.setTitle("product2");
        product2.setOwnerId(4L);

        productRepository.save(product1);
        productRepository.save(product2);

        List<Product> products = productRepository.findByOwnerId(1L);
        Assertions.assertThat(products.size()).isEqualTo(1);

        productRepository.delete(product1);
        productRepository.delete(product2);
    }

    @Test
    public void productRepositoryTestFindAllById() {

        Product product1 = new Product();
        product1.setId(20L);
        product1.setTitle("product1");

        Product product2 = new Product();
        product2.setId(60L);
        product2.setTitle("product2");

        Product product3 = new Product();
        product3.setId(90L);
        product3.setTitle("product3");

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        List<Long> ids = Arrays.asList(20L, 90L);

        List<Product> products = productRepository.findAllById(ids);
        Assertions.assertThat(products.size()).isEqualTo(2);

        productRepository.delete(product1);
        productRepository.delete(product2);
        productRepository.delete(product3);
    }
}
