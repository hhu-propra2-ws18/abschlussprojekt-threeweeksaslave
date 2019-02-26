package propra2.database;

import lombok.Data;
import propra2.model.Address;
import propra2.model.OrderProcessStatus;
import propra2.repositories.OrderProcessRepository;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "product")
@Entity (name = "Product")

public class Product {
	@GeneratedValue
	@Id
	Long id;

	String title;
	String description;

	boolean forSale;
	boolean available;
	Date borrowedUntil;

	Integer sellingPrice;
	Integer deposit;
	Integer dailyFee;

	@ManyToOne
	Customer owner;

	@Lob
	@Embedded
	Address address;


	public boolean allValuesSetRent() {
		return this.getTitle() != null &&
				this.getDeposit() != null &&
				this.getDescription() != null &&
				this.getOwner() != null ||
				this.getDailyFee() != null;
	}

	public boolean allValuesSetSale() {
		return this.getTitle() != null &&
				this.getSellingPrice() != null &&
				this.getDescription() != null &&
				this.getOwner() != null;
	}

	public Product() {
		this.address = new Address();
	}

	public double getExpectedTotalAmount(Date from, Date to) {
		double totalDailyFee = getExpectedTotalDailyFee(from, to);
		return totalDailyFee+deposit;
	}

	private double getExpectedTotalDailyFee(Date from, Date to) {
		long days = ChronoUnit.DAYS.between(LocalDate.parse(from.toString()), LocalDate.parse(to.toString()));
		if(days<0) return 0;
		return (days+1)*dailyFee;
	}

	public double getTotalDailyFee(Date from){
		java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
		long days = ChronoUnit.DAYS.between(LocalDate.parse(from.toString()), LocalDate.parse(date.toString()));
		if(days<0) return 0;
		return (days+1)*dailyFee;
	}

	public boolean isEditingAllowed(OrderProcessRepository orderProcessRepository){
		List<OrderProcess> orderProcesses = orderProcessRepository.findByProduct(this);
		for(OrderProcess orderProcess : orderProcesses){
			if(!(orderProcess.status == OrderProcessStatus.DENIED ||
					orderProcess.status == OrderProcessStatus.FINISHED ||
					orderProcess.status == OrderProcessStatus.PUNISHED)){
				return false;
			}
		}
		return true;
	}
}