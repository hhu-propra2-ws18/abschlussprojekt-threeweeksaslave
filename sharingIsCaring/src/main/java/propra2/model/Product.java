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

	Integer deposit;
	Integer dailyFee;

	Long ownerId;

	public boolean allValuesSet() {
		if(this.getTitle() == null||
		   this.getBorrowedUntil() == null ||
		   this.getId() == null ||
		   this.getBorrowedUntil() == null ||
		   this.getDailyFee() == null) {

			return false;
		}
		return true;

	}
}
