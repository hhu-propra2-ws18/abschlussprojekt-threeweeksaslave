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
    private String role;

    private List<Long> borrowedProductIds;

    void addBorrowedProduct(Product newProduct){
        borrowedProductIds.add(newProduct.getId());
    }

}
