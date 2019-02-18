package propra2.database;

import lombok.Data;
import propra2.model.Address;

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

	Integer deposit;
	Integer dailyFee;

	@ManyToOne
	Customer owner;

	@Lob
	@Embedded
	Address address;


	public boolean allValuesSet() {
		return this.getTitle() != null &&
				this.getDeposit() != null &&
				this.getDescription() != null &&
				//this.getBorrowedUntil() == null ||
				this.getOwner() != null ||
				this.getDailyFee() != null;
	}
	public Product() {
		this.address = new Address();
	}
}