package propra2.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue
    private Long customerId;
    private String username;
    private String mail;
    private String proPay;
    private List<Product> productsToLend;
    private List<Product> borrowedProducts;

    public void addProductToLend(Product newProduct){
    	this.productsToLend.add(newProduct);
	}

}
