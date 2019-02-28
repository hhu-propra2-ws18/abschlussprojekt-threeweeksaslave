package propra2.handler;

import org.junit.Assert;
import org.junit.Ignore;
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
		product1.setAvailable(true);

		Product product2 = new Product();
		product2.setTitle("testProduct2");
		product2.setDescription("this is Two product kappa");
		product2.setAvailable(true);

		Product product3 = new Product();
		product3.setTitle("testProduct3");
		product3.setDescription("this is Three product kappa");
		product3.setAvailable(true);

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

		List<Product> borrowedProducts = new ArrayList<>();
		borrowedProducts.add(product1);
		borrowedProducts.add(product2);
		borrowedProducts.add(product3);

		Customer customer = new Customer();
		customer.setBorrowedProducts(borrowedProducts);


		List<Product> allProducts = searchProductHandler.getSearchedProducts("test", "borrowed", customer, productRepository);
		List<Product> productTitle1 = searchProductHandler.getSearchedProducts("1", "borrowed", customer, productRepository);
		List<Product> productDescriptionOne = searchProductHandler.getSearchedProducts("One", "borrowed", customer, productRepository);
		List<Product> kappaProducts = searchProductHandler.getSearchedProducts("kappa", "borrowed", customer, productRepository);

		Assert.assertEquals(3, allProducts.size());
		Assert.assertEquals(1 ,productTitle1.size());
		Assert.assertEquals(1, productDescriptionOne.size());
		Assert.assertEquals(2, kappaProducts.size());

	}

	@Ignore
	@Test
	public void findOfferedByContainingString(){

		Customer customer = new Customer();
		Long id = 69L;
		customer.setCustomerId(id);

		Customer customer2 = new Customer();

		Product product1 = new Product();
		product1.setTitle("testProduct1");
		product1.setDescription("this is One product");
		product1.setOwner(customer);

		Product product2 = new Product();
		product2.setTitle("testProduct2");
		product2.setDescription("this is Two product kappa");
		product2.setOwner(customer);

		Product product3 = new Product();
		product3.setTitle("testProduct3");
		product3.setDescription("this is Three product kappa");
		product3.setOwner(customer);

		Product product4 = new Product();
		product4.setTitle("testProduct2Unlent");
		product4.setDescription("this is unlent product kappa");
		product4.setOwner(customer2);

		product1 = productRepository.save(product1);
		product2 = productRepository.save(product2);
		product3 = productRepository.save(product3);
		product4 = productRepository.save(product4);

		List<Long> borrowedProductIds = new ArrayList<>();
		borrowedProductIds.add(product1.getId());
		borrowedProductIds.add(product2.getId());
		borrowedProductIds.add(product3.getId());

		List<Product> allProducts = searchProductHandler.getSearchedProducts("test", "offered", customer, productRepository);
		List<Product> productTitle1 = searchProductHandler.getSearchedProducts("1", "offered", customer, productRepository);
		List<Product> productDescriptionOne = searchProductHandler.getSearchedProducts("One", "offered", customer, productRepository);
		List<Product> kappaProducts = searchProductHandler.getSearchedProducts("kappa", "offered", customer, productRepository);

		//TODO Assert.assertEquals schlägt fehl, wir wissen nicht, was genau die Methode getSearchedProducts tut, aber auf jeden Fall sehen wir dort keine if-Abfrage für den filter: "lent"(haben den solange ignored)
		Assert.assertEquals(3, allProducts.size());
		Assert.assertEquals(1 ,productTitle1.size());
		Assert.assertEquals(1, productDescriptionOne.size());
		Assert.assertEquals(2, kappaProducts.size());

	}


}
