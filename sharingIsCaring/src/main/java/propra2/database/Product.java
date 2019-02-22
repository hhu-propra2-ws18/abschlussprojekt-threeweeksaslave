package propra2.database;

import lombok.Data;
import propra2.model.Address;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
				this.getOwner() != null ||
				this.getDailyFee() != null;
	}
	public Product() {
		this.address = new Address();
	}

	public double getTotalAmount(Date from) {
		double totalDailyFee = getTotalDailyFee(from);
		return totalDailyFee+deposit;
	}

	public double getTotalDailyFee(Date from){
		java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
		long days = ChronoUnit.DAYS.between(LocalDate.parse(from.toString()), LocalDate.parse(date.toString()));
		if(days<0) return 0;
		return (days+1)*dailyFee;
	}
}