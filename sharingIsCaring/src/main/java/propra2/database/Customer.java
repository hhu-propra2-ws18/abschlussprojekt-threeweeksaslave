package propra2.database;

import lombok.Data;
import org.springframework.web.reactive.function.client.WebClient;
import propra2.model.Address;
import propra2.model.ProPayAccount;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Data
@Table (name = "customer")
@Entity (name = "Customer")

public class Customer {

    @Id
    @GeneratedValue
    private Long customerId;

    private String role;
    private String username;
    private String password;
    private String mail;

    @Lob
    @Embedded
    private Address address;

    @Lob
    @Embedded
    private ProPayAccount proPay;

    @OneToMany
    private List<Product> borrowedProducts;

    void addBorrowedProduct(Product newProduct){
        borrowedProducts.add(newProduct);
    }

    public Customer() {
        this.address = new Address();
        this.proPay = new ProPayAccount();
    }

    public boolean hasEnoughMoney(double totalAmount) {
        return totalAmount <= proPay.getAvailableAmount();
    }
}
