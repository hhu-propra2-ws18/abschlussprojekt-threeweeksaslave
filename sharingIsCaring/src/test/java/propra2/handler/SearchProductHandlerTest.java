package propra2.handler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.database.Customer;
import propra2.database.Product;
import propra2.repositories.CustomerRepository;
import propra2.repositories.ProductRepository;

import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SearchProductHandlerTest {

	@Autowired
	ProductRepository productRepository;

	private SearchProductHandler searchProductHandler = new SearchProductHandler();

	@Test
	public void findAllByContainingString(){

		Product product1 = new Product();
		product1.setTitle("testProduct1");
		product1.setDescription("this is One product");

		Product product2 = new Product();
		product2.setTitle("testProduct2");
		product2.setDescription("this is Two product kappa");

		Product product3 = new Product();
		product3.setTitle("testProduct3");
		product3.setDescription("this is Three product kappa");

		productRepository.save(product1);
		productRepository.save(product2);
		productRepository.save(product3);

		List<Product> allProducts = searchProductHandler.getSearchedProducts("test", "all", null, productRepository);
		List<Product> productTitle1 = searchProductHandler.getSearchedProducts("1", "all", null, productRepository);
		List<Product> productDescriptionOne = searchProductHandler.getSearchedProducts("One", "all", null, productRepository);
		List<Product> kappaProducts = searchProductHandler.getSearchedProducts("kappa", "all", null, productRepository);

		Assert.assertEquals(3, allProducts.size());
		Assert.assertEquals(1 ,productTitle1.size());
		Assert.assertEquals(1, productDescriptionOne.size());
		Assert.assertEquals(2, kappaProducts.size());

	}

	

}
