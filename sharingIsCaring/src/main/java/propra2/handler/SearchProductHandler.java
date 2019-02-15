package propra2.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import propra2.database.Customer;
import propra2.database.Product;
import propra2.repositories.CustomerRepository;
import propra2.repositories.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchProductHandler {

	public List<Product> getSearchedProducts(final String query, String filter,  Customer customer, ProductRepository productRepository){
		List<Product> products = productRepository.findAllByTitleContainingOrDescriptionContaining(query, query);
		if(filter.equals("borrowed")){
			List<Product> userBorrowedProducts = new ArrayList<>();
			List<Product> borrowedProducts = customer.getBorrowedProducts();
			for(Product product : products){
				for(Product borrowedProduct : borrowedProducts){
					if(product.getId().equals(borrowedProduct.getId())){
						userBorrowedProducts.add(product);
					}
				}
			}
			products = userBorrowedProducts;
		}
		if(filter.equals("lent")){
			Long customerId = customer.getCustomerId();
			List<Product> userLentProducts = new ArrayList<>();
			for(Product product : products){
				if(product.getOwnerId().equals(customerId)){
					userLentProducts.add(product);
				}
			}
			products = userLentProducts;
		}
		return products;
	}
}
