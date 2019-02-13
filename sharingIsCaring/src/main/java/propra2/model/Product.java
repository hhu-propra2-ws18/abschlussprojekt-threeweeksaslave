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

	Integer deposit;
	Integer dailyFee;

	Long ownerId;

	public boolean allValuesSet() {
		if(this.getTitle() == null||
				this.getId() == null ||
				this.getDeposit() == null ||
				//this.getBorrowedUntil() == null ||
				this.getDailyFee() == null) {

			return false;
		}
		return true;
}
}