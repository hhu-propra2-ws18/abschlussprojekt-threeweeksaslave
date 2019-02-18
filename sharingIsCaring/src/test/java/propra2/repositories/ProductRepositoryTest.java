package propra2.repositories;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.database.Customer;
import propra2.database.Product;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProductRepositoryTest {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CustomerRepository customerRepository;

    @Test
    public void productRepositoryTestFindById() {
        Product product = new Product();
        product.setTitle("product");

        product = productRepository.save(product);

        Optional<Product> productOptional = productRepository.findById(product.getId());

        Assertions.assertThat(productOptional.get().getTitle()).isEqualTo("product");

        productRepository.delete(product);
    }

    @Test
    public void productRepositoryTestFindByOwner() {

    	Customer customer1 = new Customer();
    	customer1 = customerRepository.save(customer1);
    	Customer customer2 = new Customer();
    	customer2 = customerRepository.save(customer2);

        Product product1 = new Product();
        product1.setId(2L);
        product1.setTitle("product1");

        product1.setOwner(customer1);

        Product product2 = new Product();
        product2.setId(3L);
        product2.setTitle("product2");
        product2.setOwner(customer2);

        productRepository.save(product1);
        productRepository.save(product2);

        List<Product> products = productRepository.findByOwner(customer1);
        Assertions.assertThat(products.size()).isEqualTo(1);

        productRepository.delete(product1);
        productRepository.delete(product2);
    }

    @Test
    public void productRepositoryTestFindAllById() {

        Product product1 = new Product();
        product1.setTitle("product1");

        Product product2 = new Product();
        product2.setTitle("product2");

        Product product3 = new Product();
        product3.setTitle("product3");

        product1 = productRepository.save(product1);
        product2 = productRepository.save(product2);
        product3 = productRepository.save(product3);

        List<Long> ids = Arrays.asList(product1.getId(), product2.getId());

        List<Product> products = productRepository.findAllById(ids);
        Assertions.assertThat(products.size()).isEqualTo(2);

        productRepository.delete(product1);
        productRepository.delete(product2);
        productRepository.delete(product3);
    }
}
