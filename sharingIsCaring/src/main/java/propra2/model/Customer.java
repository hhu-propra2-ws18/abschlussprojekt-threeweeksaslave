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
    @Lob
    private ProPayAccount proPay;
    private String role;

    @Lob
    private List<Long> borrowedProductIds;

    void addBorrowedProduct(Product newProduct){
        borrowedProductIds.add(newProduct.getId());
    }

}
