package propra2.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Table (name = "customer")
@Entity (name = "Customer")
public class Customer {

    @Id
    @GeneratedValue
    private Long customerId;

    private String username;
    private String mail;
    private String proPay;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> productsToLend;


    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> borrowedProducts;

    public void addProductToLend(Product newProduct){
    	this.productsToLend.add(newProduct);
    }

}
