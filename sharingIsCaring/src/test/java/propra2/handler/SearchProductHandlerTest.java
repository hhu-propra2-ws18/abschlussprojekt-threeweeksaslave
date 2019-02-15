package propra2.handler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.database.Customer;
import propra2.database.Product;
import propra2.repositories.ProductRepository;

import java.util.ArrayList;
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

	@Test
	public void findBorrowedByContainingString(){

		Product product1 = new Product();
		product1.setTitle("testProduct1");
		product1.setDescription("this is One product");

		Product product2 = new Product();
		product2.setTitle("testProduct2");
		product2.setDescription("this is Two product kappa");

		Product product3 = new Product();
		product3.setTitle("testProduct3");
		product3.setDescription("this is Three product kappa");

		Product product4 = new Product();
		product4.setTitle("testProduct2Unborrowed");
		product4.setDescription("this is unborrowed product kappa");

		product1 = productRepository.save(product1);
		product2 = productRepository.save(product2);
		product3 = productRepository.save(product3);
		product4 = productRepository.save(product4);

		List<Long> borrowedProductIds = new ArrayList<>();
		borrowedProductIds.add(product1.getId());
		borrowedProductIds.add(product2.getId());
		borrowedProductIds.add(product3.getId());

		Customer customer = new Customer();
		customer.setBorrowedProductIds(borrowedProductIds);


		List<Product> allProducts = searchProductHandler.getSearchedProducts("test", "borrowed", customer, productRepository);
		List<Product> productTitle1 = searchProductHandler.getSearchedProducts("1", "borrowed", customer, productRepository);
		List<Product> productDescriptionOne = searchProductHandler.getSearchedProducts("One", "borrowed", customer, productRepository);
		List<Product> kappaProducts = searchProductHandler.getSearchedProducts("kappa", "borrowed", customer, productRepository);

		Assert.assertEquals(3, allProducts.size());
		Assert.assertEquals(1 ,productTitle1.size());
		Assert.assertEquals(1, productDescriptionOne.size());
		Assert.assertEquals(2, kappaProducts.size());

	}

	@Test
	public void findLentByContainingString(){

		Customer customer = new Customer();
		Long id = 69L;
		customer.setCustomerId(id);

		Product product1 = new Product();
		product1.setTitle("testProduct1");
		product1.setDescription("this is One product");
		product1.setOwnerId(id);

		Product product2 = new Product();
		product2.setTitle("testProduct2");
		product2.setDescription("this is Two product kappa");
		product2.setOwnerId(id);

		Product product3 = new Product();
		product3.setTitle("testProduct3");
		product3.setDescription("this is Three product kappa");
		product3.setOwnerId(id);

		Product product4 = new Product();
		product4.setTitle("testProduct2Unlent");
		product4.setDescription("this is unlent product kappa");
		product4.setOwnerId(70L);

		product1 = productRepository.save(product1);
		product2 = productRepository.save(product2);
		product3 = productRepository.save(product3);
		product4 = productRepository.save(product4);

		List<Long> borrowedProductIds = new ArrayList<>();
		borrowedProductIds.add(product1.getId());
		borrowedProductIds.add(product2.getId());
		borrowedProductIds.add(product3.getId());

		List<Product> allProducts = searchProductHandler.getSearchedProducts("test", "lent", customer, productRepository);
		List<Product> productTitle1 = searchProductHandler.getSearchedProducts("1", "lent", customer, productRepository);
		List<Product> productDescriptionOne = searchProductHandler.getSearchedProducts("One", "lent", customer, productRepository);
		List<Product> kappaProducts = searchProductHandler.getSearchedProducts("kappa", "lent", customer, productRepository);

		Assert.assertEquals(3, allProducts.size());
		Assert.assertEquals(1 ,productTitle1.size());
		Assert.assertEquals(1, productDescriptionOne.size());
		Assert.assertEquals(2, kappaProducts.size());

	}


}
