package propra2.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Table (name = "user")
@Entity (name = "User")
public class User {

    @Id
    @GeneratedValue
    private Long customerId;

    private String username;
    private String mail;
    private String proPay;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> productsToLend;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> borrowedProducts;

    private List<Product> productsToLend;
    private List<Product> borrowedProducts;

    public void addProductToLend(Product newProduct){
    	this.productsToLend.add(newProduct);
    }

}
