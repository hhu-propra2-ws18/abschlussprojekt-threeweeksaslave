package propra2.repositories;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.database.Product;
import reactor.test.StepVerifier;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SoldRepositoryTest {

    @Autowired
    SoldProductRepository soldProductRepository;

    @Test
    public void testFindById(){
        Product product = new Product();
        product.setDailyFee(10);

        product.setId(soldProductRepository.save(product).getId());
        Product product1 = soldProductRepository.findById(product.getId()).get();

        Assertions.assertThat(product1.getDailyFee()).isEqualTo(10);
    }
}
