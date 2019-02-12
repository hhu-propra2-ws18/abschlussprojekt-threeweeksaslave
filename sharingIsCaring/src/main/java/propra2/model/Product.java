package propra2.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "product")
@Entity (name = "Product")
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
	@JoinColumn(name = "customer_customerId")
	Customer owner;
}
