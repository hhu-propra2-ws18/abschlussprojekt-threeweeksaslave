package propra2.handler;

import org.springframework.stereotype.Service;
import propra2.database.Customer;
import propra2.database.Product;
import propra2.repositories.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchProductHandler {

	public List<Product> getSearchedProducts(final String query, String filter,  Customer customer, ProductRepository productRepository){
		List<Product> products = productRepository.findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
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
		if(filter.equals("offered")){
			List<Product> userLentProducts = new ArrayList<>();
			for(Product product : products){
				if(product.getOwner().equals(customer)){
					userLentProducts.add(product);
				}
			}
			products = userLentProducts;
		}
		return products;
	}
}
