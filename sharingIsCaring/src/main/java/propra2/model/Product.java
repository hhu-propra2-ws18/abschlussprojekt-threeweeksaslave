package propra2.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity (name = "Product")
@Table(name = "product")
@Data
public class Product {
	@GeneratedValue
	@Id
	Long id;

	String title;
	String description;

	boolean available;
	Date borrowedUntil;

	int deposit;
	int dailyFee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_customerId")
	Customer customer;
}
